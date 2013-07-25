package aurora.datasource.nativejdbc;

import java.sql.Connection;
import java.sql.SQLException;

public interface INativeJdbcExtractor {
	Connection getNativeConnection(Connection con) throws SQLException;
}
