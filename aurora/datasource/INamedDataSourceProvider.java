package aurora.datasource;
import java.util.Map;

import javax.sql.DataSource;

public interface INamedDataSourceProvider{	
	public DataSource  getDataSource(String name);	
	public Map getAllDataSources();
}
