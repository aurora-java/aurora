package aurora.plugin.c3p0;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;

import com.mchange.v2.c3p0.C3P0ProxyConnection;
import com.mchange.v2.c3p0.DataSources;
import com.mchange.v2.c3p0.DriverManagerDataSource;
import com.mchange.v2.c3p0.PooledDataSource;

import aurora.datasource.DatabaseConnection;
import aurora.datasource.IDataSourceFactory;

public class DataSourceFactory implements IDataSourceFactory {

	public DataSource createDataSource(DatabaseConnection dbConfig)
			throws Exception {
		DataSource ds = null;
		try {
			ds = DataSources.unpooledDataSource(dbConfig.getUrl(),
					dbConfig.getUserName(), dbConfig.getPassword());

			((DriverManagerDataSource) ds).setDriverClass(dbConfig
					.getDriverClass());
			if (dbConfig.getName() != null)
				((DriverManagerDataSource) ds).setDescription(dbConfig
						.getName());
			if (dbConfig.getPool()) {
				if (dbConfig.getConfig() != null)
					ds = DataSources.pooledDataSource(ds, dbConfig.getConfig());
				else
					ds = DataSources.pooledDataSource(ds);
			}
		} catch (SQLException e) {
			throw e;
		}
		return ds;
	}

	public void cleanDataSource(DataSource ds) {
		try {
			((PooledDataSource) ds).close();
		} catch (SQLException e) {
			e.printStackTrace(System.err);
		}
	}

	public Connection getNativeJdbcExtractor(Connection conn) throws Exception {
		Connection nativeConn=null;
		if(conn instanceof C3P0ProxyConnection){
        	C3P0NativeJdbcExtractor nativeJdbcExtractor=new C3P0NativeJdbcExtractor();
        	try {
        		nativeConn = nativeJdbcExtractor.getNativeConnection(conn);
			} catch (Exception e) {
				throw new Exception(e);			
			}			
        }
		return nativeConn;
	}
}
