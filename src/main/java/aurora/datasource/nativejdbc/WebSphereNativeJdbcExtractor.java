package aurora.datasource.nativejdbc;

import java.lang.reflect.Method;
import java.sql.Connection;

public class WebSphereNativeJdbcExtractor implements INativeJdbcExtractor {
	private static final String JDBC_ADAPTER_CONNECTION_NAME_5 = "com.ibm.ws.rsadapter.jdbc.WSJdbcConnection";

	private static final String JDBC_ADAPTER_UTIL_NAME_5 = "com.ibm.ws.rsadapter.jdbc.WSJdbcUtil";

	private static final String CONNECTION_PROXY_NAME_4 = "com.ibm.ejs.cm.proxy.ConnectionProxy";

	private Class webSphere5ConnectionClass;

	private Class webSphere4ConnectionClass;

	private Method webSphere5NativeConnectionMethod;

	private Method webSphere4PhysicalConnectionMethod;

	/**
	 * This constructor retrieves WebSphere JDBC adapter classes, so we can get
	 * the underlying vendor connection using reflection.
	 */
	public WebSphereNativeJdbcExtractor() {
		// Detect WebSphere 5 connection classes.
		try {
			this.webSphere5ConnectionClass = getClass().getClassLoader()
					.loadClass(JDBC_ADAPTER_CONNECTION_NAME_5);
			Class jdbcAdapterUtilClass = getClass().getClassLoader().loadClass(
					JDBC_ADAPTER_UTIL_NAME_5);
			this.webSphere5NativeConnectionMethod = jdbcAdapterUtilClass
					.getMethod("getNativeConnection",
							new Class[] { this.webSphere5ConnectionClass });
		} catch (Exception ex) {
			throw new RuntimeException(
					"Could not find WebSphere 5 connection pool classes", ex);
		}

		// Detect WebSphere 4 connection classes.
		// Might also be found on WebSphere 5, for version 4 DataSources.
		try {
			this.webSphere4ConnectionClass = getClass().getClassLoader()
					.loadClass(CONNECTION_PROXY_NAME_4);
			this.webSphere4PhysicalConnectionMethod = this.webSphere4ConnectionClass
					.getMethod("getPhysicalConnection", (Class[]) null);
		} catch (Exception ex) {
			throw new RuntimeException(
					"Could not find WebSphere 4 connection pool classes", ex);
		}
	}

	/**
	 * Return true, as WebSphere returns wrapped Statements.
	 */
	public boolean isNativeConnectionNecessaryForNativeStatements() {
		return true;
	}

	/**
	 * Return true, as WebSphere returns wrapped PreparedStatements.
	 */
	public boolean isNativeConnectionNecessaryForNativePreparedStatements() {
		return true;
	}

	/**
	 * Return true, as WebSphere returns wrapped CallableStatements.
	 */
	public boolean isNativeConnectionNecessaryForNativeCallableStatements() {
		return true;
	}

	/**
	 * Retrieve the Connection via WebSphere's <code>getNativeConnection</code>
	 * method.
	 */
	public Connection getNativeConnection(Connection con) {
		// WebSphere 5 connection?
		if (this.webSphere5ConnectionClass != null
				&& this.webSphere5ConnectionClass.isAssignableFrom(con
						.getClass())) {
			try {
				// WebSphere 5's
				// WSJdbcUtil.getNativeConnection(wsJdbcConnection)
				return (Connection) this.webSphere5NativeConnectionMethod
						.invoke(null, new Object[] { con });
			} catch (Exception ex) {
				throw new RuntimeException(
						"Could not invoke WebSphere5's getNativeConnection method",
						ex);
			}
		}

		// WebSphere 4 connection (or version 4 connection on WebSphere 5)?
		else if (this.webSphere4ConnectionClass != null
				&& this.webSphere4ConnectionClass.isAssignableFrom(con
						.getClass())) {
			try {
				// WebSphere 4's connectionProxy.getPhysicalConnection()
				return (Connection) this.webSphere4PhysicalConnectionMethod
						.invoke(con, (Object[]) null);
			} catch (Exception ex) {
				throw new RuntimeException(
						"Could not invoke WebSphere4's getPhysicalConnection method",
						ex);
			}
		}

		// No known WebSphere connection -> return as-is.
		else {
			throw new RuntimeException("Connection [" + con
					+ "] is not a WebSphere 5/4 connection, returning as-is.");
		}
	}
}
