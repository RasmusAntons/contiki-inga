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
	private JPanel contentPane;
	private IngaControl ic;
	private JComboBox comboBox;
	private HashMap<Integer, Byte> speeds = new HashMap<Integer, Byte>();
	private HashMap<Integer, Byte> rv_speeds = new HashMap<Integer, Byte>();
	private final int forwardLeft = KeyEvent.VK_Q;
	private final int forwardRight = KeyEvent.VK_E;
	private final int backwardLeft = KeyEvent.VK_A;
	private final int backwardRight = KeyEvent.VK_D;


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

		speeds.put(0, IngaControl.SPEED_LOW);
		speeds.put(1, IngaControl.SPEED_MID);
		speeds.put(2, IngaControl.SPEED_HIGH);
		speeds.put(3, IngaControl.SPEED_MAX);

		rv_speeds.put(0, IngaControl.SPEED_RV_LOW);
		rv_speeds.put(1, IngaControl.SPEED_RV_MID);
		rv_speeds.put(2, IngaControl.SPEED_RV_HIGH);
		rv_speeds.put(3, IngaControl.SPEED_RV_MAX);
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
					ic.sendCommand(IngaControl.LEFT, speeds.get(comboBox.getSelectedIndex()));
					break;
				case forwardRight:
					ic.sendCommand(IngaControl.RIGHT, speeds.get(comboBox.getSelectedIndex()));
					break;
				case backwardLeft:
					ic.sendCommand(IngaControl.LEFT, rv_speeds.get(comboBox.getSelectedIndex()));
					break;
				case backwardRight:
					ic.sendCommand(IngaControl.RIGHT, rv_speeds.get(comboBox.getSelectedIndex()));
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
				ic.sendCommand(IngaControl.LEFT, IngaControl.SPEED_STOP);
			}
			if ((arg0.getKeyCode() == forwardRight) || (arg0.getKeyCode() == backwardRight)) {
				ic.sendCommand(IngaControl.RIGHT, IngaControl.SPEED_STOP);
			}
		}


		@Override
		public void keyTyped(KeyEvent e) {
		}

	}
}
