package ingaControl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.usb4java.BufferUtils;
import org.usb4java.DeviceHandle;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;
import org.usb4java.Transfer;
import org.usb4java.TransferCallback;

public class inputListener implements Runnable {
	DeviceHandle handle;
	static final byte IN_ENDPOINT = (byte) 0x81;
	private static final long TIMEOUT = 5000;
	private GUI gui;

	public inputListener(DeviceHandle handle, GUI g) {
		super();
		this.handle = handle;
		this.gui = g;
	}

	static byte[] trim(byte[] bytes) {

		int i = bytes.length - 1;
		while (i >= 0 && bytes[i] == 0) {
			--i;
		}

		return Arrays.copyOf(bytes, i + 1);
	}

	@Override
	public void run() {
		int size = 64;
		while (true) {
			ByteBuffer in = read(handle, size);

			byte[] bytes = new byte[size];
			in.get(bytes, 0, size);

			byte[] bytescut = new byte[bytes.length];

			System.arraycopy(bytes, 2, bytescut, 0, size - 2);

			bytescut = trim(bytescut);

			if (bytescut.length != 0) {
				System.out.print(new String(bytescut, Charset.forName("UTF-8")));

				// System.out.println(Arrays.toString(bytescut));

				/*
				if (bytescut[0] == -34) {
					String v = new String(bytescut, Charset.forName("UTF-8")).substring(1);
					System.out.println("Received new RobotID: " + v);
					gui.addRobotID(Short.parseShort(v));
				}
				*/

				for (int i = 0; i < bytescut.length - 2; ++i) {
					if (bytescut[i] == (byte) 243)
						continue;
					short id = (short) (bytescut[++i] << 8 | bytescut[++i]);
					System.out.println("Received new RobotID: " + id);
					gui.addRobotID(id);
				}

			}
		}
	}

	/**
	 * Reads some data from the device.
	 * 
	 * @param handle
	 *            The device handle.
	 * @param size
	 *            The number of bytes to read from the device.
	 * @return The read data.
	 */

	public static ByteBuffer read(DeviceHandle handle, int size) {
		ByteBuffer buffer = BufferUtils.allocateByteBuffer(size).order(ByteOrder.LITTLE_ENDIAN);
		IntBuffer transferred = BufferUtils.allocateIntBuffer();
		int result = LibUsb.bulkTransfer(handle, IN_ENDPOINT, buffer, transferred, TIMEOUT);
		if (result != LibUsb.SUCCESS) {
			throw new LibUsbException("Unable to read data", result);
		}
		int bytesread = transferred.get();
		// if (bytesread != 2)
		// System.out.println("bytesread " + bytesread);
		// System.out.println(transferred.get() + " bytes read from device");
		return buffer;
	}

}
