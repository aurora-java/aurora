package aurora.application.task.excel;

import uncertain.composite.CompositeMap;

public interface IExcelTask {
	public String getSvc();
	public String getDir();
	public CompositeMap getProcedure();
}
