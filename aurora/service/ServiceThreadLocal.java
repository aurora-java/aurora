/*
 * Created on 2011-3-26 上午12:10:37
 * $Id$
 */
package aurora.service;

import uncertain.composite.CompositeMap;

public class ServiceThreadLocal {

	private static ThreadLocal mThreadLocal = new ThreadLocal();
	private static ThreadLocal sourceThreadLocal = new ThreadLocal();

	public static CompositeMap getCurrentThreadContext() {
		return (CompositeMap) mThreadLocal.get();
	}

	public static void setCurrentThreadContext(CompositeMap context) {
		mThreadLocal.set(context);
	}

	public static void remove() {
		mThreadLocal.remove();
		sourceThreadLocal.remove();
	}

	public static String getSource() {
		return (String)sourceThreadLocal.get();
	}

	public static void setSource(String source) {
		sourceThreadLocal.set(source);
	}
}
