package ingaControl;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

public class IngaControl {
	DeviceHandle handle;
	Context context;
	int interfaceNumber;
	boolean detach;
	inputListener listener;
	Thread listenerThread;
	static final byte LEFT = 0x6C;
	static final byte RIGHT = 0x72;

	// 0 stop
	// 1-128 rückwärts
	// 129-256 forwärts

	static final byte SPEED_STOP = 0;
	static final byte SPEED_RV_LOW = -32;
	static final byte SPEED_RV_MID = -64;
	static final byte SPEED_RV_HIGH = -96;
	static final byte SPEED_RV_MAX = -127;
	static final byte SPEED_LOW = 32;
	static final byte SPEED_MID = 64;
	static final byte SPEED_HIGH = 96;
	static final byte SPEED_MAX = 127;

	public static void main(String[] args) {

		IngaControl ic = new IngaControl();

		short vendor = 0x0403;
		short product = 0x6001;

		Device inga = ic.findDevice(vendor, product);
		DeviceHandle handle = ic.getHandle(inga);
		ic.claimInterface(handle, 0);
		ic.spawnListener();

		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ic.exit();
	}

	public void sendCommand(short receiver, byte left, byte right) {
		System.out.println(String.format("receiver = 0x%04x, left = %d, right = %d", receiver, left, right));
		ByteBuffer buffer = ByteBuffer.allocateDirect(5);
		buffer.put(new byte[] { (byte) 243, (byte) (receiver >>> 8), (byte) receiver, left, right });
		senBytesEndpoint(handle, 10000, buffer, (byte) 0x02);
	}
	

	public void spawnListener() {
		listener = new inputListener(handle);
		listenerThread = new Thread(listener);
		listenerThread.start();
	}

	public void sendBytes(DeviceHandle handle, int timeout, ByteBuffer buffer) {

		int transfered = LibUsb.controlTransfer(handle, (byte) (LibUsb.REQUEST_TYPE_CLASS | LibUsb.RECIPIENT_INTERFACE),
				(byte) 0x09, (short) 256, (short) 1, buffer, timeout);
		if (transfered < 0)
			throw new LibUsbException("Control transfer failed", transfered);
		System.out.println(transfered + " bytes sent");

	}

	public void senBytesEndpoint(DeviceHandle handle, int timeout, ByteBuffer buffer, byte endpoint) {
		IntBuffer transfered = IntBuffer.allocate(1);
		int result = LibUsb.bulkTransfer(handle, endpoint, buffer, transfered, timeout);
		if (result != LibUsb.SUCCESS)
			throw new LibUsbException("Control transfer failed", result);
		System.out.println(transfered.get() + " bytes sent");
	}

	public void claimInterface(DeviceHandle handle, int interfaceNumber) {
		this.interfaceNumber = interfaceNumber;

		// Check if kernel driver must be detached
		this.detach = (LibUsb.kernelDriverActive(handle, interfaceNumber) == 1);
		System.out.println("detach " + detach);

		// Detach the kernel driver
		if (detach) {
			int result = LibUsb.detachKernelDriver(handle, interfaceNumber);
			if (result != LibUsb.SUCCESS)
				throw new LibUsbException("Unable to detach kernel driver", result);
		}

		int result = LibUsb.claimInterface(handle, interfaceNumber);
		if (result != LibUsb.SUCCESS)
			throw new LibUsbException("Unable to claim interface", result);
	}

	public DeviceHandle getHandle(Device device) {
		this.handle = new DeviceHandle();
		int result = LibUsb.open(device, handle);
		if (result != LibUsb.SUCCESS)
			throw new LibUsbException("Unable to open USB device", result);
		return handle;
	}

	public void closeHandle(DeviceHandle handle) {
		LibUsb.close(handle);
	}

	public IngaControl() {
		super();
		this.context = new Context();
		int result = LibUsb.init(context);
		if (result != LibUsb.SUCCESS)
			throw new LibUsbException("Unable to initialize libusb.", result);
	}

	public void exit() {
		System.out.println("Exiting now");
		if(listenerThread != null)
			listenerThread.stop();
		int result = LibUsb.releaseInterface(handle, interfaceNumber);
		if (result != LibUsb.SUCCESS)
			throw new LibUsbException("Unable to release interface", result);

		// Attach the kernel driver again if needed
		if (detach) {
			result = LibUsb.attachKernelDriver(handle, interfaceNumber);
			if (result != LibUsb.SUCCESS)
				throw new LibUsbException("Unable to re-attach kernel driver", result);
		}

		LibUsb.close(handle);
		LibUsb.exit(context);
	}

	public Device findDevice(short vendorId, short productId) {
		// Read the USB device list
		DeviceList list = new DeviceList();
		int result = LibUsb.getDeviceList(null, list);
		if (result < 0)
			throw new LibUsbException("Unable to get device list", result);

		try {
			// Iterate over all devices and scan for the right one
			DeviceDescriptor descriptor;
			for (Device device : list) {
				descriptor = new DeviceDescriptor();
				result = LibUsb.getDeviceDescriptor(device, descriptor);
				// System.out.println(descriptor.toString());
				if (result != LibUsb.SUCCESS)
					throw new LibUsbException("Unable to read device descriptor", result);
				if (descriptor.idVendor() == vendorId && descriptor.idProduct() == productId) {
					System.out.println("Found device:\n" + descriptor.toString());
					return device;
				}
			}
		} finally {
			// Ensure the allocated device list is freed
			LibUsb.freeDeviceList(list, true);
		}

		// Device not found
		return null;
	}

}
