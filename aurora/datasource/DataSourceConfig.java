package aurora.datasource;

import java.sql.SQLException;
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
import aurora.plugin.xapool.TransactionService;
import aurora.plugin.xapool.XADataSources;
import aurora.service.IServiceFactory;
import aurora.service.ServiceFactoryImpl;
import aurora.transaction.ITransactionService;

import com.mchange.v2.c3p0.DataSources;
import com.mchange.v2.c3p0.DriverManagerDataSource;
import com.mchange.v2.c3p0.PooledDataSource;

public class DataSourceConfig implements ILifeCycle {
    DatabaseConnection[] mDatabaseConnections;
    boolean useTransactionManager = false;
    IObjectRegistry mObjectRegistry;
    ILogger mLogger;
    OCManager mOCManager;
    boolean flag=false;

    public DataSourceConfig(IObjectRegistry reg, OCManager ocManager) {
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
    public void onInitialize() {
        startup();
    }
    */

    public boolean startup() {
    	if(flag)
    		return true;
    	else	
    		flag=true;    	
        try {
            int length = mDatabaseConnections.length;
            DataSource ds = null;
            ITransactionService ts = null;
            INamedDataSourceProvider dsProvider = new NamedDataSourceProvider();
            DatabaseConnection dbConfig = null;
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
                        mOCManager.populateObject(dbConfig.config,
                                (StandardXAPoolDataSource) ds);
                    }
                    registryDataSource(ds, dbConfig, dsProvider);
                }
            } else {// c3p0
                if (length != 1) {
                    mLogger.log(Level.SEVERE,
                            "TransactionManager is disabled,please use only one datasource");
                    throw new RuntimeException(
                            "TransactionManager is disabled,please use only one datasource");
                }
                ts = new TransactionService(false);
                dbConfig = mDatabaseConnections[0];
                ds = DataSources.unpooledDataSource(dbConfig.getUrl(),
                        dbConfig.getUserName(), dbConfig.getPassword());
                ((DriverManagerDataSource) ds).setDriverClass(dbConfig
                        .getDriverClass());
                if (dbConfig.getPool() && dbConfig.config != null) {
                    ds = DataSources.pooledDataSource(ds, dbConfig.config);
                }
                registryDataSource(ds, dbConfig, dsProvider);
            }
            mObjectRegistry.registerInstance(INamedDataSourceProvider.class,
                    dsProvider);
            mObjectRegistry.registerInstance(ITransactionService.class, ts);
            // TODO to be refactor
            IServiceFactory sf = createServiceFactory(ts);
            mObjectRegistry.registerInstance(IServiceFactory.class, sf);
            mObjectRegistry.registerInstance(DataSourceConfig.class,this);
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
    public void onShutdown() {
        shutdown();
    }
    */

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
        // c3p0 pool
        if (ds != null) {
            if (ds instanceof PooledDataSource)
                try {
                    ((PooledDataSource) ds).close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
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
}
