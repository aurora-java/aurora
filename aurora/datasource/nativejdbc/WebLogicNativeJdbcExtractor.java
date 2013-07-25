package aurora.datasource.nativejdbc;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

public class WebLogicNativeJdbcExtractor implements INativeJdbcExtractor {
	private static final String JDBC_EXTENSION_NAME = "weblogic.jdbc.extensions.WLConnection";

	private final Class jdbcExtensionClass;

	private final Method getVendorConnectionMethod;

	/**
	 * This constructor retrieves the WebLogic JDBC extension interface, so we
	 * can get the underlying vendor connection using reflection.
	 */
	public WebLogicNativeJdbcExtractor() {
		try {
			this.jdbcExtensionClass = getClass().getClassLoader().loadClass(
					JDBC_EXTENSION_NAME);
			this.getVendorConnectionMethod = this.jdbcExtensionClass.getMethod(
					"getVendorConnection", (Class[]) null);
		} catch (Exception ex) {
			throw new RuntimeException(
					"Couldn't initialize WebLogicNativeJdbcExtractor because WebLogic API classes are not available",
					ex);
		}
	}

	/**
	 * Retrieve the Connection via WebLogic's <code>getVendorConnection</code>
	 * method.
	 */
	public Connection getNativeConnection(Connection con) throws SQLException {
		if (this.jdbcExtensionClass.isAssignableFrom(con.getClass())) {
			try {
				return (Connection) this.getVendorConnectionMethod.invoke(con,
						(Object[]) null);
			} catch (Exception ex) {
				throw new RuntimeException(
						"Could not invoke WebLogic's getVendorConnection method",
						ex);
			}
		}
		return con;
	}
}
