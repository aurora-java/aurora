package aurora.datasource;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

public class NamedDataSourceProvider implements INamedDataSourceProvider{
	HashMap dsMap=new HashMap();
	public DataSource getDataSource(String name) {
		DataSource ds=(DataSource)dsMap.get(name);
		return ds;
	}
	
	public Map getAllDataSources() {
		return dsMap;
	}

	public void putDataSource(String name, DataSource ds) {
		dsMap.put(name, ds);		
	}	
}
