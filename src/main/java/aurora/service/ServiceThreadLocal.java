/*
 * Created on 2011-3-26 上午12:10:37
 * $Id$
 */
package aurora.service;

import uncertain.composite.CompositeMap;

public class ServiceThreadLocal {

	private static ThreadLocal mThreadLocal = new ThreadLocal();
	private static ThreadLocal sourceThreadLocal = new ThreadLocal();
	private static ThreadLocal useTransactionManager = new ThreadLocal();

	public static CompositeMap getCurrentThreadContext() {
		return (CompositeMap) mThreadLocal.get();
	}

	public static void setCurrentThreadContext(CompositeMap context) {
		mThreadLocal.set(context);
	}

	public static void remove() {
		mThreadLocal.remove();
		sourceThreadLocal.remove();
		useTransactionManager.remove();
	}

	public static String getSource() {
		return (String)sourceThreadLocal.get();
	}

	public static void setSource(String source) {
		sourceThreadLocal.set(source);
	}
	
	public static Boolean getUseTransactionManager() {
		return (Boolean)useTransactionManager.get();
	}

	public static void setUseTransactionManager(Boolean transactionManager) {
		useTransactionManager.set(transactionManager);
	}
}
