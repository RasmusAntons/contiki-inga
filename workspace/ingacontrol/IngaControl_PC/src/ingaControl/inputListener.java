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

	public inputListener(DeviceHandle handle) {
		super();
		this.handle = handle;
	}

	@Override
	public void run() {
		int size = 64;
		while (true) {
			// System.out.println("while");
			ByteBuffer in = read(handle, size);

			byte[] bytes = new byte[size];
			in.get(bytes, 0, size);

			byte[] bytescut = new byte[size];

			System.arraycopy(bytes, 2, bytescut, 0, size - 2);

			// System.out.println(Arrays.toString(bytescut));

			int sum = 0;
			for (byte b : bytescut) {
				sum += b;
				// if(sum!=0) return;
			}

			if (sum != 0) {
				// System.out.println(Arrays.toString(bytescut));
				String v = new String(bytescut, Charset.forName("UTF-8"));
				System.out.println(v);
			}

			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
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
		//if (bytesread != 2)
			//System.out.println("bytesread " + bytesread);
		// System.out.println(transferred.get() + " bytes read from device");
		return buffer;
	}

}
