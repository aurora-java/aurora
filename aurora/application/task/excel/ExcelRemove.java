package aurora.application.task.excel;



import java.io.File;

import uncertain.composite.TextParser;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

public class ExcelRemove extends AbstractEntry{
	public String path;

	@Override
	public void run(ProcedureRunner runner) throws Exception {
		if(path == null)
			throw BuiltinExceptionFactory.createAttributeMissing(this, "path");
		path = TextParser.parse(path, runner.getContext());
		if (!path.endsWith(".xls") && !path.endsWith(".xlsx")) {
			throw new IllegalArgumentException("This file '" + path + "' is not an excel file!");
		}
		ILogger logger = LoggingContext.getLogger(runner.getContext(),this.getClass().getCanonicalName());
		File file = new File(path);
		if(!file.exists())
			logger.warning("This file '" + path + "' is not exist!");
		boolean is_success = file.delete();
		if (!is_success) {
			logger.warning("This file '" + path + "' can not be deleted!");
		}
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
}
