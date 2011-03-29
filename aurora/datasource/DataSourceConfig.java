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
import aurora.plugin.c3p0.PoolConfig;
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
		mLogger =LoggingContext.getLogger("aurora.datasource", reg);		
		mObjectRegistry = reg;
		mOCManager=ocManager;
	}	
		
	public void onInitialize() throws Exception {
		int length=mDatabaseConnections.length;
		DataSource ds=null;			
		ITransactionService ts =null;
		INamedDataSourceProvider dsProvider = new NamedDataSourceProvider();		
		DatabaseConnection dbConfig=null;
		if(useTransactionManager){//xapool			
			TransactionManager tm=null;				
			ts = new TransactionService(true);			
			tm = ts.getTransactionManager();
			for(int i=0;i<length;i++){
				dbConfig = mDatabaseConnections[i];
				ds=XADataSources.unpooledXADataSource(dbConfig.getUrl(), dbConfig.getUserName(), dbConfig.getPassword(), dbConfig.getDriverClass(), tm);
				if (dbConfig.getPool()) {
					ds = XADataSources.pooledXADataSource((XADataSource)ds);		
//					((StandardXAPoolDataSource)ds).getMaxSize()
					mOCManager.populateObject(dbConfig.config, (StandardXAPoolDataSource)ds);					
				}
				registryDataSource(ds,dbConfig,dsProvider);
			}			
		}else{//c3p0
			if(length!=1){
				mLogger.log(Level.SEVERE, "TransactionManager is disabled,please use only one datasource");
				throw new ServletException("TransactionManager is disabled,please use only one datasource");
			}
			ts = new TransactionService(false);	
			dbConfig=mDatabaseConnections[0];			
			ds = DataSources.unpooledDataSource(dbConfig.getUrl(),dbConfig.getUserName(),dbConfig.getPassword());
			((DriverManagerDataSource)ds).setDriverClass(dbConfig.getDriverClass());
			if(dbConfig.getPool()){					
				PoolConfig poolConfig=new PoolConfig();
				mOCManager.populateObject(dbConfig.config, poolConfig);
				ds=DataSources.pooledDataSource(ds, poolConfig.getConfig());				
			}
			registryDataSource(ds,dbConfig,dsProvider);
		}		
		mObjectRegistry.registerInstance(INamedDataSourceProvider.class,dsProvider);			
		mObjectRegistry.registerInstance(ITransactionService.class, ts);			
	}
	
	private void registryDataSource(DataSource ds,DatabaseConnection dbConfig,INamedDataSourceProvider dsProvider){
		String dataSourceName=dbConfig.getName();		
		if(dataSourceName==null){
			mLogger.log(Level.CONFIG,"Setting up dataSource url:{0},user:{1}",
					new Object[]{dbConfig.getUrl(),dbConfig.getUserName()});			
			mObjectRegistry.registerInstance(DataSource.class, ds);
		}else{
			((NamedDataSourceProvider) dsProvider).putDataSource(dbConfig.getName(), ds);
			mLogger.log(Level.CONFIG,"Setting up dataSource url:{0},user:{1},name:{2}",
					new Object[]{dbConfig.getUrl(),dbConfig.getUserName(),dbConfig.getName()});			
		}
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
}
