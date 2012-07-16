package aurora.application.task;

public interface TaskTableFields {

	public static final String TASK_ID = "task_id";
	public static final String TASK_NAME = "task_name";
	public static final String TASK_DESCRIPTION = "task_description";
	public static final String EXECUTOR_INSTANCE = "executor_instance";//
	public static final String PRIORITY = "priority";
	public static final String TASK_TYPE = "task_type";
	public static final String PROC_FILE_PATH = "proc_file_path";
	public static final String PROC_CONTENT = "proc_content";
	public static final String CONTEXT = "context";
	public static final String SQL = "sql";
	public static final String EXCEPTION = "exception";
	
	
	public static final String JAVA_TYPE = "JAVA";
	public static final String FUNCTION_TYPE = "FUNCTION";
	public static final String PROCEDURE_TYPE = "PROCEDURE";

}
