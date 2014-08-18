package aurora.application.task;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;

import javax.sql.DataSource;

import uncertain.composite.CompositeMap;
import uncertain.core.ILifeCycle;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.IProcedureManager;
import aurora.application.features.msg.IConsumer;
import aurora.application.features.msg.IMessage;
import aurora.application.features.msg.IMessageListener;
import aurora.application.features.msg.IMessageStub;
import aurora.application.features.msg.INoticerConsumer;
import aurora.application.features.msg.Message;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.service.IServiceFactory;

public class TaskHandler extends AbstractLocatableObject implements ILifeCycle, IMessageListener {

	public static final String DEFAULT_TOPIC = "task";
	public static final String DEFAULT_MESSAGE = "task_message";//兼容不可取消任务的老版本
	public static final String NEW_MESSAGE = "new_task_message";
	public static final String REMOVE_MESSAGE = "remove_task_message";

	private IObjectRegistry mRegistry;

	private String oldTaskBM;
	private String fetchTaskBM;
	private String updateTaskBM;
	private String finishTaskBM;
	private int threadCount = 2;
	private int fetchTaskTimerInterval = 10000;// 默认10秒

	private IDatabaseServiceFactory databaseServiceFactory;
	private DataSource dataSource;
	private IProcedureManager procedureManager;
	private IServiceFactory serviceFactory;
	private IMessageStub msgStub;

	private ILogger logger;
	private boolean running = true;

	private Queue<CompositeMap> taskQueue = new ConcurrentLinkedQueue<CompositeMap>();
	private ExecutorService mainThreadPool;
	private TaskExecutorManager taskExecutorManager;
	protected String topic = DEFAULT_TOPIC;
	protected String new_message = NEW_MESSAGE;
	protected String remove_message = REMOVE_MESSAGE;
	private Queue<Connection> connectionQueue = new ConcurrentLinkedQueue<Connection>();
	private Map<String,Future<String>> runningTask = new HashMap<String,Future<String>>();
	private Map<String,CompositeMap> waitTasks = new HashMap<String,CompositeMap>();
	
	Object fetchNewTaskLock = new Object();

	private TaskUtil taskUtil;

	public TaskHandler(IObjectRegistry registry) {
		this.mRegistry = registry;
	}

	public void onInitialize() {
		initInstances();
		initConnectionQueue();
		taskUtil = new TaskUtil(logger, mRegistry);
		
		TaskFetcher taskFetcher = new TaskFetcher(mRegistry, this);
		taskExecutorManager = new TaskExecutorManager(mRegistry, this);
		mainThreadPool = Executors.newFixedThreadPool(2);
		mainThreadPool.submit(taskFetcher);
		mainThreadPool.submit(taskExecutorManager);
		resetUnfinishedTaskStatus(msgStub);
	}
	
	private void initConnectionQueue(){
		for (int i = 0; i < threadCount; i++) {
			connectionQueue.add(getConnection());
		}
	}

	private void initInstances() {
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

		msgStub = (IMessageStub) mRegistry.getInstanceOfType(IMessageStub.class);
		if (msgStub == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(this, IMessageStub.class, this.getClass().getName());
		if (!msgStub.isStarted())
			logger.warning("JMS MessageStub is not started, please check the configuration.");
		IConsumer consumer = msgStub.getConsumer(topic);
		if (consumer == null) {
			throw new IllegalStateException("MessageStub does not define the topic '" + topic + "', please check the configuration.");
		}
		if (!(consumer instanceof INoticerConsumer))
			throw BuiltinExceptionFactory.createInstanceTypeWrongException(this.getOriginSource(), INoticerConsumer.class, IConsumer.class);
		((INoticerConsumer) consumer).addListener(DEFAULT_MESSAGE, this);
		((INoticerConsumer) consumer).addListener(new_message, this);
		((INoticerConsumer) consumer).addListener(remove_message, this);

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

	private void resetUnfinishedTaskStatus(IMessageStub messageStub) {
		Connection connection = getConnection();
		if (oldTaskBM != null) {
			try {
				CompositeMap context = new CompositeMap("context");
				CompositeMap parameter = new CompositeMap("parameter");
				taskUtil.executeBM(connection, oldTaskBM, context, parameter);
				
				Message msg = new Message(new_message, null);
				messageStub.getDispatcher().send(topic, msg, context);
				context.clear();
			} catch (Exception e) {
				logger.log(Level.SEVERE, "", e);
			} finally {
				taskUtil.closeConnection(connection);
			}
		}
	}

	public boolean isRunnning() {
		return running;
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

	public int getFetchTaskTimerInterval() {
		return fetchTaskTimerInterval;
	}

	public void setFetchTaskTimerInterval(int fetchTaskTimerInterval) {
		this.fetchTaskTimerInterval = fetchTaskTimerInterval;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getMessage() {
		return new_message;
	}

	public void setMessage(String message) {
		this.new_message = message;
	}

	public Connection getConnection() {
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

	@Override
	public boolean startup() {
		return true;
	}

	@Override
	public void shutdown() {
		running = false;
		synchronized (fetchNewTaskLock) {
			fetchNewTaskLock.notify();
		}
		try {
			taskExecutorManager.shutdown();
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
		if (connectionQueue != null) {
			Connection connection = connectionQueue.poll();
			while (connection != null) {
				taskUtil.closeConnection(connection);
				connection = connectionQueue.poll();
			}
		}
		if(runningTask != null){
			runningTask.clear();
		}
	}
	
	public void addRunningTask(String taskId,Future<String> task){
		runningTask.put(taskId, task);
	}
	
	public void stopRunningTask(String taskId){
		Future<String> task = runningTask.get(taskId);
		if(task != null){
			task.cancel(true);
		}
		removeRunningTask(taskId);
	}
	
	public void cancelTask(String taskId){
		CompositeMap task = waitTasks.get(taskId);
		if(task!= null){
			taskQueue.remove(task);
		}
		stopRunningTask(taskId);
	}
	
	public void removeRunningTask(String taskId){
		runningTask.remove(taskId);
	}

	public void addToTaskQueue(CompositeMap task) {
		if (task == null)
			return;
		taskQueue.add(task);
		waitTasks.put(String.valueOf(taskUtil.getTaskId(task)), task);
	}

	public CompositeMap popTaskQueue() {
		CompositeMap task = taskQueue.poll();
		if(task != null){
			String taskId=String.valueOf(taskUtil.getTaskId(task));
			waitTasks.remove(taskId);
		}
		return task;
	}

	public boolean hasIdleConnnection() {
		return connectionQueue.size() > 0;
	}

	public Connection getConnectionFromQueue() {
		return connectionQueue.poll();
	}

	public void backToQueue(Connection connection) {
		connectionQueue.add(connection);
	}

	public void checkTaskQueue() {
		if (taskQueue.size() == 0) {
			synchronized (fetchNewTaskLock) {
				fetchNewTaskLock.notify();
			}
		}
	}

	@Override
	public void onMessage(IMessage message) {
		try {
			String taskType = message.getText();
			if(new_message.equalsIgnoreCase(taskType)||DEFAULT_MESSAGE.equalsIgnoreCase(taskType)){
				// 还有任务未处理，暂时不接收新任务。并在线程都执行完成后，会查看是否有新任务。
				if (taskQueue.size() > 0)
					return;
				logger.log(Level.CONFIG, "receive a messsage:" + message.getText());
				synchronized (fetchNewTaskLock) {
					fetchNewTaskLock.notify();
				}
			}else if(remove_message.equals(taskType)){
				String taskId = message.getProperties().getString("task_id");
				if(taskId != null){
					cancelTask(taskId);
				}
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Can not add the task:" + message);
		}
	}
}
