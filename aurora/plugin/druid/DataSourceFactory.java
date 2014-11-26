package aurora.plugin.druid;

import java.sql.Connection;
import java.util.Properties;
import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;

import aurora.datasource.DatabaseConnection;
import aurora.datasource.IDataSourceFactory;

public class DataSourceFactory implements IDataSourceFactory {

	public DataSource createDataSource(DatabaseConnection dbConfig)
			throws Exception {		
		Properties properties=dbConfig.getPoolProperties();
		DataSource ds = null;
		try {			
			properties.put("username", dbConfig.getUserName());
			properties.put("password", dbConfig.getPassword());
			properties.put("driverClassName", dbConfig.getDriverClass());
			properties.put("url", dbConfig.getUrl());
			ds = DruidDataSourceFactory.createDataSource(properties);
		} catch (Exception e) {
			throw e;
		}
		return ds;
	}

	public void cleanDataSource(DataSource ds) {
		((DruidDataSource) ds).close();
	}

	@Override
	public Connection getNativeJdbcExtractor(Connection conn) throws Exception {
		Connection nativeConn=conn;
		if(conn instanceof DruidPooledConnection){
			DruidPooledConnection c=(DruidPooledConnection)conn;
			nativeConn=c.getConnection();        	
        }
		return nativeConn;
	}	
}
