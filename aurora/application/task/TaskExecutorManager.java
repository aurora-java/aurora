package aurora.application.task;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import uncertain.composite.CompositeMap;
import uncertain.core.ILifeCycle;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;

public class TaskExecutorManager implements Callable<String>, ILifeCycle {
	private ExecutorService timeOutService;
	private ExecutorService handleTaskService;

	private IObjectRegistry mRegistry;
	private ILogger logger;
	private TaskHandler taskManager;
	
	public TaskExecutorManager(IObjectRegistry registry,TaskHandler taskManager) {
		this.mRegistry = registry;
		this.taskManager = taskManager;
		logger = LoggingContext.getLogger(this.getClass().getCanonicalName(), mRegistry);
	}

	@Override
	public String call() throws Exception {
		try {
			timeOutService = Executors.newCachedThreadPool();
			int threadCount = taskManager.getThreadCount();
			handleTaskService = Executors.newFixedThreadPool(threadCount);
			while (taskManager.isRunnning()) {
				CompositeMap taskRecord = taskManager.popTaskQueue();
				try {
					if (taskRecord == null || taskRecord.isEmpty()) {
						Thread.sleep(1000);
						continue;
					}
					logger.log(Level.CONFIG, "get a task record from queue,task is" + TaskUtil.LINE_SEPARATOR + TaskUtil.LINE_SEPARATOR + taskRecord.toXML());
					Object task_id = taskRecord.get(TaskTableFields.TASK_ID);
					if (task_id == null || "null".equals(task_id)) {
						Thread.sleep(1000);
						continue;
					}
					TaskExecutor taskExecutor = new TaskExecutor(mRegistry,taskManager,timeOutService, taskRecord);
					handleTaskService.submit(taskExecutor);
				} catch (InterruptedException e) {
					// ignore
				} catch (Throwable e) {
					logger.log(Level.SEVERE, "", e);
				}
			}
		} catch (Exception e) {
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
