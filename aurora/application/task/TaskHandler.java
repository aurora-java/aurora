package aurora.application.task;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

import javax.sql.DataSource;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.core.ILifeCycle;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.IProcedureManager;
import uncertain.proc.Procedure;
import aurora.database.service.BusinessModelService;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.database.service.SqlServiceContext;
import aurora.service.IServiceFactory;
import aurora.service.ServiceInvoker;
import aurora.service.ServiceThreadLocal;

public class TaskHandler extends AbstractLocatableObject implements ILifeCycle {

	private IObjectRegistry mRegistry;

	private String queryTaskBM;
	private String finishTaskBM;
	private int tryTime = 10;
	private IDatabaseServiceFactory databaseServiceFactory;
	private DataSource dataSource;
	private IProcedureManager procedureManager;
	private IServiceFactory serviceFactory;

	private ILogger logger;
	private boolean running = true;
	private Thread taskThread;

	public TaskHandler(IObjectRegistry registry) {
		this.mRegistry = registry;
	}

	public void onInitialize() {
		logger = LoggingContext.getLogger(this.getClass().getCanonicalName(), mRegistry);
		if (queryTaskBM == null)
			throw BuiltinExceptionFactory.createAttributeMissing(this, "queryTaskBM");
		if (finishTaskBM == null)
			throw BuiltinExceptionFactory.createAttributeMissing(this, "finishTaskBM");
		dataSource = (DataSource) mRegistry.getInstanceOfType(DataSource.class);
		if (dataSource == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(this, DataSource.class, this.getClass().getName());
		databaseServiceFactory = (IDatabaseServiceFactory) mRegistry.getInstanceOfType(IDatabaseServiceFactory.class);
		if (databaseServiceFactory == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(this, IDatabaseServiceFactory.class, this.getClass().getName());
		procedureManager = (IProcedureManager) mRegistry.getInstanceOfType(IProcedureManager.class);
		if (procedureManager == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(this, IProcedureManager.class, this.getClass().getName());
		serviceFactory = (IServiceFactory) mRegistry.getInstanceOfType(IServiceFactory.class);
		if (serviceFactory == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(this, IServiceFactory.class, this.getClass().getName());
		taskThread = new Thread() {
			public void run() {
				try {
					loopRun();
				} catch (Exception e) {
					logger.log(Level.SEVERE, "", e);
				}
			}
		};
		taskThread.start();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					shutdown();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void loopRun() throws Exception {
		int failedCount = 0;
		while (running) {
			CompositeMap task = new CompositeMap();
			CompositeMap context = new CompositeMap();
			try{
				executeBM(queryTaskBM, context, task);
				if (task == null || task.isEmpty()) {
					Thread.sleep(1000);
					continue;
				}
				Object task_id = task.get(TaskTableFields.TASK_ID);
				if (task_id == null || "null".equals(task_id)) {
					Thread.sleep(1000);
					continue;
				}
				String strContext = task.getString(TaskTableFields.CONTEXT);
				if (strContext != null && !"".equals(strContext)) {
					context = new CompositeLoader().loadFromString(strContext);
					clearInstance(context);
				}
				ServiceThreadLocal.setCurrentThreadContext(context);
			}catch (Throwable e) {
				logger.log(Level.SEVERE, "", e);
				failedCount++;
				if(failedCount>tryTime)
					break;
				else
					continue;
			}
			failedCount = 0;
			CompositeMap parameter = (CompositeMap) task.clone();
			try {
				executeTask(task, parameter);
			} catch (Throwable e) {
				parameter.put(TaskTableFields.EXCEPTION, getFullStackTrace(e));
			}
			try {
				executeBM(finishTaskBM, context, parameter);
			} catch (Throwable e) {
				logger.log(Level.SEVERE, "", e);
			}
			ServiceThreadLocal.remove();
		}
	}

	public void executeTask(CompositeMap task, CompositeMap parameter) throws Exception {
		CompositeLoader loader = new CompositeLoader();
		CompositeMap context = ServiceThreadLocal.getCurrentThreadContext();
		int task_id = task.getInt(TaskTableFields.TASK_ID);
		String task_type = task.getString(TaskTableFields.TASK_TYPE);
		String proc_file_path = task.getString(TaskTableFields.PROC_FILE_PATH);
		String proc_content = task.getString(TaskTableFields.PROC_CONTENT);
		String sql = task.getString(TaskTableFields.SQL);
		if (task_id == 0)
			throw BuiltinExceptionFactory.createAttributeMissing(null, TaskTableFields.TASK_ID);
		if (TaskTableFields.JAVA_TYPE.equals(task_type)) {
			if (proc_file_path != null && !proc_file_path.equals("")) {
				executeProc(proc_file_path,task_id,context);
			} else {
				if (proc_content == null || "".equals(proc_content))
					throw BuiltinExceptionFactory.createOneAttributeMissing(null, TaskTableFields.PROC_FILE_PATH + "," + TaskTableFields.PROC_CONTENT);
				CompositeMap procedure_config = loader.loadFromString(proc_content);
				executeProc(procedure_config,task_id,context);
			}
		} else if (TaskTableFields.PROCEDURE_TYPE.equals(task_type)) {
			if (sql == null || "".equals(sql))
				throw BuiltinExceptionFactory.createAttributeMissing(null, TaskTableFields.SQL);
			execDbProc(context, sql);
		} else if (TaskTableFields.FUNCTION_TYPE.equals(task_type)) {
			if (sql == null || "".equals(sql))
				throw BuiltinExceptionFactory.createAttributeMissing(null, TaskTableFields.SQL);
			execDbFun(context, sql);
		} else {
			throw new IllegalArgumentException("The " + task_type + " is not supported!");
		}
	}
	
	private void clearInstance(CompositeMap context){
		if(context == null)
			return;
		Iterator it = context.entrySet().iterator();
		if(it == null)
			return ;
        while(it.hasNext()){
            Map.Entry entry = (Map.Entry)it.next();
            Object key = entry.getKey();
            if(key.toString().startsWith("_")){
            	it.remove();
            }
        }
	}

	public void executeBM(String bm_name, CompositeMap context, CompositeMap parameterMap) throws Exception {
		CompositeMap localContext = context;
		if (localContext == null)
			localContext = new CompositeMap();
		SqlServiceContext sqlContext = SqlServiceContext.createSqlServiceContext(localContext);
		if(sqlContext == null)
			throw new RuntimeException("Can not create SqlServiceContext for context:"+localContext.toXML());
		Connection connection = getConnection();
		connection.setAutoCommit(false);
		sqlContext.setConnection(connection);
		try {
			BusinessModelService service = databaseServiceFactory.getModelService(bm_name, localContext);
			service.execute(parameterMap);
			connection.commit();
		} catch (Exception ex) {
			rollbackConnection(connection);
			throw new RuntimeException(ex);
		} finally {
			if (sqlContext != null)
				sqlContext.freeConnection();
		}
	}

	protected void executeProc(String procedure_name,int taskId,CompositeMap context) {
		logger.log(Level.CONFIG, "load procedure:{0}", new Object[] { procedure_name });
		Procedure proc = null;
		try {
			proc = procedureManager.loadProcedure(procedure_name);
		} catch (Exception ex) {
			throw BuiltinExceptionFactory.createResourceLoadException(this, procedure_name, ex);
		}
		executeProc(taskId,proc,context);
	}

	protected void executeProc(CompositeMap procedure_config,int taskId,CompositeMap context) {
		logger.log(Level.CONFIG, "load procedure:{0}", new Object[] { procedure_config });
		Procedure proc = null;
		try {
			proc = procedureManager.createProcedure(procedure_config);
		} catch (Exception ex) {
			throw BuiltinExceptionFactory.createResourceLoadException(this, String.valueOf(taskId), ex);
		}
		executeProc(taskId,proc,context);
	}
	
	protected void executeProc(int taskId, Procedure proc, CompositeMap context) {
		if(proc == null)
			throw new IllegalArgumentException("Procedure can not be null!");
		try {
			logger.log(Level.CONFIG, "load procedure:{0}", new Object[] { proc.getName()});
			String name = "task." + taskId;
			if (context != null){
				context.putObject("/parameter/@task_id", taskId,true);
				ServiceInvoker.invokeProcedureWithTransaction(name, proc, serviceFactory, context);
			}
			else {
				ServiceInvoker.invokeProcedureWithTransaction(name, proc, serviceFactory);
			}
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Error when invoking procedure " + proc.getName(), ex);
			throw new RuntimeException(ex);
		}
	}

	private String execDbFun(CompositeMap context, String function) throws Exception {
		SqlServiceContext sqlContext = SqlServiceContext.createSqlServiceContext(context);
		if(sqlContext == null)
			throw new RuntimeException("Can not create SqlServiceContext for context:"+context.toXML());
		Connection connection = getConnection();
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
			if (sqlContext != null)
				sqlContext.freeConnection();
		}
		return errorMessage;

	}

	private void execDbProc(CompositeMap context, String executePkg) throws Exception {
		SqlServiceContext sqlContext = SqlServiceContext.createSqlServiceContext(context);
		if(sqlContext == null)
			throw new RuntimeException("Can not create SqlServiceContext for context:"+context.toXML());
		Connection connection = getConnection();
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
			if (sqlContext != null)
				sqlContext.freeConnection();
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

	public String getQueryTaskBM() {
		return queryTaskBM;
	}

	public void setQueryTaskBM(String queryTaskBM) {
		this.queryTaskBM = queryTaskBM;
	}

	public String getFinishTaskBM() {
		return finishTaskBM;
	}

	public void setFinishTaskBM(String finishTaskBM) {
		this.finishTaskBM = finishTaskBM;
	}

	public int getTryTime() {
		return tryTime;
	}

	public void setTryTime(int tryTime) {
		this.tryTime = tryTime;
	}

	private Connection getConnection() {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
		if (connection == null)
			throw new IllegalStateException("Can't get database connection from dataSource.");
		return connection;
	}
	private String getFullStackTrace(Throwable exception) {
		String message = getExceptionStackTrace(exception);
		if (message.length() > 4000)
			message = message.substring(0, 4000);
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

	@Override
	public boolean startup() {
		return true;
	}

	@Override
	public void shutdown() {
		running = false;
		if (taskThread != null && taskThread.isAlive())
			taskThread.interrupt();
		taskThread = null;
		
	}
	
}
