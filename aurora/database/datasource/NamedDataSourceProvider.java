package aurora.database.datasource;

import java.util.HashMap;
import javax.sql.DataSource;

public class NamedDataSourceProvider implements INamedDataSourceProvider{
	HashMap dsMap=new HashMap();
	public DataSource getDataSource(String name) {
		DataSource ds=(DataSource)dsMap.get(name);
		return ds;
	}

	public void putDataSource(String name, DataSource ds) {
		dsMap.put(name, ds);		
	}
}
