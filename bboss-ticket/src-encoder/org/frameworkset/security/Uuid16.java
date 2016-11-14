package org.frameworkset.security;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class Uuid16 implements Serializable {

	private static final long serialVersionUID = -2669965391494374578L;

	private static int UUID_HOST_LOCK_PORT = 5504;

	// private static final int MAX_RETRYS = 1200;
	// private static final int INTERVAL_TIME = 100;
	private static ServerSocket lockSocket;

	private static long timeStamp;

	private static long adapterAddress;

	private static int instanceCounter;

	// private static final long versionMask = 4096L;
	// private static final long reserveMask = 0xe000000000000000L;
	// private static final long randomMask = 0x1fffffffL;
	private static final char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
			'e', 'f' };

	private synchronized static void acquireHostLock() throws Exception {
		String portProperty = null;
		try {
			portProperty = System.getProperty("bluewater.uuid.hostLockPort");
		} catch (SecurityException securityexception) {
		}
		if (portProperty != null)
			try {
				UUID_HOST_LOCK_PORT = Integer.parseInt(portProperty);
			} catch (NumberFormatException numberformatexception) {
			}
		for (int numberOfRetrys = 0; lockSocket == null; numberOfRetrys++) {
			try {
				lockSocket = new ServerSocket(UUID_HOST_LOCK_PORT);
				return;
			} catch (BindException bindexception) {
			} catch (IOException e2) {
				throw new Exception("Unique identifier unexpected failure");
			}
			try {
				Thread.sleep(100L);
			} catch (InterruptedException interruptedexception) {
			}
			if (numberOfRetrys == 1200)
				throw new Exception("Unique identifier lock failure");
		}

	}

	public static synchronized Uuid16 create() {
		try {
			if (timeStamp == 0L)
				setTimeStamp();
			if (adapterAddress == 0L)
				setAdapterAddress();
			Uuid16 uuid = new Uuid16();
			long midTime = timeStamp >> 32 & 0xffffffffL;
			uuid.high = timeStamp << 32 | midTime << 16 & 0xffff0000L | 4096L | timeStamp >> 48 & 4095L;
			int count = instanceCounter++;
			if (count == 0x1fffffff) {
				instanceCounter = 0;
				setTimeStamp();
			}
			uuid.low = (count & 0x1fffffffL) << 32 | 0xe000000000000000L | adapterAddress;
			return uuid;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void letClockTick(long curTime) throws Exception {
		int timeoutCounter = 0;
		long sleepTime = 1L;
		for (long newTime = System.currentTimeMillis(); newTime == curTime; newTime = System.currentTimeMillis()) {
			timeoutCounter++;
			sleepTime *= 2L;
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException interruptedexception) {
			}
			if (sleepTime > 60000L)
				throw new Exception("Unique identifier unexpected failure");
		}

	}

	public static void main(String args[]) {
//		try {
//			long begin = System.currentTimeMillis();
//			for (int i = 0; i < 30; i++) {
//				Uuid16 uuid = create();
//				Out.println("aaaaa-----" + uuid.toString().length());
//			}
//
//			long end = System.currentTimeMillis();
//			Out.println("total=" + (end - begin) + "ms," + (end - begin) / 1000L + " second");
//		} catch (Exception exception) {
//		}
		for(int i=0;i<100;i++)
			System.out.println(create());
	}

	public static Uuid16 read(DataInput in) throws IOException {
		long high = in.readLong();
		long low = in.readLong();
		return new Uuid16(high, low);
	}

	public static Uuid16 read(String id) throws Exception {
		String part = id.substring(0, 8);
		long high = 0L;
		high = Long.parseLong(part, 16) << 32;
		part = id.substring(9, 13);
		high |= Long.parseLong(part, 16) << 16;
		part = id.substring(14, 18);
		high |= Long.parseLong(part, 16);
		long low = 0L;
		part = id.substring(19, 23);
		low = Long.parseLong(part, 16) << 48;
		part = id.substring(24, 36);
		low |= Long.parseLong(part, 16);
		Uuid16 uuid = new Uuid16(high, low);
		return uuid;
	}

	private static void releaseHostLock() {
		if (lockSocket != null) {
			try {
				lockSocket.close();
			} catch (IOException ioexception) {
			}
			lockSocket = null;
		}
	}

	private static void setAdapterAddress() throws Exception {
		try {
			byte addr[] = InetAddress.getLocalHost().getAddress();
			int raw = addr[3] & 0xff | addr[2] << 8 & 0xff00 | addr[1] << 16 & 0xff0000 | addr[0] << 24 & 0xff000000;
			adapterAddress = raw & 0xffffffffL;
		} catch (UnknownHostException e) {
			throw new Exception("Unexpected failure");
		}
	}

	private static void setTimeStamp() throws Exception {
		acquireHostLock();
		try {
			long newTime = System.currentTimeMillis();
			if (timeStamp != 0L) {
				if (newTime < timeStamp)
					throw new Exception("Unique identifier clock failure");
				if (newTime == timeStamp) {
					letClockTick(newTime);
					newTime = System.currentTimeMillis();
				}
			}
			timeStamp = newTime;
		} finally {
			releaseHostLock();
		}
		return;
	}

	private static String toHexString(long x, int chars) {
		char buf[] = new char[chars];
		for (int charPos = chars; --charPos >= 0;) {
			buf[charPos] = hexDigits[(int) (x & 15L)];
			x >>>= 4;
		}

		return new String(buf);
	}

	//	a2355dbf000000ac
	private long high;

	private long low;
private transient String str36;
	private Uuid16() {
		str36 = null;
	}
	private Uuid16(long high, long low) {
		str36 = null;
		this.high = high;
		this.low = low;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj != null && (obj instanceof Uuid16))
			return high == ((Uuid16) obj).high && low == ((Uuid16) obj).low;
		else
			return false;
	}
	@Override
	public int hashCode() {
		return (int) (low << 24) & 0xff000000 | (int) (high >> 20) & 0xfff000 | (int) (low >> 32) & 0xfff;
	}
	public byte[] toByteArray() {
		byte array[] = new byte[16];
		toBytes(high, array, 0);
		toBytes(low, array, 8);
		return array;
	}
	private void toBytes(long x, byte array[], int startPos) {
		for (int bytePos = 8; --bytePos >= 0;) {
			array[startPos + bytePos] = (byte) (int) (x & 255L);
			x >>>= 8;
		}

	}
	@Override
	public String toString() {
		if (str36 != null) {
			return str36;
		} else {
			StringBuffer buf = new StringBuffer();
			buf.append(toHexString(high >>> 32, 8));
			// buf.append(toHexString(high >>> 16, 4));
			// buf.append(toHexString(high, 4));
			// buf.append(toHexString(low >>> 48, 4));
			buf.append(toHexString(low >>> 24, 8));
			str36 = buf.toString();

			return str36;
		}
	}
	public void write(DataOutput out) throws IOException {
		out.writeLong(high);
		out.writeLong(low);
	}

}
