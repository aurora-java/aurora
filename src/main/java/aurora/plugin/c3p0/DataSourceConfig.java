/*
 * Created on 2009-9-6 下午09:54:26
 * Author: Zhou Fan
 */
package aurora.plugin.c3p0;

import java.util.logging.Level;

import javax.sql.DataSource;

import uncertain.core.UncertainEngine;
import uncertain.logging.ILogger;
import uncertain.ocm.IObjectRegistry;

import com.mchange.v2.c3p0.DataSources;

public class DataSourceConfig {
    
    String              mUrl;
    String              mUserName;
    String              mPassword;
    boolean             mPool;
    String              mDriverClass;
    
    DataSource          mDataSource;
    IObjectRegistry     mObjectRegistry;
    ILogger             mLogger;
    
    public DataSourceConfig( UncertainEngine engine ){
        mObjectRegistry = engine.getObjectRegistry();
        mLogger = engine.getLogger(UncertainEngine.UNCERTAIN_LOGGING_TOPIC);        
    }

    public DataSourceConfig( ILogger logger, IObjectRegistry reg ){
        mLogger = logger;
        mObjectRegistry = reg;
    }
    
    public String getUrl() {
        return mUrl;
    }
    
    public void setUrl(String url) {
        this.mUrl = url;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        this.mUserName = userName;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        this.mPassword = password;
    }
    
    public void onInitialize() throws Exception {
        if(mDriverClass!=null){
            Class.forName(mDriverClass).newInstance();            
        }
        DataSource unpooled = DataSources.unpooledDataSource(mUrl,mUserName,mPassword);
        if(mPool)
            mDataSource = DataSources.pooledDataSource( unpooled );
        else
            mDataSource = unpooled;
        mLogger.log(
                    Level.CONFIG, 
                    "Setting up pooled DataSource:{0}, database user:{1}",
                    new Object[]{mUrl, mUserName} );
        mObjectRegistry.registerInstance( DataSource.class, mDataSource );
    }
    
    public void onShutdown() throws Exception {
        if(mDataSource!=null){
            DataSources.destroy(mDataSource);            
        }
    }

    public DataSource getDataSource() {
        return mDataSource;
    }

    public boolean getPool() {
        return mPool;
    }

    public void setPool(boolean pool) {
        this.mPool = pool;
    }

    public String getDriverClass() {
        return mDriverClass;
    }

    public void setDriverClass(String driverClass) {
        this.mDriverClass = driverClass;
    }

}
