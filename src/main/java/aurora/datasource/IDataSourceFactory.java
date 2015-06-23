package aurora.datasource;

import java.sql.Connection;

import javax.sql.DataSource;

public interface IDataSourceFactory {
	public DataSource createDataSource(DatabaseConnection dbConfig)
			throws Exception;

	public void cleanDataSource(DataSource ds);

	public Connection getNativeJdbcExtractor(Connection conn) throws Exception;
}
