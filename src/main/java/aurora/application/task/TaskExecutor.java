package aurora.application.task;

import java.sql.Connection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import uncertain.composite.CompositeMap;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;
import aurora.service.ServiceThreadLocal;

public class TaskExecutor implements Callable<String> {
	private CompositeMap taskRecord;
	private ExecutorService timeOutService;
	private IObjectRegistry registry;
	private ILogger logger;
	private TaskUtil taskUtil;
	private TaskHandler taskManager;

	public TaskExecutor(IObjectRegistry registry, TaskHandler taskManager, ExecutorService timeOutService, CompositeMap taskRecord) {
		this.registry = registry;
		this.timeOutService = timeOutService;
		this.taskRecord = taskRecord;
		this.taskManager = taskManager;
		logger = LoggingContext.getLogger(this.getClass().getCanonicalName(), registry);
		taskUtil = new TaskUtil(logger, registry);

	}

	@Override
	public String call() throws Exception {
		Connection connection = taskManager.getConnectionFromQueue();
		while (connection == null) {
			logger.log(Level.SEVERE, "Can not get database connection!");
			Thread.sleep(1000);
			connection = taskManager.getConnectionFromQueue();
		}
		CompositeMap para = new CompositeMap();
		CompositeMap context = new CompositeMap();
		int taskId = taskUtil.getTaskId(taskRecord);
		para.put(TaskTableFields.TASK_ID, taskId);
		try {
			context = taskUtil.getContext(taskRecord);
			ServiceThreadLocal.setCurrentThreadContext(context);

			logger.log(Level.CONFIG, "begin to execute task,task_id=" + taskUtil.getTaskId(taskRecord));
			int execute_time = taskRecord.getInt(TaskTableFields.RETRY_TIME, 0) + 1;
			int current_retry_time = taskRecord.getInt(TaskTableFields.CURRENT_RETRY_TIME, 0);
			int time_out = taskRecord.getInt(TaskTableFields.TIME_OUT);
			StringBuilder excepiton = new StringBuilder();
			String errorMessage = null;
			para.put(TaskTableFields.STATUS, TaskTableFields.STATUS_RUNNING);

			String updateTaskBM = taskManager.getUpdateTaskBM();
			try {
				taskUtil.executeBM(connection, updateTaskBM, context, para);
			} catch (Throwable e) {
				logger.log(Level.SEVERE, "", e);
			}
			for (; current_retry_time < execute_time; current_retry_time++) {
				if (current_retry_time > 0) {
					try {
						para.put(TaskTableFields.CURRENT_RETRY_TIME, current_retry_time);
						taskUtil.executeBM(connection, updateTaskBM, context, para);
					} catch (Throwable e) {
						logger.log(Level.SEVERE, "", e);
					}
				}
				try {
					errorMessage = executeTask(connection, time_out);
					if (errorMessage == null || errorMessage.isEmpty()) {
						excepiton = null;
						break;
					}
					excepiton.append(errorMessage).append(TaskUtil.LINE_SEPARATOR);
				} catch (Exception e) {
					excepiton.append(taskUtil.getFullStackTrace(e)).append(TaskUtil.LINE_SEPARATOR);
				}
			}
			if (excepiton != null && excepiton.length() != 0) {
				para.put(TaskTableFields.EXCEPTION, excepiton.toString());
				logger.log(Level.SEVERE, excepiton.toString());
			}
			logger.log(Level.CONFIG, "finish task,task_id=" + taskId);
			logger.log(Level.CONFIG, "pass parameter =" + para.toXML());
			updateFinishStatus(connection, context, para);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "", e);
			para.put(TaskTableFields.EXCEPTION, taskUtil.getFullStackTrace(e));
			updateFinishStatus(connection, context, para);
		} finally {
			taskManager.removeRunningTask(String.valueOf(taskId));
			taskManager.backToQueue(connection);
		}
		// 如果任务队列空了，就去系统查看是否有新任务需要处理
		taskManager.checkTaskQueue();
		ServiceThreadLocal.remove();
		return "finished";
	}

	private void updateFinishStatus(Connection connection, CompositeMap context, CompositeMap newPara) {
		try {
			taskUtil.executeBM(connection, taskManager.getFinishTaskBM(), context, newPara);
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "", e);
		}
	}

	private String executeTask(Connection connection, int timeOut) {
		int taskId = taskUtil.getTaskId(taskRecord);
		CallableTask callableTask = new CallableTask(registry, connection, taskRecord);
		StringBuilder excepiton = new StringBuilder();
		Future<String> future = timeOutService.submit(callableTask);
		taskManager.addRunningTask(String.valueOf(taskId), future);
		try {
			String result = "";
			if (timeOut > 0)
				result = future.get(timeOut, TimeUnit.MILLISECONDS);
			else
				result = future.get();
			return result;
		} catch (Exception e) {
			boolean successful = future.cancel(true);
			if (!successful) {
				logger.log(Level.WARNING, "Can not cancel the task:" + taskId);
			}
			excepiton.append(taskUtil.getFullStackTrace(e));
		}
		return excepiton.toString();
	}
}
