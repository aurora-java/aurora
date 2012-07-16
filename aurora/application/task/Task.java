package aurora.application.task;

import aurora.database.service.BusinessModelService;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.database.service.SqlServiceContext;
import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

public class Task extends AbstractEntry {

	private IDatabaseServiceFactory mDatabaseServiceFactory;

	private String bm;
	private String taskName;
	private String taskDescription;
	private String executorInstance;
	private String procFilePath;
	private String procContent;
	private String context;
	private int priority;
	private String taskType;
	private String sql;

	public Task(IDatabaseServiceFactory databaseServiceFactory) {
		this.mDatabaseServiceFactory = databaseServiceFactory;
	}

	@Override
	public void run(ProcedureRunner runner) throws Exception {
		CompositeMap context = runner.getContext();
		if (bm == null)
			throw BuiltinExceptionFactory.createAttributeMissing(this, "bm");
		bm = TextParser.parse(bm, context);
		if (taskType == null)
			throw BuiltinExceptionFactory.createAttributeMissing(this, "taskType");
		taskType = TextParser.parse(taskType, context);
		if (TaskTableFields.JAVA_TYPE.equals(taskType)) {
			if (procFilePath == null && procContent == null)
				throw BuiltinExceptionFactory.createOneAttributeMissing(this, "procFilePath,procContent");
			if (procFilePath != null && procContent != null)
				throw BuiltinExceptionFactory.createConflictAttributesExcepiton(this, "procFilePath,procContent");
			procFilePath = TextParser.parse(procFilePath, context);
			procContent = TextParser.parse(procContent, context);
		}
		if (TaskTableFields.PROCEDURE_TYPE.equals(taskType) || TaskTableFields.FUNCTION_TYPE.equals(taskType)) {
			if (sql == null)
				throw BuiltinExceptionFactory.createAttributeMissing(this, "taskType");
			sql = TextParser.parse(sql, context);
		}
		SqlServiceContext sqlServiceContext = null;
		try {
			sqlServiceContext = mDatabaseServiceFactory.createContextWithConnection();
			CompositeMap parameters = context.getChild("parameter");
			if(parameters == null){
				parameters = new CompositeMap("parameter");
				context.addChild(parameters);
			}
			parameters.put(TaskTableFields.TASK_NAME, taskName);
			parameters.put(TaskTableFields.TASK_DESCRIPTION, taskDescription);
			parameters.put(TaskTableFields.EXECUTOR_INSTANCE, executorInstance);
			parameters.put(TaskTableFields.PROC_FILE_PATH, procFilePath);
			parameters.put(TaskTableFields.PROC_CONTENT, procContent);
			parameters.put(TaskTableFields.CONTEXT, context.toXML());
			parameters.put(TaskTableFields.PRIORITY, priority);
			parameters.put(TaskTableFields.TASK_TYPE, taskType);
			parameters.put(TaskTableFields.SQL, sql);
			BusinessModelService businessModelService = mDatabaseServiceFactory.getModelService(bm, runner.getContext());
			businessModelService.execute(parameters);
		} finally {
			if (sqlServiceContext != null)
				sqlServiceContext.freeConnection();
		}
	}

	public String getBm() {
		return bm;
	}

	public void setBm(String bm) {
		this.bm = bm;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskDescription() {
		return taskDescription;
	}

	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}

	public String getExecutorInstance() {
		return executorInstance;
	}

	public void setExecutorInstance(String executorInstance) {
		this.executorInstance = executorInstance;
	}

	public String getProcFilePath() {
		return procFilePath;
	}

	public void setProcFilePath(String procFilePath) {
		this.procFilePath = procFilePath;
	}

	public String getProcContent() {
		return procContent;
	}

	public void setProcContent(String procContent) {
		this.procContent = procContent;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

}
