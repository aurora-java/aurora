package aurora.service.exception;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.sql.DataSource;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.core.IGlobalInstance;
import uncertain.core.UncertainEngine;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.exception.IExceptionListener;
import uncertain.exception.IExceptionWithContext;
import uncertain.ocm.IConfigurable;
import aurora.database.DBUtil;
import aurora.service.ServiceThreadLocal;

/*
 *         配置文件样例
 <exception-database-log xmlns="aurora.service.exception">
	 <!-- 定义哪些异常是可以忽略的 -->
	 <ignored-types>
		 <ignored-type name="uncertain.core.ConfigurationError" />
		 <ignored-type name="uncertain.exception.GeneralException" />
	 </ignored-types>
	
	 <!-- 定义创建异常记录的SQL语句 -->
	 <insert-sql>
		 begin
		 insert into fnd_exception_logs ( id,exception_type, exception_message,source ,context ,root_stack_trace,full_stack_trace)
		 values (fnd_exception_logs_s.nextval, ?, ?, ?, ?,?,? );
		 end;
	 </insert-sql>
 </exception-database-log>	
 * 
 */
public class ExceptionDatabaseLog implements IExceptionListener, IGlobalInstance,IConfigurable{

	private IgnoredType[] ignoredTypes;
	private UncertainEngine mUncertainEngine;
	private String sql;
	private CompositeMap sqlNode;
	public static final String INSERT_SQL="insert-sql";
	public ExceptionDatabaseLog(UncertainEngine engine) {
		mUncertainEngine = engine;
	}

	public void addIgnoredTypes(IgnoredType[] ignoredTypes) {
		this.ignoredTypes = ignoredTypes;
	}

	public void setInsertSql(CompositeMap sqlNode) {
		this.sqlNode = sqlNode;
		sql = sqlNode.getText();
		if (sql == null || "".equals(sql)) {
			throw BuiltinExceptionFactory.createCDATAMissing(sqlNode.asLocatable(), INSERT_SQL);
		}
	}

	public CompositeMap getInsertSql() {
		return sqlNode;
	}

	public void onException(Throwable exception) {
		if (exception == null)
			return;
		if (ignoredTypes != null) {
			for (int i = 0; i < ignoredTypes.length; i++) {
				if (exception.getClass().getCanonicalName().equals(ignoredTypes[i].getName())) {
					return;
				}
			}
		}
		DataSource ds = (DataSource) mUncertainEngine.getObjectRegistry().getInstanceOfType(DataSource.class);
		if(ds == null ){
			System.err.println("Can not get DataSource instance from uncertainEngine. Please check config file.");
			return;
		}
		Connection conn = null;
		PreparedStatement ps = null;
		CompositeMap contextCM = getContext(exception);
		String context = null;
		if(contextCM != null){
			sql = parseSQL(sql, contextCM);
			context = contextCM.toXML();
		}else{
			sql = parseSQL(sql, new CompositeMap());
		}
		try {
			conn = ds.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, exception.getClass().getCanonicalName());
			ps.setString(2, exception.getMessage());
			ps.setString(3, getSource());
			ps.setString(4, context);
			String rootStackTrace = getRootStackTrace(exception);
			ps.setString(5, rootStackTrace);
			String fullStackTrace = getFullStackTrace(exception);
			ps.setString(6, fullStackTrace);
			ps.executeUpdate();
			ps.close();
			conn.commit();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeStatement(ps);
			DBUtil.closeConnection(conn);
		}
	}

	private String getSource() {
		if (ServiceThreadLocal.getSource() != null)
			return ServiceThreadLocal.getSource();
		return "";
	}

	private CompositeMap getContext(Throwable exception) {
		CompositeMap context;
		if (exception instanceof IExceptionWithContext) {
			IExceptionWithContext e = (IExceptionWithContext) exception;
			context = e.getExceptionContext();
			if (context != null) {
				return context;
			}
		}
		context = ServiceThreadLocal.getCurrentThreadContext();
		if (context != null)
			return context;
		return null;
	}

	private String getRootStackTrace(Throwable exception) {
		Throwable rootCause = getRootCause(exception);
		return getExceptionStackTrace(rootCause);

	}

	private String getFullStackTrace(Throwable exception) {
		return getExceptionStackTrace(exception);

	}

	private String getExceptionStackTrace(Throwable exception) {
		if (exception == null)
			return null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream pw = new PrintStream(baos);
		exception.printStackTrace(pw);
		pw.close();
		return baos.toString();

	}

	private Throwable getRootCause(Throwable exception) {
		if (exception == null)
			return exception;
		Throwable cause = exception.getCause();
		if (cause == null)
			return exception;
		return getRootCause(cause);
	}

	public void beginConfigure(CompositeMap config) {
		if(config == null){
			throw new RuntimeException("ExceptionDatabaseLog conifg can not be null!");
		}
		if(config.getChild(INSERT_SQL)==null){
			throw BuiltinExceptionFactory.createNodeMissing(config.asLocatable(), INSERT_SQL);
		}
	}
	public String parseSQL(String sql,final CompositeMap context){
		return TextParser.parse(sql, context);
	}
	public void endConfigure() {
	}
}
