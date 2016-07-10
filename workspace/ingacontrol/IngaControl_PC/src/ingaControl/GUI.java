package ingaControl;

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



public class GUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7466782193912627641L;
	private final static byte[] speeds = {
		IngaControl.SPEED_LOW, IngaControl.SPEED_MID, IngaControl.SPEED_HIGH, IngaControl.SPEED_MAX
	};
	private final static byte[] rv_speeds = {
		IngaControl.SPEED_RV_LOW, IngaControl.SPEED_RV_MID, IngaControl.SPEED_RV_HIGH, IngaControl.SPEED_RV_MAX
	};

	private JPanel contentPane;
	private IngaControl ic;
	private JComboBox comboBox;

	private final int forwardLeft = KeyEvent.VK_Q;
	private final int forwardRight = KeyEvent.VK_E;
	private final int backwardLeft = KeyEvent.VK_A;
	private final int backwardRight = KeyEvent.VK_D;

	private byte left, right;
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
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				ic.exit(); 
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.addKeyListener(new myKeyListener());
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] { "Slow", "Mid", "Fast", "Max" }));
		comboBox.setBounds(12, 13, 66, 22);
		comboBox.addKeyListener(new myKeyListener());
		contentPane.add(comboBox);

		ic = new IngaControl();

		short vendor = 0x0403;
		short product = 0x6001;

		Device inga = ic.findDevice(vendor, product);
		DeviceHandle handle = ic.getHandle(inga);
		ic.claimInterface(handle, 0);
		
		ic.spawnListener();
	}

	private class myKeyListener implements KeyListener {
		Set<Integer> pressedKeys = new TreeSet<Integer>();


		@Override
		public void keyPressed(KeyEvent arg0) {
			int code = arg0.getKeyCode();
			if (pressedKeys.contains(code)) {
				return;
			} else {
				switch (code) {
					case forwardLeft:
						left = speeds[comboBox.getSelectedIndex()];
						ic.sendCommand(target, left, right);
						break;
					case forwardRight:
						right = speeds[comboBox.getSelectedIndex()];
						ic.sendCommand(target, left, right);
						break;
					case backwardLeft:
						left = rv_speeds[comboBox.getSelectedIndex()];
						ic.sendCommand(target, left, right);
						break;
					case backwardRight:
						right = rv_speeds[comboBox.getSelectedIndex()];
						ic.sendCommand(target, left, right);
						break;
					default:
						break;
				}
			}
			pressedKeys.add(code);
		}


		@Override
		public void keyReleased(KeyEvent arg0) {
			pressedKeys.remove(arg0.getKeyCode());
			if ((arg0.getKeyCode() == forwardLeft) || (arg0.getKeyCode() == backwardLeft)) {
				left =  IngaControl.SPEED_STOP;
			} else if ((arg0.getKeyCode() == forwardRight) || (arg0.getKeyCode() == backwardRight)) {
				right = IngaControl.SPEED_STOP;
			} else {
				return;
			}
			ic.sendCommand(target, left, right);
		}


		@Override
		public void keyTyped(KeyEvent e) {
		}

	}
}
