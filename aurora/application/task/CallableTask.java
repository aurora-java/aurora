package aurora.application.task;

import java.sql.Connection;
import java.util.concurrent.Callable;

import uncertain.composite.CompositeMap;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;

public class CallableTask implements Callable<String> {
	private CompositeMap taskRecord;
	private Connection connection;
	private TaskUtil taskUtil;
	public CallableTask(IObjectRegistry registry,Connection connection, CompositeMap taskRecord) {
		this.connection = connection;
		this.taskRecord = taskRecord;
		ILogger logger = LoggingContext.getLogger(this.getClass().getCanonicalName(), registry);
		taskUtil = new TaskUtil(logger,registry);
	}

	@Override
	public String call() throws Exception {
		try {
			taskUtil.executeTask(connection, taskRecord);
		} catch (Exception e) {
			// logger.log(Level.SEVERE, "", e);
			return taskUtil.getFullStackTrace(e);
		}
		return null;
	}
}
