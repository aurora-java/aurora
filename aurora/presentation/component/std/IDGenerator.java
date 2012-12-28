package aurora.presentation.component.std;

import java.net.InetAddress;
import java.util.UUID;

/**
 * ID生成器
 * 
 * @version $Id$
 * @author <a href="mailto:njq.niu@hand-china.com">znjq</a>
 */
public class IDGenerator {
	
	public static final String VERSION = "$Revision$";

	// Singleton
	private static final IDGenerator instance = new IDGenerator();

	public static IDGenerator getInstance() {
		return instance;
	}

//	private static final int IP;
//	static {
//		int ipadd;
//		try {
//			ipadd = BytesHelper.toInt(InetAddress.getLocalHost().getAddress());
//		} catch (Exception e) {
//			ipadd = 0;
//		}
//		IP = ipadd;
//	}
//
//	private static String sep = "";
//
//	private static short counter = (short) 0;
//
//	private static final int JVM = (int) (System.currentTimeMillis() >>> 8);
//
//	private IDGenerator() {
//	}
//
//	/**
//	 * Unique across JVMs on this machine (unless they load this class in the
//	 * same quater second - very unlikely)
//	 */
//	private int getJVM() {
//		return JVM;
//	}
//
//	/**
//	 * Unique in a millisecond for this JVM instance (unless there are >
//	 * Short.MAX_VALUE instances created in a millisecond)
//	 */
//	private short getCount() {
//		// Need not synchronized if singleton
//		// synchronized(IDGenerator.class) {
//		if (counter < 0)
//			counter = 0;
//		return counter++;
//		// }
//	}
//
//	/**
//	 * Unique in a local network
//	 */
//	private int getIP() {
//		return IP;
//	}
//
//	/**
//	 * Unique down to millisecond
//	 */
//	private short getHiTime() {
//		return (short) (System.currentTimeMillis() >>> 32);
//	}
//
//	private int getLoTime() {
//		return (int) System.currentTimeMillis();
//	}
//
//	private String format(int intval) {
//		String formatted = Integer.toHexString(intval);
//		StringBuffer buf = new StringBuffer("00000000");
//		buf.replace(8 - formatted.length(), 8, formatted);
//		return buf.toString();
//	}
//
//	private String format(short shortval) {
//		String formatted = Integer.toHexString(shortval);
//		StringBuffer buf = new StringBuffer("0000");
//		buf.replace(4 - formatted.length(), 4, formatted);
//		return buf.toString();
//	}

//	//@SuppressWarnings("unused")
//	private static String toString(int value) {
//		return new String(BytesHelper.toBytes(value));
//	}
//
//	//@SuppressWarnings("unused")
//	private static String toString(short value) {
//		return new String(BytesHelper.toBytes(value));
//	}

	public String generate() {
		return UUID.randomUUID().toString().replace("-", "").toUpperCase();
//		return new StringBuffer(36).append(format(getIP())).append(sep).append(format(getJVM())).append(sep).append(format(getHiTime())).append(sep).append(format(getLoTime())).append(sep).append(format(getCount())).toString();
	}

}
