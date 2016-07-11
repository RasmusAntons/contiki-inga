package ingaControl;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.usb4java.Device;
import org.usb4java.DeviceHandle;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JButton;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.Font;
import javax.swing.border.LineBorder;
import javax.swing.JLabel;
import java.awt.Window.Type;
import java.awt.Frame;



public class GUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7466782193912627641L;
	private JPanel contentPane;
	private IngaControl ic;
	private JSpinner spinner;
	private JButton btnClickHere;
	private JComboBox comboBox;

	private final int forwardLeft = KeyEvent.VK_Q;
	private final int forwardRight = KeyEvent.VK_E;
	private final int backwardLeft = KeyEvent.VK_A;
	private final int backwardRight = KeyEvent.VK_D;
	private final int incSpeed = KeyEvent.VK_1;
	private final int decSpeed = KeyEvent.VK_2;
	private final int forward = KeyEvent.VK_UP;
	private final int backward = KeyEvent.VK_DOWN;
	private final int left = KeyEvent.VK_LEFT;
	private final int right = KeyEvent.VK_RIGHT;

	private byte leftValue, rightValue;
	private short target = (short) 0x04c3;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI frame = new GUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	/**
	 * Create the frame.
	 */
	public GUI() {
		setResizable(false);
		setTitle("IngaControl");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				ic.exit(); 
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 331);
		contentPane = new JPanel();
		//contentPane.addKeyListener(new myKeyListener());
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(50, 0, 100, 5));
		spinner.setBounds(123, 25, 59, 22);
		contentPane.add(spinner);
		
		btnClickHere = new JButton("Click here");
		btnClickHere.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		btnClickHere.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnClickHere.setFocusTraversalKeysEnabled(false);
		btnClickHere.setFocusPainted(false);
		btnClickHere.setBackground(new Color(240,90,90));
		btnClickHere.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				btnClickHere.setText("Ready for userinput");
				btnClickHere.setBackground(new Color(90,240,90));
				//button.setForeground(new Color(90,240,90));
			}
			
			@Override
			public void focusLost(FocusEvent e) {
				btnClickHere.setText("Click here");
				btnClickHere.setBackground(new Color(240,90,90));
				//button.setForeground(new Color(240,90,90));
			}
		});
		btnClickHere.setBounds(0, 67, 444, 229);
		btnClickHere.addKeyListener(new myKeyListener());
		contentPane.add(btnClickHere);
		
		comboBox = new JComboBox();
		comboBox.setMaximumRowCount(18);
		comboBox.setModel(new DefaultComboBoxModel(new Short[] {0x04c3, 0x04f0}));
		comboBox.setBounds(12, 25, 78, 22);
		contentPane.add(comboBox);
		
		JLabel lblNodeId = new JLabel("Node ID");
		lblNodeId.setBounds(12, 3, 56, 16);
		contentPane.add(lblNodeId);
		
		JLabel lblSpeed = new JLabel("Speed (%)");
		lblSpeed.setBounds(123, 3, 71, 16);
		contentPane.add(lblSpeed);

		ic = new IngaControl();

		short vendor = 0x0403;
		short product = 0x6001;
		Device inga = ic.findDevice(vendor, product);
		DeviceHandle handle = ic.getHandle(inga);
		ic.claimInterface(handle, 0);
		
		ic.spawnListener();
		
		btnClickHere.grabFocus();
	}

	private class myKeyListener implements KeyListener {
		Set<Integer> pressedKeys = new TreeSet<Integer>();

		private void decreaseSpeed() {
			int value = (int) spinner.getValue()-5;
			spinner.setValue(value<0?0:value);

		}

		private void increaseSpeed() {
			int value = (int) spinner.getValue()+5;
			spinner.setValue(value>100?100:value);

		}

		private void arrowControl(byte setSpeed) {
			byte x = 0, y = 0;
			if (pressedKeys.contains(backward)) --x;
			if (pressedKeys.contains(forward)) ++x;
			if (pressedKeys.contains(left)) --y;
			if (pressedKeys.contains(right)) ++y;
			if (x == 0) {
				leftValue = (byte) (y * setSpeed);
				rightValue = (byte) (-y * setSpeed);
			} else {
				leftValue = (byte) ((x * setSpeed) / (y < 0 ? 2 : 1));
				rightValue = (byte) ((x * setSpeed) / (y > 0 ? 2 : 1));
			}
			ic.sendCommand(target, leftValue, rightValue);
		}

		@Override
		public void keyPressed(KeyEvent arg0) {
			int code = arg0.getKeyCode();
			if (pressedKeys.contains(code))
				return;
			pressedKeys.add(code);
			target = (short) comboBox.getSelectedItem();
			byte setSpeed = (byte) (((int) spinner.getValue() / 100.0) * IngaControl.SPEED_MAX);
			switch (code) {
				case forwardLeft:
					leftValue = setSpeed;
					ic.sendCommand(target, leftValue, rightValue);
					break;
				case forwardRight:
					rightValue = setSpeed;
					ic.sendCommand(target, leftValue, rightValue);
					break;
				case backwardLeft:
					leftValue = (byte) -setSpeed;
					ic.sendCommand(target, leftValue, rightValue);
					break;
				case backwardRight:
					rightValue = (byte) -setSpeed;
					ic.sendCommand(target, leftValue, rightValue);
					break;
				case forward:
				case backward:
				case left:
				case right:
					arrowControl(setSpeed);
					break;
				case incSpeed:
					increaseSpeed();
					break;
				case decSpeed:
					decreaseSpeed();
					break;
				default:
					break;
			}
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
			int code = arg0.getKeyCode();
			if (!pressedKeys.contains(code))
				return;
			pressedKeys.remove(code);
			target = (short) comboBox.getSelectedItem();
			byte setSpeed = (byte) (((int) spinner.getValue() / 100.0) * IngaControl.SPEED_MAX);
			switch(code) {
				case forwardLeft:
				case backwardLeft:
					leftValue = IngaControl.SPEED_STOP;
					ic.sendCommand(target, leftValue, rightValue);
					break;
				case forwardRight:
				case backwardRight:
					rightValue = IngaControl.SPEED_STOP;
					ic.sendCommand(target, leftValue, rightValue);
					break;
				case forward:
				case backward:
				case left:
				case right:
					arrowControl(setSpeed);
					break;
				default:
					break;
			}
		}
		
		public void addRobotID(short id) {
			comboBox.addItem(id);
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}

	}
}
