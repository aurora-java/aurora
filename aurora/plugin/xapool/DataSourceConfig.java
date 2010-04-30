package aurora.plugin.xapool;

import java.util.logging.Level;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import org.enhydra.jdbc.pool.StandardXAPoolDataSource;
import org.enhydra.jdbc.standard.StandardXADataSource;

import aurora.database.datasource.INamedDataSourceProvider;
import aurora.database.datasource.NamedDataSourceProvider;
import aurora.transaction.ITransactionService;
import uncertain.core.UncertainEngine;
import uncertain.logging.ILogger;
import uncertain.ocm.IObjectRegistry;

public class DataSourceConfig {
	DataBase[] mDataBases;
	public DataBase[] getDataBases() {
		return mDataBases;
	}

	public void setDataBases(DataBase[] DataBases) {
		mDataBases = DataBases;
	}

	IObjectRegistry mObjectRegistry;
	ILogger mLogger;

	public DataSourceConfig() {
	}

	public DataSourceConfig(UncertainEngine engine) {
		mObjectRegistry = engine.getObjectRegistry();
		mLogger = engine.getLogger(UncertainEngine.UNCERTAIN_LOGGING_TOPIC);
	}

	public DataSourceConfig(ILogger logger, IObjectRegistry reg) {
		mLogger = logger;
		mObjectRegistry = reg;
	}

	public void onInitialize() throws Exception {
		ITransactionService ts = new TransactionService();
		TransactionManager tm = ts.getTransactionManager();
		INamedDataSourceProvider dsProvider = new NamedDataSourceProvider();
		DataSource ds;
		DataBase dbConfig;
		StandardXADataSource xads;
		StandardXAPoolDataSource pool;
		for (int i = 0, length = mDataBases.length; i < length; i++) {
			dbConfig = mDataBases[i];
			xads = new StandardXADataSource();
			xads.setDriverName(dbConfig.getDriverClass());
			xads.setUrl(dbConfig.getUrl());
			xads.setUser(dbConfig.getUserName());
			xads.setPassword(dbConfig.getPassword());
			// set the isolation level (default is READ_COMITTED)
			xads.setTransactionIsolation(2);
			xads.setTransactionManager(tm);
			if (dbConfig.getPool()) {
				pool = new StandardXAPoolDataSource();
				pool.setTransactionManager(tm);
				pool.setDataSource(xads);
				pool.setUser(xads.getUser());
				pool.setPassword(xads.getPassword());
				if (dbConfig.getExpirationTime() != null)
					pool.setLifeTime(dbConfig.getExpirationTime().longValue());
				if (dbConfig.getSleepTime() != null)
					pool.setSleepTime(dbConfig.getSleepTime().longValue());
				if (dbConfig.getMaxConn() != null)
					pool.setMaxSize(dbConfig.getMaxConn().intValue());
				if (dbConfig.getMinConn() != null)
					pool.setMinSize(dbConfig.getMinConn().intValue());
				if (dbConfig.getDeadlockMaxWait() != null)
					pool.setDeadLockMaxWait(dbConfig.getDeadlockMaxWait()
							.intValue());
				if (dbConfig.getDeadlockRetryWait() != null)
					pool.setDeadLockRetryWait(dbConfig.getDeadlockRetryWait()
							.intValue());
				ds = pool;
			} else {
				ds = xads;
			}
			if (dbConfig.getName() == null) {
				mObjectRegistry.registerInstance(DataSource.class, ds);
				mLogger.log(Level.CONFIG,"Setting up dataSource url:{0},user:{1}",
						new Object[]{dbConfig.getUrl(),dbConfig.getUserName()});
			} else {
				((NamedDataSourceProvider) dsProvider).putDataSource(dbConfig
						.getName(), ds);
				mLogger.log(Level.CONFIG,"Setting up namedDataSource url:{0},user:{1},name:{2}",
						new Object[]{dbConfig.getUrl(),dbConfig.getUserName(),dbConfig.getName()});
			}
		}
		mObjectRegistry.registerInstance(INamedDataSourceProvider.class,dsProvider);
		mObjectRegistry.registerInstance(ITransactionService.class, ts);

	}

	
}
