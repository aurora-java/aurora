package aurora.application.task;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.ILogger;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.IProcedureManager;
import uncertain.proc.Procedure;
import aurora.database.FetchDescriptor;
import aurora.database.service.BusinessModelService;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.database.service.SqlServiceContext;
import aurora.service.IServiceFactory;
import aurora.service.ServiceInvoker;
import aurora.service.ServiceThreadLocal;

public class TaskUtil {
	
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private ILogger logger;
	private IObjectRegistry mRegistry;
	
	private IDatabaseServiceFactory databaseServiceFactory;
	private IProcedureManager procedureManager;
	private IServiceFactory serviceFactory;
	
	public TaskUtil(ILogger logger,IObjectRegistry registry){
		this.logger = logger;
		this.mRegistry = registry;
		databaseServiceFactory = (IDatabaseServiceFactory) mRegistry.getInstanceOfType(IDatabaseServiceFactory.class);
		if (databaseServiceFactory == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(null, IDatabaseServiceFactory.class, this.getClass().getName());
		procedureManager = (IProcedureManager) mRegistry.getInstanceOfType(IProcedureManager.class);
		if (procedureManager == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(null, IProcedureManager.class, this.getClass().getName());
		serviceFactory = (IServiceFactory) mRegistry.getInstanceOfType(IServiceFactory.class);
		if (serviceFactory == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(null, IServiceFactory.class, this.getClass().getName());
	}

	public void executeTask(Connection connection, CompositeMap task) throws Exception {
		CompositeMap context = getContext(task);
		if (context == null)
			context = new CompositeMap();
		ServiceThreadLocal.setCurrentThreadContext(context);

		int task_id = task.getInt(TaskTableFields.TASK_ID);
		if (task_id == 0)
			throw BuiltinExceptionFactory.createAttributeMissing(null, TaskTableFields.TASK_ID);

		String task_type = task.getString(TaskTableFields.TASK_TYPE);
		String proc_file_path = task.getString(TaskTableFields.PROC_FILE_PATH);
		CompositeMap proc_content = getProcContext(task);
		String sql = task.getString(TaskTableFields.SQL);

		if (TaskTableFields.JAVA_TYPE.equals(task_type)) {
			if (proc_file_path != null && !proc_file_path.equals("")) {
				executeProc(proc_file_path, task_id, context, connection);
			} else {
				if (proc_content == null)
					throw BuiltinExceptionFactory.createOneAttributeMissing(null, TaskTableFields.PROC_FILE_PATH + "," + TaskTableFields.PROC_CONTENT);
				executeProc(proc_content, task_id, context, connection);
			}
		} else if (TaskTableFields.PROCEDURE_TYPE.equals(task_type)) {
			if (sql == null || "".equals(sql))
				throw BuiltinExceptionFactory.createAttributeMissing(null, TaskTableFields.SQL);
			execDbProc(connection, context, sql);
		} else if (TaskTableFields.FUNCTION_TYPE.equals(task_type)) {
			if (sql == null || "".equals(sql))
				throw BuiltinExceptionFactory.createAttributeMissing(null, TaskTableFields.SQL);
			execDbFun(connection, context, sql);
		} else {
			throw new IllegalArgumentException("The " + task_type + " is not supported!");
		}
	}
	
	public CompositeMap getContext(CompositeMap taskRecord) throws Exception {
		if (taskRecord == null)
			return null;
		Object context = taskRecord.get(TaskTableFields.CONTEXT);
		if (context == null)
			return null;
		String strContext = null;
		if (context instanceof Clob) {
			strContext = clobToString((Clob) context);
		} else {
			strContext = context.toString();
		}
		return loadFromString(strContext);
	}
	
	private String clobToString(Clob clob) throws Exception {
		Reader inStreamDoc = clob.getCharacterStream();
		char[] tempDoc = new char[(int) clob.length()];
		inStreamDoc.read(tempDoc);
		inStreamDoc.close();
		return new String(tempDoc);
	}
	
	private CompositeMap loadFromString(String content) throws Exception {
		if (content == null)
			return null;
		CompositeMap context = null;
		if (content != null && !"".equals(content)) {
			context = new CompositeLoader().loadFromString(content, "UTF-8");
			clearInstance(context);
		}
		return context;
	}
	
	private void clearInstance(CompositeMap context) {
		if (context == null)
			return;
		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<Object, Object>> it = context.entrySet().iterator();
		if (it == null)
			return;
		while (it.hasNext()) {
			it.next();
			it.remove();
		}
	}
	
	private CompositeMap getProcContext(CompositeMap taskRecord) throws Exception {
		if (taskRecord == null)
			return null;
		Object proc_content = taskRecord.get(TaskTableFields.PROC_CONTENT);
		if (proc_content == null)
			return null;
		String str_Proc_content = null;
		if (proc_content instanceof Clob) {
			str_Proc_content = clobToString((Clob) proc_content);
		} else {
			str_Proc_content = proc_content.toString();
		}
		return loadFromString(str_Proc_content);
	}
	
	public CompositeMap queryBM(Connection connection, String bm_name, CompositeMap context, CompositeMap parameterMap) throws Exception {
		CompositeMap localContext = context;
		if (localContext == null)
			localContext = new CompositeMap();
		SqlServiceContext sqlContext = SqlServiceContext.createSqlServiceContext(localContext);
		if (sqlContext == null)
			throw new RuntimeException("Can not create SqlServiceContext for context:" + localContext.toXML());
		sqlContext.setConnection(connection);
		// try {
		BusinessModelService service = databaseServiceFactory.getModelService(bm_name, localContext);
		CompositeMap resultMap = service.queryAsMap(parameterMap, FetchDescriptor.fetchAll());
		return resultMap;
		// }
		// finally {
		// if (sqlContext != null)
		// sqlContext.freeConnection();
		// }
	}

	public void executeBM(Connection connection, String bm_name, CompositeMap context, CompositeMap parameterMap) throws Exception {
		CompositeMap localContext = context;
		if (localContext == null)
			localContext = new CompositeMap();
		SqlServiceContext sqlContext = SqlServiceContext.createSqlServiceContext(localContext);
		if (sqlContext == null)
			throw new RuntimeException("Can not create SqlServiceContext for context:" + localContext.toXML());
		// Connection connection = getConnection();
		connection.setAutoCommit(false);
		sqlContext.setConnection(connection);
		try {
			BusinessModelService service = databaseServiceFactory.getModelService(bm_name, localContext);
			service.execute(parameterMap);
			connection.commit();
		} catch (Exception ex) {
			rollbackConnection(connection);
			throw new RuntimeException(ex);
		}
		// finally {
		// if (sqlContext != null)
		// sqlContext.freeConnection();
		// }
	}

	public void executeProc(String procedure_name, int taskId, CompositeMap context, Connection connection) {
		logger.log(Level.CONFIG, "load procedure:{0}", new Object[] { procedure_name });
		Procedure proc = procedureManager.loadProcedure(procedure_name);
		executeProc(taskId, proc, context, connection);
	}

	protected void executeProc(CompositeMap procedure_config, int taskId, CompositeMap context, Connection connection) {
		logger.log(Level.CONFIG, "load procedure:{0}", new Object[] { procedure_config.toXML() });
		Procedure proc = null;
		proc = procedureManager.createProcedure(procedure_config);
		executeProc(taskId, proc, context, connection);
	}

	protected void executeProc(int taskId, Procedure proc, CompositeMap context, Connection connection) {
		if (proc == null)
			throw new IllegalArgumentException("Procedure can not be null!");
		try {
			String name = "task." + taskId;
			if (context != null) {
				context.putObject("/parameter/@task_id", taskId, true);
				ServiceInvoker.invokeProcedureWithTransaction(name, proc, serviceFactory, context);
				// ServiceInvoker.invokeProcedureWithTransaction(name, proc,
				// serviceFactory, context,connection);
			} else {
				ServiceInvoker.invokeProcedureWithTransaction(name, proc, serviceFactory);
				// ServiceInvoker.invokeProcedureWithTransaction(name, proc,
				// serviceFactory, connection);
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private String execDbFun(Connection connection, CompositeMap context, String function) throws Exception {
		SqlServiceContext sqlContext = SqlServiceContext.createSqlServiceContext(context);
		if (sqlContext == null)
			throw new RuntimeException("Can not create SqlServiceContext for context:" + context.toXML());
		// Connection connection = getConnection();
		sqlContext.setConnection(connection);
		String errorMessage = null;
		CallableStatement proc = null;
		try {
			connection.setAutoCommit(false);
			proc = connection.prepareCall("{call ? := " + function + "}");
			proc.registerOutParameter(1, Types.VARCHAR);
			proc.execute();
			errorMessage = proc.getString(1);
			if (errorMessage == null || "".equals(errorMessage)) {
				connection.commit();
			} else {
				connection.rollback();
			}
			proc.close();
		} catch (Exception e) {
			rollbackConnection(connection);
			throw new RuntimeException(e);
		} finally {
			closeStatement(proc);
			// if (sqlContext != null)
			// sqlContext.freeConnection();
		}
		return errorMessage;

	}

	private void execDbProc(Connection connection, CompositeMap context, String executePkg) throws Exception {
		SqlServiceContext sqlContext = SqlServiceContext.createSqlServiceContext(context);
		if (sqlContext == null)
			throw new RuntimeException("Can not create SqlServiceContext for context:" + context.toXML());
		// Connection connection = getConnection();
		sqlContext.setConnection(connection);
		CallableStatement proc = null;
		try {
			connection.setAutoCommit(false);
			proc = connection.prepareCall("{call " + executePkg + "}");
			proc.execute();
			connection.commit();
			proc.close();
			connection.setAutoCommit(true);
		} catch (Exception e) {
			rollbackConnection(connection);
			throw new RuntimeException(e);
		} finally {
			closeStatement(proc);
			// if (sqlContext != null)
			// sqlContext.freeConnection();
		}
	}

	private void rollbackConnection(Connection dbConn) {
		if (dbConn == null)
			return;
		try {
			dbConn.rollback();
		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "", ex);
		}
	}

	private void closeStatement(Statement stmt) {
		if (stmt == null)
			return;
		try {
			stmt.close();
		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "", ex);
		}
	}

	public void closeConnection(Connection conn) {
		if (conn == null)
			return;
		try {
			conn.close();
		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "", ex);
		}
	}
	
	public String getFullStackTrace(Throwable exception) {
		String message = getExceptionStackTrace(exception);
		return message;
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
	
	public int getTaskId(CompositeMap taskRecord) {
		if (taskRecord == null)
			return -1;
		return taskRecord.getInt(TaskTableFields.TASK_ID, -1);
	}

}
