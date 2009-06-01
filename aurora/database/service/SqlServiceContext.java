/*
 * Created on 2007-11-22
 */
package aurora.database.service;

import java.sql.Connection;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.composite.ICompositeAccessor;
import uncertain.event.Configuration;
import aurora.database.FetchDescriptor;
import aurora.database.IResultSetConsumer;
import aurora.service.ServiceContext;

public class SqlServiceContext extends ServiceContext {
    
    public static final String KEY_RESULTSET_CONSUMER   = "ResultsetConsumer";
    
    public static final String KEY_FETCH_DESCRIPTOR     = "FetchDescriptor";
    
    public static final String KEY_SQL_STRING           = "SqlString"; 
    
    
    //public static final String KEY_COMPOSITE_ACCESSOR = "composite_accessor";

    // public static final String KEY_DATABASE_CONNECTION = "database_connection";
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
    
    //ICompositeAccessor      CompositeAccessor;

    public Connection getConnection(){
        //return (Connection)getObjectContext().get(KEY_DATABASE_CONNECTION);
        return (Connection)getInstanceOfType(Connection.class);
    }
    
    public void setConnection(Connection conn){
        //getObjectContext().put(KEY_DATABASE_CONNECTION, conn);
        setInstanceOfType(Connection.class, conn);
    }


    public ICompositeAccessor getCompositeAccessor() {
        return (ICompositeAccessor)getInstanceOfType(ICompositeAccessor.class);
    }

    public void setCompositeAccessor(ICompositeAccessor compositeAccessor) {
        setInstanceOfType(ICompositeAccessor.class, compositeAccessor);
        //getObjectContext().put(KEY_COMPOSITE_ACCESSOR, compositeAccessor);
    }
    
    public void commit() throws Exception {
        Configuration config = getConfig();
        if(config!=null) config.fireEvent("commit", null);
    }
        
    public void rollback() throws Exception {
        Configuration config = getConfig();
        if(config!=null) config.fireEvent("rollback", null);     
    }
    
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
    

}
