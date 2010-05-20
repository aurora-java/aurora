package aurora.datasource;

import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import javax.transaction.TransactionManager;
import org.enhydra.jdbc.pool.StandardXAPoolDataSource;
import com.mchange.v2.c3p0.DataSources;
import com.mchange.v2.c3p0.DriverManagerDataSource;

import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.OCManager;
import aurora.plugin.xapool.TransactionService;
import aurora.plugin.xapool.XADataSources;
import aurora.transaction.ITransactionService;

public class DataSourceConfig {
	DatabaseConnection[] mDatabaseConnections;
	boolean useTransactionManager=false;
	IObjectRegistry mObjectRegistry;
	ILogger mLogger;
	OCManager mOCManager;
	public DataSourceConfig(IObjectRegistry reg,OCManager ocManager) {
		mLogger =LoggingContext.getLogger("aurora.database", reg);		
		mObjectRegistry = reg;
		mOCManager=ocManager;
	}
	
	public boolean getUseTransactionManager(){
		return useTransactionManager;
	}
	public void setUseTransactionManager(boolean useTransactionManager){
		this.useTransactionManager=useTransactionManager;
	}
	public DatabaseConnection[] getDatabaseConnections() {
		return mDatabaseConnections;
	}    
	public void setDatabaseConnections(DatabaseConnection[] DataBases) {
		mDatabaseConnections = DataBases;
	}	
	public void onInitialize() throws Exception {
		int length = mDatabaseConnections.length;
		DataSource ds=null;	
		DatabaseConnection dbConfig=null;
		TransactionManager tm=null;		
		INamedDataSourceProvider dsProvider = new NamedDataSourceProvider();
		ITransactionService ts =null;
		if(useTransactionManager){
			ts = new TransactionService(true);
			tm = ts.getTransactionManager();
		}else{
			ts = new TransactionService(false);
			boolean is_error=false;
			if(length==1){
				if(mDatabaseConnections[0].getName()!=null){
					is_error=true;					
				}
			}else{
				is_error=true;				
			}			
			if(is_error){
				mLogger.log(Level.SEVERE, "TransactionManager is disabled,please use default database");
				throw new ServletException("TransactionManager is disabled,please use default database");
			}		
		}			
		for (int i = 0;i<length; i++) {
			dbConfig = mDatabaseConnections[i];
			if(useTransactionManager){
				ds=XADataSources.unpooledXADataSource(dbConfig.getUrl(), dbConfig.getUserName(), dbConfig.getPassword(), dbConfig.getDriverClass(), tm);
				if (dbConfig.getPool()) {
					ds = XADataSources.pooledXADataSource((XADataSource)ds);
					mOCManager.populateObject(dbConfig.config, (StandardXAPoolDataSource)ds);					
				}
			}else{
				ds = DataSources.unpooledDataSource(dbConfig.getUrl(),dbConfig.getUserName(),dbConfig.getPassword());
				((DriverManagerDataSource)ds).setDriverClass(dbConfig.getDriverClass());
				if(dbConfig.getPool())
					ds=DataSources.pooledDataSource(ds);
			}			
			if (dbConfig.getName() == null) {
				mObjectRegistry.registerInstance(DataSource.class, ds);
				mLogger.log(Level.CONFIG,"Setting up dataSource url:{0},user:{1}",
						new Object[]{dbConfig.getUrl(),dbConfig.getUserName()});
			} else {
				((NamedDataSourceProvider) dsProvider).putDataSource(dbConfig.getName(), ds);
				mLogger.log(Level.CONFIG,"Setting up namedDataSource url:{0},user:{1},name:{2}",
						new Object[]{dbConfig.getUrl(),dbConfig.getUserName(),dbConfig.getName()});
			}
		}		
		mObjectRegistry.registerInstance(INamedDataSourceProvider.class,dsProvider);		
		mObjectRegistry.registerInstance(ITransactionService.class, ts);
	}	
}
