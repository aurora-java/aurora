package aurora.datasource;
import javax.sql.DataSource;

public interface INamedDataSourceProvider{	
	public DataSource  getDataSource(String name);	
}
