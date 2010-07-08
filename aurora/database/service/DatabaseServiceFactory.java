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
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.IProcedureManager;
import uncertain.proc.Procedure;
import uncertain.proc.ProcedureRunner;
import aurora.bm.BusinessModel;
import aurora.bm.DeleteSqlCreator;
import aurora.bm.IModelFactory;
import aurora.bm.InsertSqlCreator;
import aurora.bm.ModelFactory;
import aurora.bm.QuerySqlCreator;
import aurora.bm.UpdateSqlCreator;
import aurora.database.DatabaseConstant;
import aurora.database.features.AutoQueryCounter;
import aurora.database.features.LookUpField;
import aurora.database.features.OrderByClauseCreator;
import aurora.database.features.WhereClauseCreator;
import aurora.database.profile.IDatabaseFactory;
import aurora.events.E_PrepareBusinessModel;

public class DatabaseServiceFactory {

    UncertainEngine uncertainEngine;
    IModelFactory modelFactory;
    DataSource dataSource;
    IDatabaseFactory databaseFactory;
    IProcedureManager       mProcedureManager;
    // IDatabaseProfile databaseProfile;
    // ISqlBuilderRegistry sqlBuilderRegistry;

    // Class -> Default participant instance
    Map defaultParticipantsMap = new HashMap();
    Configuration globalConfig;

    public static ILogger getLogger(CompositeMap context) {
        ILogger logger = LoggingContext.getLogger(context,
                DatabaseConstant.AURORA_DATABASE_LOGGING_TOPIC);
        return logger;
    }

    public DatabaseServiceFactory(UncertainEngine engine) {
        this.uncertainEngine = engine;
        this.mProcedureManager = engine.getProcedureManager();
        init();
    }

    protected void addDefaultParticipants() {
        QuerySqlCreator query_creator = new QuerySqlCreator(getModelFactory(),
                getDatabaseFactory());
        setGlobalParticipant(QuerySqlCreator.class, query_creator);

        UpdateSqlCreator update_creator = new UpdateSqlCreator(
                getModelFactory(), getDatabaseFactory());
        setGlobalParticipant(UpdateSqlCreator.class, update_creator);

        InsertSqlCreator insert_creator = new InsertSqlCreator(
                getModelFactory(), getDatabaseFactory());
        setGlobalParticipant(InsertSqlCreator.class, insert_creator);

        WhereClauseCreator where_creator = new WhereClauseCreator(
                getDatabaseFactory());
        setGlobalParticipant(WhereClauseCreator.class, where_creator);

        AutoQueryCounter auto_query_counter = new AutoQueryCounter();
        setGlobalParticipant(AutoQueryCounter.class, auto_query_counter);

        DeleteSqlCreator delete_creator = new DeleteSqlCreator(
                getModelFactory(), getDatabaseFactory());
        setGlobalParticipant(DeleteSqlCreator.class, delete_creator);
        
        LookUpField lookupfiled = new LookUpField(databaseFactory,uncertainEngine.getObjectRegistry());
        setGlobalParticipant(LookUpField.class, lookupfiled);

        setGlobalParticipant(OrderByClauseCreator.class,
                new OrderByClauseCreator());
    }

    protected void init() {
        IObjectRegistry os = uncertainEngine.getObjectRegistry();

        os.registerInstance(DatabaseServiceFactory.class, this);
        /*
         * databaseProfile =
         * (IDatabaseProfile)os.getInstanceOfType(IDatabaseProfile.class);
         * if(databaseProfile==null){ databaseProfile = new
         * DatabaseProfile("SQL92"); os.registerInstance(IDatabaseProfile.class,
         * databaseProfile); }
         * 
         * sqlBuilderRegistry =
         * (ISqlBuilderRegistry)os.getInstanceOfType(ISqlBuilderRegistry.class);
         * if(sqlBuilderRegistry==null){ sqlBuilderRegistry = new
         * SqlBuilderRegistry(databaseProfile);
         * os.registerInstance(ISqlBuilderRegistry.class, sqlBuilderRegistry); }
         */
        modelFactory = (IModelFactory) os
                .getInstanceOfType(IModelFactory.class);
        if (modelFactory == null) {
            modelFactory = new ModelFactory(uncertainEngine.getOcManager());
            os.registerInstance(IModelFactory.class, modelFactory);
        }

        dataSource = (DataSource) os.getInstanceOfType(DataSource.class);

        globalConfig = uncertainEngine.createConfig();

    }

    public Object getGlobalParticipant(Class type) {
        return defaultParticipantsMap.get(type);
    }

    public void setGlobalParticipant(Class type, Object instance) {
        if (!defaultParticipantsMap.containsKey(type)) {
            defaultParticipantsMap.put(type, instance);
            globalConfig.addParticipant(instance);
        }
    }
    
    public void setGlobalParticipant( Object instance ){
        setGlobalParticipant( instance.getClass(), instance );
    }

    public SqlServiceContext createContextWithConnection() throws SQLException {
        SqlServiceContext context = createContext();
        Connection conn = null;
        conn = dataSource.getConnection();
        context.setConnection(conn);
        return context;
    }

    public SqlServiceContext createContext() {
        CompositeMap map = new CompositeMap("sql-service");
        return createContext(map);
    }

    public SqlServiceContext createContext(CompositeMap map) {
        SqlServiceContext context = SqlServiceContext
                .createSqlServiceContext(map);
        return context;
    }

    public ProcedureRunner loadProcedure(String class_path, CompositeMap context) {
        try{
            ProcedureRunner runner = uncertainEngine.createProcedureRunner();
            Procedure proc = mProcedureManager.loadProcedure(class_path);
            if(proc==null)
                throw new IllegalArgumentException("Can't load procedure "+class_path);            
            runner.setProcedure(proc);
            /*
            ProcedureRunner runner = uncertainEngine
                    .createProcedureRunner(class_path);
                    */
            runner.setContext(context);
            return runner;
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }catch(SAXException ex){
            throw new RuntimeException(ex);
        }
    }

    /**
     * @return the dataSource
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * @param dataSource
     *            the dataSource to set
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
     * @param metadataFactory
     *            the ModelFactory to set
     */
    public void setModelFactory(IModelFactory factory) {
        this.modelFactory = factory;
    }

    /**
     * @return the uncertainEngine
     */
    public UncertainEngine getUncertainEngine() {
        return uncertainEngine;
    }

    /**
     * @param uncertainEngine
     *            the uncertainEngine to set
     */
    public void setUncertainEngine(UncertainEngine uncertainEngine) {
        this.uncertainEngine = uncertainEngine;
    }

    public BusinessModelService getModelService(String name) throws IOException {
        CompositeMap map = new CompositeMap("model-service-context");
        BusinessModelService bms = getModelService(name, map);
        return bms;
    }

    protected void prepareConfig(Configuration config) {
        Iterator it = defaultParticipantsMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            config.addParticipant(entry.getValue());
        }
    }
    
    public BusinessModelService getModelService(BusinessModel model,
            CompositeMap context_map) throws IOException {
        Configuration config = uncertainEngine.createConfig();
        prepareConfig(config);
        config.loadConfig(model.getObjectContext());
        BusinessModelService service = null;
        try {
            service = new BusinessModelService(this, config, model, context_map);
            config.addParticipant(service);
            config.fireEvent(E_PrepareBusinessModel.EVENT_NAME, new Object[]{model} );            
        } catch (Exception ex) {
            throw new RuntimeException(
                    "Error when creating business model service " + model.getName(), ex);
        }        
        return service;
    }    

    public BusinessModelService getModelService(CompositeMap bm_config,
            CompositeMap context_map) throws IOException {
        BusinessModel model = modelFactory.getModel(bm_config);
        return getModelService(model, context_map);
    }
    
    public BusinessModelService getModelService(String name,
            CompositeMap context_map) throws IOException {
        if (modelFactory == null)
            throw new IllegalStateException("ModelFactory must be set first");
        BusinessModel model = modelFactory.getModel(name);
        if (model == null)
            throw new IllegalArgumentException("Can't load business model "
                    + name);
        return getModelService(model, context_map);
    }

    public RawSqlService getSqlService(String name) throws IOException,
            SAXException {
        CompositeMap config = uncertainEngine.loadCompositeMap(name);
        if (config == null)
            throw new IOException("Can't load resource " + name);
        if (!"sql-service".equalsIgnoreCase(config.getName()))
            throw new IllegalArgumentException(name
                    + " is not a valid sql service");
        RawSqlService service = new RawSqlService(uncertainEngine
                .getOcManager());
        service.mConfiguration = uncertainEngine.createConfig();
        uncertainEngine.getOcManager().populateObject(config, service);
        prepareConfig(service.mConfiguration);
        service.mConfiguration.loadConfig(config);
        return service;
    }

    public RawSqlService getSqlService(String name, CompositeMap context_map)
            throws IOException, SAXException {
        RuntimeContext svc = RuntimeContext.getInstance(context_map);
        return getSqlService(name, svc);
    }

    public RawSqlService getSqlService(String name, RuntimeContext context)
            throws IOException, SAXException {
        RawSqlService service = getSqlService(name);
        Configuration conf = context.getConfig();
        if (conf != null)
            service.mConfiguration.setParent(conf);
        return service;
    }

    public IDatabaseFactory getDatabaseFactory() {
        return databaseFactory;
    }

    public void setDatabaseFactory(IDatabaseFactory databaseFactory) {
        this.databaseFactory = databaseFactory;
        addDefaultParticipants();
        //ParticipantManager pm = databaseFactory.getParticipantManager();
    }
    
    public IObjectRegistry getObjectRegistry(){
        return uncertainEngine==null?null:uncertainEngine.getObjectRegistry();
    }

    /*
     * public boolean isCacheEnabled() { return cacheEnabled; }
     * 
     * public void setCacheEnabled(boolean cacheEnabled) { this.cacheEnabled =
     * cacheEnabled; if(!cacheEnabled){ modelCompositeCache.clear();
     * modelConfigCache.clear(); } }
     */

}
