/*
 * Created on 2008-5-6
 */
package aurora.database.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.sql.DataSource;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;
import uncertain.event.Configuration;
import uncertain.event.RuntimeContext;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.ProcedureRunner;
import aurora.bm.BusinessModel;
import aurora.bm.IModelFactory;
import aurora.bm.InsertSqlCreator;
import aurora.bm.ModelFactory;
import aurora.bm.QuerySqlCreator;
import aurora.bm.UpdateSqlCreator;
import aurora.database.features.AutoQueryCounter;
import aurora.database.features.OrderByClauseCreator;
import aurora.database.features.WhereClauseCreator;
import aurora.database.sql.builder.DefaultDatabaseProfile;
import aurora.database.sql.builder.IDatabaseProfile;
import aurora.database.sql.builder.ISqlBuilderRegistry;
import aurora.database.sql.builder.SqlBuilderRegistry;

public class DatabaseServiceFactory {
    
    UncertainEngine         uncertainEngine;
    IModelFactory            modelFactory;
    DataSource              dataSource;
    IDatabaseProfile        databaseProfile;
    ISqlBuilderRegistry     sqlBuilderRegistry;
    
    // Class -> Default participant instance
    Map                     defaultParticipantsMap = new HashMap();
    Configuration           globalConfig;
    
    public DatabaseServiceFactory( UncertainEngine  engine)
    {
        this.uncertainEngine = engine;
        init();
    }
    
    protected void addDefaultParticipants(){        
        QuerySqlCreator query_creator = new QuerySqlCreator( getModelFactory(), getSqlBuilderRegistry());
        setGlobalParticipant(QuerySqlCreator.class, query_creator);
        
        UpdateSqlCreator update_creator = new UpdateSqlCreator( getModelFactory(), getSqlBuilderRegistry() );
        setGlobalParticipant(UpdateSqlCreator.class, update_creator);
        
        InsertSqlCreator insert_creator = new InsertSqlCreator( getModelFactory(), getSqlBuilderRegistry() );
        setGlobalParticipant(InsertSqlCreator.class, insert_creator);
        
        WhereClauseCreator where_creator = new WhereClauseCreator(getSqlBuilderRegistry());
        setGlobalParticipant(WhereClauseCreator.class, where_creator);
        
        AutoQueryCounter auto_query_counter = new AutoQueryCounter();
        setGlobalParticipant(AutoQueryCounter.class, auto_query_counter);
        
        setGlobalParticipant(OrderByClauseCreator.class, new OrderByClauseCreator());
    }
    
    protected void init()
    {
        IObjectRegistry os = uncertainEngine.getObjectRegistry();
        
        os.registerInstance(DatabaseServiceFactory.class, this);
        
        databaseProfile = (IDatabaseProfile)os.getInstanceOfType(IDatabaseProfile.class);
        if(databaseProfile==null){
            databaseProfile = new DefaultDatabaseProfile("SQL92");
            os.registerInstance(IDatabaseProfile.class, databaseProfile);
        }
        
        sqlBuilderRegistry = (ISqlBuilderRegistry)os.getInstanceOfType(ISqlBuilderRegistry.class);
        if(sqlBuilderRegistry==null){
            sqlBuilderRegistry = new SqlBuilderRegistry(databaseProfile);
            os.registerInstance(ISqlBuilderRegistry.class, sqlBuilderRegistry);
        }

        modelFactory = (IModelFactory)os.getInstanceOfType(IModelFactory.class);
        if(modelFactory==null){
            modelFactory = new ModelFactory(uncertainEngine);
            os.registerInstance(IModelFactory.class, modelFactory);
        }
        
        dataSource = (DataSource)os.getInstanceOfType(DataSource.class);
        
        globalConfig = uncertainEngine.createConfig();
        addDefaultParticipants();
    }
    
    public Object getGlobalParticipant( Class type ){
        return defaultParticipantsMap.get(type);
    }
    
    public void setGlobalParticipant( Class type, Object instance ){
        defaultParticipantsMap.put(type, instance);
        globalConfig.addParticipant(instance);
    }
    
    public SqlServiceContext createContextWithConnection()
        throws SQLException
    {
        SqlServiceContext context = createContext();
        Connection conn = null;
        conn = dataSource.getConnection();
        context.setConnection(conn);
        return context;
    }
    
    public SqlServiceContext createContext(){
        CompositeMap map = new CompositeMap("sql-service");
        return createContext(map);
    }
    
    public SqlServiceContext createContext( CompositeMap map ){
        SqlServiceContext context = SqlServiceContext.createSqlServiceContext(map);
        return context;
    }
    
    public ProcedureRunner loadProcedure(String class_path, CompositeMap context ){
        ProcedureRunner runner = uncertainEngine.createProcedureRunner(class_path);
        runner.setContext(context);
        return runner;
    }

    /**
     * @return the databaseProfile
     */
    public IDatabaseProfile getDatabaseProfile() {
        return databaseProfile;
    }

    /**
     * @param databaseProfile the databaseProfile to set
     */
    public void setDatabaseProfile(IDatabaseProfile databaseProfile) {
        this.databaseProfile = databaseProfile;
    }

    /**
     * @return the dataSource
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * @param dataSource the dataSource to set
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @return ModelFactory instance to create BusinessModel from xml config
     */
    public IModelFactory getModelFactory() {
        return modelFactory;
    }

    /**
     * @param metadataFactory the ModelFactory to set
     */
    public void setModelFactory(IModelFactory factory) {
        this.modelFactory = factory;
    }

    /**
     * @return the sqlBuilderRegistry
     */
    public ISqlBuilderRegistry getSqlBuilderRegistry() {
        return sqlBuilderRegistry;
    }

    /**
     * @param sqlBuilderRegistry the sqlBuilderRegistry to set
     */
    public void setSqlBuilderRegistry(ISqlBuilderRegistry sqlBuilderRegistry) {
        this.sqlBuilderRegistry = sqlBuilderRegistry;
    }

    /**
     * @return the uncertainEngine
     */
    public UncertainEngine getUncertainEngine() {
        return uncertainEngine;
    }

    /**
     * @param uncertainEngine the uncertainEngine to set
     */
    public void setUncertainEngine(UncertainEngine uncertainEngine) {
        this.uncertainEngine = uncertainEngine;        
    }
    
    public BusinessModelService getModelService( String name )
        throws IOException
    {
        CompositeMap map = new CompositeMap("model-service-context");
        //RuntimeContext context = (RuntimeContext)DynamicObject.cast(map,RuntimeContext.class);
        BusinessModelService bms = getModelService( name,  map );
        return bms;
    }
    
    protected void prepareConfig( Configuration config ){
        Iterator it = defaultParticipantsMap.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry entry = (Map.Entry)it.next();
            config.addParticipant(entry.getValue());
        } 
    }
    
    
    public BusinessModelService getModelService( String name, CompositeMap context_map )
        throws IOException
    {
        if(modelFactory==null) throw new IllegalStateException("ModelFactory must be set first");
        Configuration config = uncertainEngine.createConfig();
        BusinessModel model = modelFactory.getModel(name);
        if(model==null)
            throw new IllegalArgumentException("Can't load business model "+name);
        prepareConfig(config);
        config.loadConfig(model.getObjectContext());
        BusinessModelService service = new BusinessModelService(this, config, model, context_map);
        //BusinessModelServiceContext bmsc = service.getServiceContext();
        return service;
    }
    
    public RawSqlService getSqlService(String name)
        throws IOException, SAXException
    {
        CompositeMap config = uncertainEngine.loadCompositeMap(name);
        if(config==null) throw new IOException("Can't load resource "+name);
        if(!"sql-service".equalsIgnoreCase(config.getName()))
            throw new IllegalArgumentException(name + " is not a valid sql service");
        RawSqlService service = new RawSqlService(uncertainEngine.getOcManager());
        service.mConfiguration = uncertainEngine.createConfig();
        uncertainEngine.getOcManager().populateObject(config, service);
        prepareConfig(service.mConfiguration);
        service.mConfiguration.loadConfig(config);
        return service;
    }
    
    public RawSqlService getSqlService( String name, CompositeMap context_map )
        throws IOException, SAXException
    {
        RuntimeContext svc = RuntimeContext.getInstance(context_map);
        return getSqlService( name, svc );
    }
    
    public RawSqlService getSqlService( String name, RuntimeContext context)
        throws IOException, SAXException
    {
        RawSqlService service = getSqlService(name);
        Configuration conf = context.getConfig();
        if(conf!=null)
            service.mConfiguration.setParent(conf);
        return service;
    }

    /*
    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
        if(!cacheEnabled){
            modelCompositeCache.clear();
            modelConfigCache.clear();
        }
    }
    */
    

}
