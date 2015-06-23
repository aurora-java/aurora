package aurora.application.task;

import java.sql.Connection;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import uncertain.composite.CompositeMap;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;

public class TaskFetcher implements Callable<String> {

	private IObjectRegistry mRegistry;
	private TaskHandler taskManager;
	private Connection connection;
	private ILogger logger;
	private TaskUtil taskUtil;
	private int failedTime = 0;
	private int retryTime = 10;

	public TaskFetcher(IObjectRegistry registry, TaskHandler taskManager) {
		this.mRegistry = registry;
		this.taskManager = taskManager;
		logger = LoggingContext.getLogger(this.getClass().getCanonicalName(), mRegistry);
		taskUtil = new TaskUtil(logger, mRegistry);
	}

	@Override
	public String call() throws Exception {
		try {
			boolean hasNext = false;
			connection = taskManager.getConnection();
			while (taskManager.isRunnning()) {
				synchronized (taskManager.fetchNewTaskLock) {
					if (!hasNext) {
						int fetchTaskTimerInterval = taskManager.getFetchTaskTimerInterval();
						if (fetchTaskTimerInterval > 0)
							taskManager.fetchNewTaskLock.wait(fetchTaskTimerInterval);
						else
							taskManager.fetchNewTaskLock.wait();
					}
					if (!taskManager.isRunnning())
						break;
					CompositeMap task = new CompositeMap();
					CompositeMap context = new CompositeMap();
					try {
						taskUtil.executeBM(connection, taskManager.getFetchTaskBM(), context, task);
						if (failedTime > 0)
							failedTime = 0;
						if (task == null || taskUtil.getTaskId(task) == -1) {
							continue;
						}
						logger.log(Level.CONFIG, "add record to queue,task_id=" + taskUtil.getTaskId(task));
						taskManager.addToTaskQueue(task);
						hasNext = false;
						// 如果有线程处于空闲，并且还有任务记录需要处理，则继续从数据空获取。
						if (taskManager.hasIdleConnnection()) {
							int record_count = task.getInt("record_count", -1);
							if (record_count > 1) {
								hasNext = true;
							}
						}
					} catch (Exception e) {
						logger.log(Level.SEVERE, "", e);
						failedTime++;
						if (failedTime < retryTime) {
							taskUtil.closeConnection(connection);
							connection = taskManager.getConnection();
							logger.log(Level.SEVERE, "It has failed " + failedTime + " time when get task from database,please check the configuration!");
							continue;
						} else {
							logger.log(Level.SEVERE, "It has failed " + failedTime + " time when get task from database! It will quit now.");
							break;
						}
					}
				}
			}
		} catch (InterruptedException e) {
			// ignore
		} catch (Exception e) {
			logger.log(Level.SEVERE, "", e);
		} finally {
			taskUtil.closeConnection(connection);
		}
		logger.log(Level.CONFIG, "taskFetcher finished.");
		return "finished";
	}
}