package aurora.application.task;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
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
import aurora.application.features.msg.IConsumer;
import aurora.application.features.msg.IMessage;
import aurora.application.features.msg.IMessageListener;
import aurora.application.features.msg.IMessageStub;
import aurora.application.features.msg.INoticerConsumer;
import aurora.database.FetchDescriptor;
import aurora.database.service.BusinessModelService;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.database.service.SqlServiceContext;
import aurora.service.IServiceFactory;
import aurora.service.ServiceInvoker;
import aurora.service.ServiceThreadLocal;

public class TaskHandler extends AbstractLocatableObject implements ILifeCycle, IMessageListener {

	public static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private IObjectRegistry mRegistry;

	private String oldTaskBM;
	private String fetchTaskBM;
	private String updateTaskBM;
	private String finishTaskBM;
	private int threadCount = 2;

	private IDatabaseServiceFactory databaseServiceFactory;
	private DataSource dataSource;
	private IProcedureManager procedureManager;
	private IServiceFactory serviceFactory;

	private ILogger logger;
	private boolean running = true;

	private Queue<CompositeMap> taskQueue = new ConcurrentLinkedQueue<CompositeMap>();
	private ExecutorService mainThreadPool;
	private HandleTask handleTask;
	protected String topic = "task";
	protected String message = "task_message";
	private Queue<Number> taskIdList = new ConcurrentLinkedQueue<Number>();
    private Queue<Connection> connectionQueue = new ConcurrentLinkedQueue<Connection>();
	public TaskHandler(IObjectRegistry registry) {
		this.mRegistry = registry;
	}

	public void onInitialize() {
		logger = LoggingContext.getLogger(this.getClass().getCanonicalName(), mRegistry);
		if (fetchTaskBM == null)
			throw BuiltinExceptionFactory.createAttributeMissing(this, "fetchTaskBM");
		if (updateTaskBM == null)
			throw BuiltinExceptionFactory.createAttributeMissing(this, "updateTaskBM");
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

		IMessageStub stub = (IMessageStub) mRegistry.getInstanceOfType(IMessageStub.class);
		if (stub == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(this, IMessageStub.class, this.getClass().getName());
		if (!stub.isStarted())
			logger.warning("JMS MessageStub is not started, please check the configuration.");
		IConsumer consumer = stub.getConsumer(topic);
		if (consumer == null) {
			throw new IllegalStateException("MessageStub does not define the topic '" + topic + "', please check the configuration.");
		}
		if (!(consumer instanceof INoticerConsumer))
			throw BuiltinExceptionFactory.createInstanceTypeWrongException(this.getOriginSource(), INoticerConsumer.class, IConsumer.class);
		((INoticerConsumer) consumer).addListener(message, this);

		mainThreadPool = Executors.newFixedThreadPool(2);
		GetTask getTask = new GetTask();
		handleTask = new HandleTask(threadCount);
		mainThreadPool.submit(getTask);
		mainThreadPool.submit(handleTask);

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

	public void executeTask(Connection connection,CompositeMap task, CompositeMap parameter) throws Exception {
		CompositeMap context = getContext(task);
		if (context == null)
			context = new CompositeMap();
		ServiceThreadLocal.setCurrentThreadContext(context);

		int task_id = task.getInt(TaskTableFields.TASK_ID);
		if (task_id == 0)
			throw BuiltinExceptionFactory.createAttributeMissing(null, TaskTableFields.TASK_ID);

		String task_type = task.getString(TaskTableFields.TASK_TYPE);
		String proc_file_path = task.getString(TaskTableFields.PROC_FILE_PATH);
		String proc_content = task.getString(TaskTableFields.PROC_CONTENT);
		String sql = task.getString(TaskTableFields.SQL);

		if (TaskTableFields.JAVA_TYPE.equals(task_type)) {
			if (proc_file_path != null && !proc_file_path.equals("")) {
				executeProc(proc_file_path, task_id, context);
			} else {
				if (proc_content == null || "".equals(proc_content))
					throw BuiltinExceptionFactory.createOneAttributeMissing(null, TaskTableFields.PROC_FILE_PATH + ","
							+ TaskTableFields.PROC_CONTENT);
				CompositeMap procedure_config = loadFromString(proc_content);
				executeProc(procedure_config, task_id, context);
			}
		} else if (TaskTableFields.PROCEDURE_TYPE.equals(task_type)) {
			if (sql == null || "".equals(sql))
				throw BuiltinExceptionFactory.createAttributeMissing(null, TaskTableFields.SQL);
			execDbProc(connection,context, sql);
		} else if (TaskTableFields.FUNCTION_TYPE.equals(task_type)) {
			if (sql == null || "".equals(sql))
				throw BuiltinExceptionFactory.createAttributeMissing(null, TaskTableFields.SQL);
			execDbFun(connection,context, sql);
		} else {
			throw new IllegalArgumentException("The " + task_type + " is not supported!");
		}
	}

	private CompositeMap loadFromString(String content) throws Exception {
		if (content == null)
			return null;
		CompositeMap context = null;
		String newContent = new String(content.getBytes(), "UTF-8");
		if (newContent != null && !"".equals(newContent)) {
			context = new CompositeLoader().loadFromString(newContent, "UTF-8");
			clearInstance(context);
		}
		return context;
	}

	private CompositeMap getContext(CompositeMap taskRecord) throws Exception {
		if (taskRecord == null)
			return null;
		String strContext = taskRecord.getString(TaskTableFields.CONTEXT);
		return loadFromString(strContext);
	}

	private void clearInstance(CompositeMap context) {
		if (context == null)
			return;
		Iterator it = context.entrySet().iterator();
		if (it == null)
			return;
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Object key = entry.getKey();
			if (key.toString().startsWith("_")) {
				it.remove();
			}
		}
	}

	public CompositeMap queryBM(Connection connection, String bm_name, CompositeMap context, CompositeMap parameterMap) throws Exception {
		CompositeMap localContext = context;
		if (localContext == null)
			localContext = new CompositeMap();
		SqlServiceContext sqlContext = SqlServiceContext.createSqlServiceContext(localContext);
		if (sqlContext == null)
			throw new RuntimeException("Can not create SqlServiceContext for context:" + localContext.toXML());
		connection.setAutoCommit(false);
		sqlContext.setConnection(connection);
//		try {
			BusinessModelService service = databaseServiceFactory.getModelService(bm_name, localContext);
			CompositeMap resultMap = service.queryAsMap(parameterMap, FetchDescriptor.fetchAll());
			return resultMap;
//		}
//		finally {
//			if (sqlContext != null)
//				sqlContext.freeConnection();
//		}
	}

	public void executeBM(Connection connection,String bm_name, CompositeMap context, CompositeMap parameterMap) throws Exception {
		CompositeMap localContext = context;
		if (localContext == null)
			localContext = new CompositeMap();
		SqlServiceContext sqlContext = SqlServiceContext.createSqlServiceContext(localContext);
		if (sqlContext == null)
			throw new RuntimeException("Can not create SqlServiceContext for context:" + localContext.toXML());
//		Connection connection = getConnection();
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
//		finally {
//			if (sqlContext != null)
//				sqlContext.freeConnection();
//		}
	}

	protected void executeProc(String procedure_name, int taskId, CompositeMap context) {
		logger.log(Level.CONFIG, "load procedure:{0}", new Object[] { procedure_name });
		Procedure proc = null;
		try {
			proc = procedureManager.loadProcedure(procedure_name);
		} catch (Exception ex) {
			throw BuiltinExceptionFactory.createResourceLoadException(this, procedure_name, ex);
		}
		executeProc(taskId, proc, context);
	}

	protected void executeProc(CompositeMap procedure_config, int taskId, CompositeMap context) {
		logger.log(Level.CONFIG, "load procedure:{0}", new Object[] { procedure_config.toXML() });
		Procedure proc = null;
		try {
			proc = procedureManager.createProcedure(procedure_config);
		} catch (Exception ex) {
			throw BuiltinExceptionFactory.createResourceLoadException(this, String.valueOf(taskId), ex);
		}
		executeProc(taskId, proc, context);
	}

	protected void executeProc(int taskId, Procedure proc, CompositeMap context) {
		if (proc == null)
			throw new IllegalArgumentException("Procedure can not be null!");
		try {
			String name = "task." + taskId;
			if (context != null) {
				context.putObject("/parameter/@task_id", taskId, true);
				ServiceInvoker.invokeProcedureWithTransaction(name, proc, serviceFactory, context);
			} else {
				ServiceInvoker.invokeProcedureWithTransaction(name, proc, serviceFactory);
			}
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Error when invoking procedure " + proc.getName(), ex);
			throw new RuntimeException(ex);
		}
	}

	private String execDbFun(Connection connection,CompositeMap context, String function) throws Exception {
		SqlServiceContext sqlContext = SqlServiceContext.createSqlServiceContext(context);
		if (sqlContext == null)
			throw new RuntimeException("Can not create SqlServiceContext for context:" + context.toXML());
//		Connection connection = getConnection();
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
//			if (sqlContext != null)
//				sqlContext.freeConnection();
		}
		return errorMessage;

	}

	private void execDbProc(Connection connection,CompositeMap context, String executePkg) throws Exception {
		SqlServiceContext sqlContext = SqlServiceContext.createSqlServiceContext(context);
		if (sqlContext == null)
			throw new RuntimeException("Can not create SqlServiceContext for context:" + context.toXML());
//		Connection connection = getConnection();
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
//			if (sqlContext != null)
//				sqlContext.freeConnection();
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

	private void closeConnection(Connection conn) {
		if (conn == null)
			return;
		try {
			conn.close();
		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "", ex);
		}
	}

	public String getOldTaskBM() {
		return oldTaskBM;
	}

	public void setOldTaskBM(String oldTaskBM) {
		this.oldTaskBM = oldTaskBM;
	}

	public String getFetchTaskBM() {
		return fetchTaskBM;
	}

	public void setFetchTaskBM(String fetchTaskBM) {
		this.fetchTaskBM = fetchTaskBM;
	}

	public String getUpdateTaskBM() {
		return updateTaskBM;
	}

	public void setUpdateTaskBM(String updateTaskBM) {
		this.updateTaskBM = updateTaskBM;
	}

	public String getFinishTaskBM() {
		return finishTaskBM;
	}

	public void setFinishTaskBM(String finishTaskBM) {
		this.finishTaskBM = finishTaskBM;
	}

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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
		try {
			handleTask.shutdown();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "", e);
		}
		if (mainThreadPool != null) {
			List<Runnable> taskList = mainThreadPool.shutdownNow();
			for (Runnable task : taskList) {
				if (task instanceof ILifeCycle) {
					((ILifeCycle) task).shutdown();
				} else
					logger.log(Level.SEVERE, "Task " + task.toString() + " can not shutdown!");
			}
		}
		if(connectionQueue != null){
			Connection connection = connectionQueue.poll();
			while(connection != null){
				closeConnection(connection);
				connection = connectionQueue.poll();
			}
		}

	}

	private void addToTaskQueue(CompositeMap task) {
		if (task == null)
			return;
		taskQueue.add(task);
	}

	private CompositeMap popTaskQueue() {
		return taskQueue.poll();
	}

	private int getTaskId(CompositeMap taskRecord) {
		if (taskRecord == null)
			return -1;
		return taskRecord.getInt(TaskTableFields.TASK_ID, -1);
	}

	class GetTask implements Callable<String> {

		Connection connection;

		@Override
		public String call() throws Exception {
			try {
				int failedTime = 0;
				int reTryTime = 10;
				connection = getConnection();
				if(oldTaskBM != null){
					CompositeMap para = new CompositeMap();
					para.put(TaskTableFields.STATUS, TaskTableFields.STATUS_NEW);
					CompositeMap newContext = new CompositeMap();
					try {
						CompositeMap oldTaskRecords = queryBM(connection, oldTaskBM, newContext, para);
						if (oldTaskRecords != null && para.getChilds() != null) {
							CompositeMap taskRecord = null;
							for (Object obj : oldTaskRecords.getChilds()) {
								taskRecord = (CompositeMap) obj;
								para = new CompositeMap();
								para.put(TaskTableFields.STATUS, TaskTableFields.STATUS_WAIT);
								para.put(TaskTableFields.TASK_ID, getTaskId(taskRecord));
								executeBM(connection,updateTaskBM, null, para);
								addToTaskQueue(taskRecord);
							}
						}
					} catch (Throwable e) {
						logger.log(Level.SEVERE, "", e);
					}
				}
				while (running) {
					Number taskId = taskIdList.peek();
					if (taskId == null || taskId.intValue() < 0) {
						Thread.sleep(1000);
						continue;
					}
					CompositeMap task = new CompositeMap();
					task.put(TaskTableFields.TASK_ID, taskId.intValue());
					CompositeMap context = new CompositeMap();
					try {
						executeBM(connection,fetchTaskBM, context, task);
						if (task == null || getTaskId(task) == -1) {
							Thread.sleep(1000);
							continue;
						}
//						CompositeMap newTask = (CompositeMap) task.getChilds().get(0);
						logger.log(Level.CONFIG, "add record to queue,task_id=" + getTaskId(task));
						addToTaskQueue(task);
						taskIdList.poll();
					} catch (IOException e) {
						logger.log(Level.SEVERE, "", e);
						failedTime++;
						if (failedTime > reTryTime) {
							logger.log(Level.SEVERE, "It has failed " + failedTime + " time when get task from database! It will quit now.");
							break;
						} else {
							closeConnection(connection);
							connection = getConnection();
							logger.log(Level.SEVERE, "It has failed " + failedTime
									+ " time when get task from database,please check the configuration!");
							continue;
						}
					} catch (SQLException e) {
						taskIdList.poll();
					}
				}
			} catch (Exception e) {
				logger.log(Level.SEVERE, "", e);
			} finally {
				closeConnection(connection);
			}
			return "finished";
		}
	}

	class HandleTask implements Callable<String>, ILifeCycle {
		int mThreadCount;
		ExecutorService timeOutService;
		ExecutorService handleTaskService;

		public HandleTask(int threadCount) {
			mThreadCount = threadCount;
		}

		@Override
		public String call() throws Exception {
			try{
				timeOutService = Executors.newCachedThreadPool();
				handleTaskService = Executors.newFixedThreadPool(mThreadCount);
				for(int i=0;i<mThreadCount;i++){
					connectionQueue.add(getConnection());
				}
				while (running) {
					CompositeMap taskRecord = popTaskQueue();
					try {
						if (taskRecord == null || taskRecord.isEmpty()) {
							Thread.sleep(1000);
							continue;
						}
						logger.log(Level.CONFIG, "get a task record from queue,task is" + LINE_SEPARATOR + LINE_SEPARATOR + taskRecord.toXML());
						Object task_id = taskRecord.get(TaskTableFields.TASK_ID);
						if (task_id == null || "null".equals(task_id)) {
							Thread.sleep(1000);
							continue;
						}
						TaskExecutor task = new TaskExecutor(timeOutService, taskRecord, (CompositeMap) taskRecord.clone());
						handleTaskService.submit(task);
					} catch (Throwable e) {
						logger.log(Level.SEVERE, "", e);
					}
				}
			}catch(Exception e){
				logger.log(Level.SEVERE, "", e);
			}
			return "finished";
		}

		@Override
		public boolean startup() {
			return true;
		}

		@Override
		public void shutdown() {
			if (timeOutService != null)
				timeOutService.shutdownNow();
			if (handleTaskService != null)
				handleTaskService.shutdownNow();
		}
	}

	class TaskExecutor implements Callable<String> {
		private CompositeMap taskRecord;
		private CompositeMap parameter;
		private ExecutorService timeOutService;

		public TaskExecutor(ExecutorService timeOutService, CompositeMap taskRecord, CompositeMap parameter) {
			this.timeOutService = timeOutService;
			this.taskRecord = taskRecord;
			this.parameter = parameter;
		}

		@Override
		public String call() throws Exception {
			Connection connection =  connectionQueue.poll();
			while(connection == null){
				logger.log(Level.SEVERE,"Can not get database connection!");
				Thread.sleep(1000);
				connection = connectionQueue.poll();
			}
			try {
				
				CompositeMap context = getContext(taskRecord);
				if (context == null)
					context = new CompositeMap();
				ServiceThreadLocal.setCurrentThreadContext(context);

				logger.log(Level.CONFIG, "begin to execute task,task_id=" + getTaskId(taskRecord));
				int execute_time = taskRecord.getInt(TaskTableFields.RETRY_TIME,0) + 1;
				int current_retry_time = taskRecord.getInt(TaskTableFields.CURRENT_RETRY_TIME, 0);
				int time_out = taskRecord.getInt(TaskTableFields.TIME_OUT);
				StringBuilder excepiton = new StringBuilder();
				String errorMessage = null;
				CompositeMap newPara = new CompositeMap();
				newPara.put(TaskTableFields.TASK_ID, getTaskId(taskRecord));
				newPara.put(TaskTableFields.STATUS, TaskTableFields.STATUS_RUNNING);
				try {
					executeBM(connection,updateTaskBM, context, newPara);
				} catch (Throwable e) {
					logger.log(Level.SEVERE, "", e);
				}
				for (; current_retry_time < execute_time; current_retry_time++) {
					if (current_retry_time > 0) {
						try {
							newPara.put(TaskTableFields.CURRENT_RETRY_TIME, current_retry_time);
							executeBM(connection,updateTaskBM, context, newPara);
						} catch (Throwable e) {
							logger.log(Level.SEVERE, "", e);
						}
					}
					try {
						if (time_out != 0) {
							errorMessage = executeTimeOutTask(connection,time_out);
							if (errorMessage == null || errorMessage.isEmpty()) {
								excepiton = null;
								break;
							}
							excepiton.append(errorMessage).append(LINE_SEPARATOR);
						} else {
							executeTask(connection,taskRecord, parameter);
							excepiton = null;
							break;
						}
					} catch (Exception e) {
						excepiton.append(getFullStackTrace(e)).append(LINE_SEPARATOR);
					}
				}
				if (excepiton != null && excepiton.length() != 0) {
					parameter.put(TaskTableFields.EXCEPTION, excepiton.toString());
					logger.log(Level.SEVERE, excepiton.toString());
				}
				logger.log(Level.CONFIG, "finish task,task_id=" + getTaskId(taskRecord));
				try {
					executeBM(connection,finishTaskBM, context, parameter);
				} catch (Throwable e) {
					logger.log(Level.SEVERE, "", e);
				}
			} catch (Exception e) {
				logger.log(Level.SEVERE, "", e);
			}finally{
				connectionQueue.add(connection);
			}
			ServiceThreadLocal.remove();
			return "finished";
		}

		private String executeTimeOutTask(Connection connection,int timeOut) {
			CallableTask callableTask = new CallableTask(connection,taskRecord, parameter);
			StringBuilder excepiton = new StringBuilder();
			Future future = timeOutService.submit(callableTask);
			try {
				future.get(timeOut, TimeUnit.MILLISECONDS);
				return excepiton.toString();
			} catch (Exception e) {
				boolean successful = future.cancel(true);
				if (!successful) {
					logger.log(Level.WARNING, "Can not cancel the task:" + getTaskId(taskRecord));
					return excepiton.toString();
				} else {
					excepiton.append(getFullStackTrace(e));
				}
			}
			return excepiton.toString();
		}

		class CallableTask implements Callable<String> {
			private CompositeMap taskRecord;
			private CompositeMap parameter;
			private Connection connection;
			public CallableTask(Connection connection,CompositeMap taskRecord, CompositeMap parameter) {
				this.connection = connection;
				this.taskRecord = taskRecord;
				this.parameter = parameter;
			}

			@Override
			public String call() throws Exception {
				try {
					executeTask(connection,taskRecord, parameter);
				} catch (Exception e) {
					logger.log(Level.SEVERE, "", e);
				}
				return "finished";
			}
		}
	}

	@Override
	public void onMessage(IMessage message) {
		try {
			logger.log(Level.CONFIG, "receive a messsage:" + message.getText());
			CompositeMap taskRecord = message.getProperties();
			if (taskRecord == null)
				return;
			int task_id = taskRecord.getInt(TaskTableFields.TASK_ID);
			taskIdList.add(task_id);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Can not add the task:" + message);
		}
	}
}
