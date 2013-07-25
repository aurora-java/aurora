package aurora.datasource;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import uncertain.exception.BuiltinExceptionFactory;

public class JNDIDataSourceFactory implements IDataSourceFactory {

	public static final String WEBLOGIC_CONTAINER_NAME = "WEBLOGIC";
	public static final String TOMCAT_CONTAINER_NAME = "TOMCAT";

	public static final String DEFAULT_CONTAINER_NAME = WEBLOGIC_CONTAINER_NAME;

	public static final int DEFAULT_LISTENERPORT = 7001;// weblogic

	private String containerName;
	private NativeJdbcExtractor nativeJdbcExtractor;

	@Override
	public DataSource createDataSource(DatabaseConnection dbConfig) throws Exception {
		String jndi_name = dbConfig.getJndiName();
		if (jndi_name == null)
			throw BuiltinExceptionFactory.createAttributeMissing(null, "jndiName");
		containerName = dbConfig.getContainerName();
		if (containerName == null) {
			containerName = DEFAULT_CONTAINER_NAME;
		}
		if (TOMCAT_CONTAINER_NAME.equals(containerName)) {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			// Look up our data source
			DataSource ds = (DataSource) envCtx.lookup(jndi_name);
			return ds;
		} else if (WEBLOGIC_CONTAINER_NAME.equals(containerName)) {
			return createDataSourceInWeblogic(jndi_name, dbConfig);
		}
		throw new IllegalArgumentException("The Web Sever Container:" + containerName + " is not support!");

	}

	private DataSource createDataSourceInWeblogic(String jndi_name, DatabaseConnection dbConfig) throws Exception {
		Properties pros = new Properties();
		int listenerPort = dbConfig.getListenerPort() > 0 ? dbConfig.getListenerPort() : DEFAULT_LISTENERPORT;
		String provider_url = "t3://127.0.0.1:" + listenerPort;
		pros.put(Context.PROVIDER_URL, provider_url);
		pros.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
		Context ctx = new InitialContext(pros);
		DataSource ds = (DataSource) ctx.lookup(jndi_name);
		return ds;

	}

	@Override
	public void cleanDataSource(DataSource ds) {
		// not support;
	}

	@Override
	public Connection getNativeJdbcExtractor(Connection conn) throws Exception {
		if (conn == null)
			return null;
		if (nativeJdbcExtractor == null) {
			synchronized (nativeJdbcExtractor) {
				if (containerName == null)
					containerName = DEFAULT_CONTAINER_NAME;

				if (TOMCAT_CONTAINER_NAME.equals(containerName)) {
					nativeJdbcExtractor = new CommonsDbcpNativeJdbcExtractor();
				} else if (WEBLOGIC_CONTAINER_NAME.equals(containerName)) {
					nativeJdbcExtractor = new WebLogicNativeJdbcExtractor();
				}
				if (nativeJdbcExtractor == null)
					throw new IllegalArgumentException("The Web Sever Container:" + containerName + " is not support!");
			}
		}
		Connection nativeConnection = nativeJdbcExtractor.getNativeConnection(conn);
		return nativeConnection;
	}

	class CommonsDbcpNativeJdbcExtractor implements NativeJdbcExtractor {

		private static final String GET_INNERMOST_DELEGATE_METHOD_NAME = "getInnermostDelegate";

		/***
		 * Extracts the innermost delegate from the given Commons DBCP object.
		 * Falls back to the given object if no underlying object found.
		 * 
		 * @param obj
		 *            the Commons DBCP Connection/Statement/ResultSet
		 * @return the underlying native Connection/Statement/ResultSet
		 */
		private Object getInnermostDelegate(Object obj) throws SQLException {
			if (obj == null) {
				return null;
			}
			try {
				Class classToAnalyze = obj.getClass();
				while (!Modifier.isPublic(classToAnalyze.getModifiers())) {
					classToAnalyze = classToAnalyze.getSuperclass();
					if (classToAnalyze == null) {
						// No public provider class found -> fall back to given
						// object.
						return obj;
					}
				}
				Method getInnermostDelegate = classToAnalyze.getMethod(GET_INNERMOST_DELEGATE_METHOD_NAME, (Class[]) null);

				Object delegate = getInnermostDelegate.invoke(obj, (Object[]) null);
				// Object delegate =
				// ReflectionUtils.invokeJdbcMethod(getInnermostDelegate, obj);
				return (delegate != null ? delegate : obj);
			}

			catch (SecurityException ex) {
				throw new IllegalStateException("Commons DBCP getInnermostDelegate method is not accessible: " + ex);
			} catch (Exception ex) {
				return obj;
			}
		}

		public Connection getNativeConnection(Connection con) throws SQLException {
			return (Connection) getInnermostDelegate(con);
		}

	}

	class WebLogicNativeJdbcExtractor implements NativeJdbcExtractor {

		private static final String JDBC_EXTENSION_NAME = "weblogic.jdbc.extensions.WLConnection";

		private final Class jdbcExtensionClass;

		private final Method getVendorConnectionMethod;

		/**
		 * This constructor retrieves the WebLogic JDBC extension interface, so
		 * we can get the underlying vendor connection using reflection.
		 */
		public WebLogicNativeJdbcExtractor() {
			try {
				this.jdbcExtensionClass = getClass().getClassLoader().loadClass(JDBC_EXTENSION_NAME);
				this.getVendorConnectionMethod = this.jdbcExtensionClass.getMethod("getVendorConnection", (Class[]) null);
			} catch (Exception ex) {
				throw new RuntimeException("Couldn't initialize WebLogicNativeJdbcExtractor because WebLogic API classes are not available", ex);
			}
		}

		/**
		 * Retrieve the Connection via WebLogic's
		 * <code>getVendorConnection</code> method.
		 */
		public Connection getNativeConnection(Connection con) throws SQLException {
			if (this.jdbcExtensionClass.isAssignableFrom(con.getClass())) {
				try {
					return (Connection) this.getVendorConnectionMethod.invoke(con, (Object[]) null);
				} catch (Exception ex) {
					throw new RuntimeException("Could not invoke WebLogic's getVendorConnection method", ex);
				}
			}
			return con;
		}

	}

	interface NativeJdbcExtractor {

		/***
		 * Retrieve the underlying native JDBC Connection for the given
		 * Connection. Supposed to return the given Connection if not capable of
		 * unwrapping.
		 * 
		 * @param con
		 *            the Connection handle, potentially wrapped by a connection
		 *            pool
		 * @return the underlying native JDBC Connection, if possible; else, the
		 *         original Connection
		 * @throws SQLException
		 *             if thrown by JDBC methods
		 */
		Connection getNativeConnection(Connection con) throws SQLException;

	}
}
