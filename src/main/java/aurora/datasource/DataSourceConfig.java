package aurora.datasource;

import java.sql.Connection;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

import javax.sql.DataSource;
import javax.sql.XADataSource;
import javax.transaction.TransactionManager;

import org.enhydra.jdbc.pool.StandardXAPoolDataSource;
import org.enhydra.jdbc.standard.StandardXADataSource;

import uncertain.core.IContainer;
import uncertain.core.ILifeCycle;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.OCManager;
import uncertain.proc.IProcedureManager;
import aurora.datasource.DatabaseConnection;
import aurora.datasource.IDataSourceFactory;
import aurora.datasource.INamedDataSourceProvider;
import aurora.datasource.NamedDataSourceProvider;
import aurora.plugin.xapool.TransactionService;
import aurora.plugin.xapool.XADataSources;
import aurora.service.IServiceFactory;
import aurora.service.ServiceFactoryImpl;
import aurora.transaction.ITransactionService;

public class DataSourceConfig implements ILifeCycle {
	DatabaseConnection[] mDatabaseConnections;
	INamedDataSourceProvider dsProvider;
	boolean useTransactionManager = false;
	IObjectRegistry mObjectRegistry;
	String className="aurora.plugin.c3p0.DataSourceFactory";
	ILogger mLogger;
	OCManager mOCManager;
	boolean flag = false;

	public DataSourceConfig(IObjectRegistry reg, OCManager ocManager) {
		dsProvider = new NamedDataSourceProvider();
		mLogger = LoggingContext.getLogger("aurora.datasource", reg);
		mObjectRegistry = reg;
		mOCManager = ocManager;
	}

	// TODO to be refactor
	private ServiceFactoryImpl createServiceFactory(ITransactionService ts) {
		IProcedureManager pr = (IProcedureManager) mObjectRegistry
				.getInstanceOfType(IProcedureManager.class);
		IContainer container = (IContainer) mObjectRegistry
				.getInstanceOfType(IContainer.class);
		ServiceFactoryImpl sf = new ServiceFactoryImpl(container, ts, pr);
		return sf;
	}

	/*
	 * public void onInitialize() { startup(); }
	 */

	public boolean startup() {
		if (flag)
			return true;
		else
			flag = true;
		DataSource ds = null;
		ITransactionService ts = null;
		DatabaseConnection dbConfig = null;
		try {
			int length = mDatabaseConnections.length;
			if (useTransactionManager) {// xapool
				TransactionManager tm = null;
				ts = new TransactionService(true);
				tm = ts.getTransactionManager();
				for (int i = 0; i < length; i++) {
					dbConfig = mDatabaseConnections[i];
					ds = XADataSources.unpooledXADataSource(dbConfig.getUrl(),
							dbConfig.getUserName(), dbConfig.getPassword(),
							dbConfig.getDriverClass(), tm);
					if (dbConfig.getPool()) {
						ds = XADataSources
								.pooledXADataSource((XADataSource) ds);
						if (dbConfig.getConfig() != null)
							mOCManager.populateObject(dbConfig.getConfig(),
									(StandardXAPoolDataSource) ds);
					}
					registryDataSource(ds, dbConfig, dsProvider);
				}
			} else {
				IDataSourceFactory dbFactory = (IDataSourceFactory) Class
						.forName(className).newInstance();
				ts = new TransactionService(false);
				for (int i = 0; i < length; i++) {
					dbConfig = mDatabaseConnections[i];
					ds = dbFactory.createDataSource(dbConfig);
					registryDataSource(ds, dbConfig, dsProvider);
				}
			}
			mObjectRegistry.registerInstance(INamedDataSourceProvider.class,
					dsProvider);
			mObjectRegistry.registerInstance(ITransactionService.class, ts);

			IServiceFactory sf = createServiceFactory(ts);
			mObjectRegistry.registerInstance(IServiceFactory.class, sf);
			mObjectRegistry.registerInstance(DataSourceConfig.class, this);
			return true;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private void registryDataSource(DataSource ds, DatabaseConnection dbConfig,
			INamedDataSourceProvider dsProvider) throws RuntimeException {
		String dataSourceName = dbConfig.getName();
		if (ds == null) {
			mLogger.log(Level.SEVERE, "dataSource not initialized");
			throw new RuntimeException(dataSourceName
					+ " dataSource not initialized");
		}

		if (dataSourceName == null) {
			mLogger.log(Level.CONFIG, "Setting up dataSource url:{0},user:{1}",
					new Object[] { dbConfig.getUrl(), dbConfig.getUserName() });
			mObjectRegistry.registerInstance(DataSource.class, ds);
		} else {
			((NamedDataSourceProvider) dsProvider).putDataSource(
					dbConfig.getName(), ds);
			mLogger.log(Level.CONFIG,
					"Setting up dataSource url:{0},user:{1},name:{2}",
					new Object[] { dbConfig.getUrl(), dbConfig.getUserName(),
							dbConfig.getName() });
		}
	}

	public boolean getUseTransactionManager() {
		return useTransactionManager;
	}

	public void setUseTransactionManager(boolean useTransactionManager) {
		this.useTransactionManager = useTransactionManager;
	}

	public DatabaseConnection[] getDatabaseConnections() {
		return mDatabaseConnections;
	}

	public void setDatabaseConnections(DatabaseConnection[] DataBases) {
		mDatabaseConnections = DataBases;
	}

	/*
	 * public void onShutdown() { shutdown(); }
	 */

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void shutdown() {
		DataSource ds = (DataSource) mObjectRegistry
				.getInstanceOfType(DataSource.class);
		INamedDataSourceProvider dsProvider = (INamedDataSourceProvider) mObjectRegistry
				.getInstanceOfType(INamedDataSourceProvider.class);
		cleanDataSource(ds);
		if (dsProvider != null) {
			Map dsMap = dsProvider.getAllDataSources();
			Iterator iterator = dsMap.keySet().iterator();
			while (iterator.hasNext()) {
				ds = (DataSource) dsMap.get(iterator.next());
				cleanDataSource(ds);
			}
		}

	}

	void cleanDataSource(DataSource ds) {
		if (ds != null) {
			IDataSourceFactory dbFactory;
			try {
				dbFactory = (IDataSourceFactory) Class.forName(className)
						.newInstance();
				dbFactory.cleanDataSource(ds);
			} catch (Exception e) {
				mLogger.log(Level.SEVERE, e.getMessage(), e.getCause());
			}
			// xa unpool
			if (ds instanceof StandardXADataSource)
				((StandardXADataSource) ds).shutdown(true);
			// xa pool
			if (ds instanceof StandardXAPoolDataSource) {
				((StandardXAPoolDataSource) ds).stopPool();
			}
		}
	}

	public Connection getNativeJdbcExtractor(Connection conn) throws Exception {
		Connection nativeConn = null;
		IDataSourceFactory dbFactory;
		try {
			dbFactory = (IDataSourceFactory) Class.forName(className)
					.newInstance();
			nativeConn = dbFactory.getNativeJdbcExtractor(conn);
		} catch (Exception e) {
			throw e;
		}
		return nativeConn;
	}
}
