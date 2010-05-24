/*
 * Created on 2007-11-22
 */
package aurora.database.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import javax.sql.DataSource;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.composite.ICompositeAccessor;
import uncertain.core.UncertainEngine;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;
import aurora.database.DBUtil;
import aurora.database.FetchDescriptor;
import aurora.database.IResultSetConsumer;
import aurora.datasource.INamedDataSourceProvider;
import aurora.datasource.NamedDataSourceProvider;
import aurora.service.ServiceContext;

public class SqlServiceContext extends ServiceContext {
    
    public static final String KEY_RESULTSET_CONSUMER   = "ResultsetConsumer";
    
    public static final String KEY_FETCH_DESCRIPTOR     = "FetchDescriptor";
    
    public static final String KEY_SQL_STRING           = "SqlString"; 
    
    
    //public static final String KEY_COMPOSITE_ACCESSOR = "composite_accessor";

    public static final String KEY_DATABASE_CONNECTION = "__database_connection";
    public static final String KEY_DATABASE_ALL_CONNECTION = "__database_all_connection";
    public static final String KEY_SERVICE_OPTION = "__database_service_option";
    
    public static SqlServiceContext createSqlServiceContext( CompositeMap context_map ){
        SqlServiceContext context = new SqlServiceContext();
        context.initialize(context_map);
        return context;
    }
    
    public static SqlServiceContext createSqlServiceContext( Connection conn ){
        CompositeMap map = new CompositeMap("sql-service");
        SqlServiceContext context = createSqlServiceContext(map);
        context.setConnection(conn);
        return context;
    }
    
    public DataSource getContextDataSource(){
        return (DataSource)getInstanceOfType(DataSource.class);
    }
    
    public Set getAllConnection(){
        return (HashSet)super.get(KEY_DATABASE_ALL_CONNECTION);
    }
    public void setConnection(Connection conn){
    	setInstanceOfType(Connection.class, conn);
    	Set databaseAllConnection=getAllConnection();  
    	if(databaseAllConnection==null)
    		databaseAllConnection=new HashSet();    	
    	databaseAllConnection.add(conn);    	  
    	super.put(KEY_DATABASE_ALL_CONNECTION, databaseAllConnection);
    }
   
    public Connection getConnection(){ 
    	return (Connection)getInstanceOfType(Connection.class);
    }     
    public void setNamedConnection(String name,Connection conn){
    	 String key = KEY_DATABASE_CONNECTION + "." + name;
    	 super.put(key, conn);
    	 Set databaseAllConnection=getAllConnection();    	
    	 if(databaseAllConnection==null)
     		databaseAllConnection=new HashSet();
    	 databaseAllConnection.add(conn);     	    
     	 super.put(KEY_DATABASE_ALL_CONNECTION, databaseAllConnection);  	
    }
    
    public Connection getNamedConnection(String name) throws SQLException{
        String key = KEY_DATABASE_CONNECTION + "." + name;      
    	return (Connection)super.get(key);
    }    
 
    public ICompositeAccessor getCompositeAccessor() {
        return (ICompositeAccessor)getInstanceOfType(ICompositeAccessor.class);
    }

    public void setCompositeAccessor(ICompositeAccessor compositeAccessor) {
        setInstanceOfType(ICompositeAccessor.class, compositeAccessor);
        //getObjectContext().put(KEY_COMPOSITE_ACCESSOR, compositeAccessor);
    }
    /*
    public void commit() throws Exception {
        Configuration config = getConfig();
        if(config!=null) config.fireEvent("commit", null);
    }
        
    public void rollback() throws Exception {
        Configuration config = getConfig();
        if(config!=null) config.fireEvent("rollback", null);     
    }
    */
    public ServiceOption getServiceOption(){
        Object obj = get(KEY_SERVICE_OPTION);
        if( obj instanceof ServiceOption )
            return (ServiceOption)obj;
        else if( obj instanceof CompositeMap )
            return (ServiceOption)DynamicObject.cast((CompositeMap)obj, ServiceOption.class);
        else
            return null;
    }
    
    public void setServiceOption( ServiceOption opt){
        put(KEY_SERVICE_OPTION, opt);
    }
    
    public IResultSetConsumer getResultsetConsumer(){
        return (IResultSetConsumer)get(KEY_RESULTSET_CONSUMER);
    }
    
    public void setResultsetConsumer(IResultSetConsumer consumer){
        put(KEY_RESULTSET_CONSUMER, consumer);
    }
    
    public FetchDescriptor getFetchDescriptor(){
        return (FetchDescriptor)get(KEY_FETCH_DESCRIPTOR);
    }
    
    public void setFetchDescriptor(FetchDescriptor desc){
        put(KEY_FETCH_DESCRIPTOR, desc);
    }
    
    public StringBuffer getSqlString(){
        return (StringBuffer)get(KEY_SQL_STRING);
    }
    
    public void setSqlString( StringBuffer sql){
        put(KEY_SQL_STRING, sql);
    }
    public void initConnection(IObjectRegistry reg,String datasourceName) throws SQLException{
    	Connection conn;
    	DataSource ds;    	
    	ILogger mLogger =LoggingContext.getLogger("aurora.database.service", reg);    	
    	if(datasourceName==null){
	    	conn=getConnection();
	    	if(conn==null){
	    		ds=(DataSource)reg.getInstanceOfType(DataSource.class);
	    		if(ds==null){	    			
	    			mLogger.log(Level.SEVERE, "No DataSource instance configured in engine");
	    			throw new IllegalStateException("No DataSource instance configured in engine");
	    		}
	    		conn=ds.getConnection();
	    		if(conn.getAutoCommit())
    				conn.setAutoCommit(false);
	    		setConnection(conn);
	    	}
    	}else{           
            conn=getNamedConnection(datasourceName);
    		if(conn==null){
    			NamedDataSourceProvider dsProvider=(NamedDataSourceProvider)reg.getInstanceOfType(INamedDataSourceProvider.class);
    			if(dsProvider==null){
    				mLogger.log(Level.SEVERE, "No NamedDataSourceProvider instance not configured in engine");    			
    				throw new IllegalStateException("No NamedDataSourceProvider instance not configured in engine");
    			}
    			ds=dsProvider.getDataSource(datasourceName);
    			if(ds==null){
    				mLogger.log(Level.SEVERE, datasourceName+" DataSource instance not configured in engine");    			
	    			throw new IllegalStateException(datasourceName+" DataSource instance not configured in engine");
    			}
    			conn=ds.getConnection();
    			if(conn.getAutoCommit())
    				conn.setAutoCommit(false);
    			setNamedConnection(datasourceName, conn);
    		}
    	}    	
    }
    public void freeConnection()
        throws SQLException
    {
    	Connection conn;
    	Set databaseAllConnection=getAllConnection();
    	if(databaseAllConnection!=null){
    		Iterator it=databaseAllConnection.iterator();
    		while(it.hasNext()){
    			conn=(Connection)it.next();
    			DBUtil.closeConnection(conn);
    		}
    	}        
    }
}
