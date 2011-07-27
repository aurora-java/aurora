package aurora.plugin.c3p0;

import java.lang.reflect.Method;
import java.sql.Connection;

import com.mchange.v2.c3p0.C3P0ProxyConnection;

public class C3P0NativeJdbcExtractor {
	private final Method getRawConnectionMethod;

	public C3P0NativeJdbcExtractor() {
		try {
			this.getRawConnectionMethod = getClass().getMethod(
					"getRawConnection", new Class[] { Connection.class });
		} catch (NoSuchMethodException ex) {
			throw new IllegalStateException(
					"Internal error in C3P0NativeJdbcExtractor: "
							+ ex.getMessage());
		}
	}

	public static Connection getRawConnection(Connection con) {
		return con;
	}

	public Connection getNativeConnection(Connection con) throws Exception {
		if (con instanceof C3P0ProxyConnection) {
			C3P0ProxyConnection cpCon = (C3P0ProxyConnection) con;
			return (Connection) cpCon.rawConnectionOperation(
					this.getRawConnectionMethod, null,
					new Object[] { C3P0ProxyConnection.RAW_CONNECTION });

		}
		return con;
	}
}
