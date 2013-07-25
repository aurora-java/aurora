package aurora.datasource.nativejdbc;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.SQLException;

public class CommonsDbcpNativeJdbcExtractor implements INativeJdbcExtractor {
	private static final String GET_INNERMOST_DELEGATE_METHOD_NAME = "getInnermostDelegate";

	/***
	 * Extracts the innermost delegate from the given Commons DBCP object. Falls
	 * back to the given object if no underlying object found.
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
			Method getInnermostDelegate = classToAnalyze.getMethod(
					GET_INNERMOST_DELEGATE_METHOD_NAME, (Class[]) null);

			Object delegate = getInnermostDelegate.invoke(obj, (Object[]) null);
			// Object delegate =
			// ReflectionUtils.invokeJdbcMethod(getInnermostDelegate, obj);
			return (delegate != null ? delegate : obj);
		}

		catch (SecurityException ex) {
			throw new IllegalStateException(
					"Commons DBCP getInnermostDelegate method is not accessible: "
							+ ex);
		} catch (Exception ex) {
			return obj;
		}
	}

	public Connection getNativeConnection(Connection con) throws SQLException {
		return (Connection) getInnermostDelegate(con);
	}
}
