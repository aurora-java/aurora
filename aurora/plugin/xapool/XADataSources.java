package aurora.plugin.xapool;

import java.sql.SQLException;

import javax.sql.DataSource;
import javax.sql.XADataSource;
import javax.transaction.TransactionManager;

import org.enhydra.jdbc.pool.StandardXAPoolDataSource;
import org.enhydra.jdbc.standard.StandardXADataSource;

public class XADataSources {
	public static DataSource unpooledXADataSource(String Url,String User,String password,String driverName,TransactionManager tm) throws SQLException{
		StandardXADataSource xads=new StandardXADataSource();
		xads.setDriverName(driverName);
		xads.setUrl(Url);
		xads.setUser(User);
		xads.setPassword(password);
		// set the isolation level (default is READ_COMITTED)
		xads.setTransactionIsolation(2);
		xads.setTransactionManager(tm);
		return xads;
	}
	public static DataSource pooledXADataSource(XADataSource unpooledXADataSource){
		StandardXAPoolDataSource pool= new StandardXAPoolDataSource();
		pool.setTransactionManager(((StandardXADataSource)unpooledXADataSource).getTransactionManager());
		pool.setDataSource(unpooledXADataSource);
		pool.setUser(((StandardXADataSource)unpooledXADataSource).getUser());
		pool.setPassword(((StandardXADataSource)unpooledXADataSource).getPassword());
		return pool;
	}
}
