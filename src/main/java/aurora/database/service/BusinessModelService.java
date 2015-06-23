/*
 * Created on 2008-5-14
 */
package aurora.database.service;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.core.ConfigurationError;
import uncertain.event.Configuration;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.OCManager;
import uncertain.proc.ProcedureRunner;
import aurora.bm.BusinessModel;
import aurora.bm.Operation;
import aurora.database.DBUtil;
import aurora.database.DatabaseConstant;
import aurora.database.FetchDescriptor;
import aurora.database.IResultSetConsumer;
import aurora.database.ParsedSql;
import aurora.database.SqlRunner;
import aurora.database.rsconsumer.CompositeMapCreator;
import aurora.database.sql.ISqlStatement;
import aurora.database.sql.IStatementWithParameter;
import aurora.service.ServiceContext;
import aurora.service.exception.ExceptionDescriptorConfig;
import aurora.service.validation.IParameterIterator;
import aurora.service.validation.ParameterParser;
import aurora.service.validation.ValidationException;

public class BusinessModelService {
    
    //ConcurrentAccessChecker checker = new ConcurrentAccessChecker();

    public static final String PROC_EXECUTE_DML = "aurora.database.service.bm.execute_dml";

    public static final String PROC_QUERY = "aurora.database.service.bm.query";

    public static final String PROC_CREATE_SQL = "aurora.database.service.bm.create_sql";
    
    // source BM
    BusinessModel mBusinessModel;
    
    // exception descriptor
    ExceptionDescriptorConfig   mExceptionProcessor;
    
    // Configuration associated with BusinessModel
    Configuration mConfig;

    // owner
    DatabaseServiceFactory mServiceFactory;
    
    // context
    CompositeMap mContextMap;
    
    // context casted as BusinessModelServiceContext
    BusinessModelServiceContext mServiceContext;

    // ProcedureRunner to run procs
    ProcedureRunner mRunner;

    // saved original config
    Configuration mOldConfig = null;
    
    // object registry to get instances
    IObjectRegistry     mObjectRegistry;
    
    // OCManager
    OCManager           mOcManager;
    
    // logger
    ILogger mLogger;  

    protected BusinessModelService(DatabaseServiceFactory factory,
            Configuration config, BusinessModel model, CompositeMap context_map)
            throws Exception {
        this.mConfig = config;
        this.mBusinessModel = model;
        this.mServiceFactory = factory;
        this.mObjectRegistry = factory.getObjectRegistry();
        this.mOcManager = (OCManager)mObjectRegistry.getInstanceOfType(OCManager.class);
        setContextMap(context_map);
        mLogger = LoggingContext.getLogger(mServiceContext
                .getObjectContext(),
                DatabaseConstant.AURORA_DATABASE_LOGGING_TOPIC);  
        
        CompositeMap cfg = model.getExceptionDescriptorConfig();
        if(cfg!=null){
            mExceptionProcessor = (ExceptionDescriptorConfig)mOcManager.createObject(cfg);
            if(mExceptionProcessor==null)
                throw new ConfigurationError("Can't create "+ExceptionDescriptorConfig.class.getName()+" instance from config:"+cfg.toXML());
        }
    }

    protected void prepareForRun(String proc_name) throws ValidationException, SQLException {
        /*
         * String operation = proc_name.substring(proc_name.lastIndexOf('.')+1);
         * if(operation==null) operation = proc_name;
         * mServiceContext.setOperation(operation);
         */
        mRunner = mServiceFactory.loadProcedure(proc_name, mContextMap);
        parseParameter(mServiceContext);
        mServiceContext.prepareForRun();
        mServiceContext.initConnection(mObjectRegistry, mBusinessModel.getDataSourceName());
        mServiceContext.put("BusinessModel", mBusinessModel);
        
        if(mExceptionProcessor!=null){
            mRunner.addExceptionHandle(mExceptionProcessor.asExceptionHandle());
            /*
            mRunner.addExceptionHandle( new IExceptionHandle() {
                
                public boolean handleException(ProcedureRunner runner, Throwable exception) {
                    CompositeMap msg = mExceptionProcessor.process(mServiceContext, exception);
                    if(msg!=null){
                        mServiceContext.setError(msg);
                        mServiceContext.putBoolean("success", false);
                        mRunner.setResumeAfterException(true);
                        mServiceContext.setSuccess(true);
                        return true;
                    }
                    return false;
                }
            });
            */            
        }
        
        mRunner.setSaveStackTrace(false);

    }

    public void setServiceContext(ServiceContext context) {
        setContextMap(context.getObjectContext());
    }

    public void setContextMap(CompositeMap map) {
        this.mContextMap = map;
        if (mServiceContext == null)
            mServiceContext = new BusinessModelServiceContext();
        mServiceContext.initialize(mContextMap);
        mServiceContext.setBusinessModel(mBusinessModel);
    }

    public BusinessModelServiceContext getServiceContext() {
        return mServiceContext;
    }

    public ProcedureRunner getRunner() {
        return mRunner;
    }

    public BusinessModel getBusinessModel() {
        return mBusinessModel;
    }

    private void pushConfig() {
        mOldConfig = mServiceContext.getConfig();
        mServiceContext.setConfig(mConfig);
    }

    private void popConfig() {
        mServiceContext.setConfig(mOldConfig);
        mOldConfig = null;
    }

    public StringBuffer getSql(String operation) throws Exception {
        mServiceContext.setOperation(operation);
        // invoke create_sql procedure
        pushConfig();
        try {
            prepareForRun(PROC_CREATE_SQL);
            mRunner.run();
            mRunner.checkAndThrow();
        } finally {
            popConfig();
        }
        return mServiceContext.getSqlString();
    }

    public void query(Map parameters, IResultSetConsumer consumer,
            FetchDescriptor desc) throws Exception {
        // Configuration old_config = context.getConfig();
        pushConfig();
        try {
            mServiceContext.setOperation("query");
            prepareForRun(PROC_QUERY);
            if (parameters != null)
                mServiceContext.setCurrentParameter(parameters);
            mServiceContext.setResultsetConsumer(consumer);
            mServiceContext.setFetchDescriptor(desc);
            mRunner.setSaveStackTrace(false);
            mRunner.run();
            mRunner.checkAndThrow();
        } finally {
            popConfig();
            printTraceInfo();
        }
    }

    public CompositeMap queryAsMap(Map parameters, FetchDescriptor desc)
            throws Exception {
        CompositeMapCreator map_creator = new CompositeMapCreator();
        query(parameters, map_creator, desc);
        return map_creator.getCompositeMap();
    }

    public CompositeMap queryAsMap(Map parameters) throws Exception {
        FetchDescriptor desc = FetchDescriptor.fetchAll();
        return queryAsMap(parameters, desc);
    }

    public CompositeMap queryIntoMap(Map parameters, FetchDescriptor desc,
            CompositeMap root) throws Exception {
        CompositeMapCreator map_creator = new CompositeMapCreator(root);
        query(parameters, map_creator, desc);
        return map_creator.getCompositeMap();
    }

    public void query() throws Exception {
        CompositeMap param = mServiceContext.getCurrentParameter();
        IResultSetConsumer consumer = mServiceContext.getResultsetConsumer();
        if (consumer == null)
            throw new IllegalStateException(
                    "IResultSetConsumer instance is not set in service context");
        FetchDescriptor desc = mServiceContext.getFetchDescriptor();
        if (desc == null)
            desc = FetchDescriptor.createFromParameter(mServiceContext
                    .getParameter());
        query(param, consumer, desc);
    }

    protected void runProcedure(Map parameters, String proc_name)
            throws Exception {
        pushConfig();
        try {
            if (parameters != null)
                mServiceContext.setCurrentParameter(parameters);
            prepareForRun(proc_name);
           
            mRunner.run();
            mRunner.checkAndThrow();
        }finally {
            popConfig();
            printTraceInfo();
        }
    }

    public void executeDml(Map parameters, String operation) throws Exception {
        //checker.checkThread();
        /*
         * Operation op = mBusinessModel.getOperation(operation); if(op!=null);
         */
        mServiceContext.getObjectContext().put("SqlStatementType",
                new StringBuffer(operation));
        mServiceContext.setOperation(operation);
        runProcedure(parameters, PROC_EXECUTE_DML);
    }

    public void updateByPK(Map parameters) throws Exception {
        executeDml(parameters, "Update");
    }

    public void insert(Map parameters) throws Exception {
        executeDml(parameters, "Insert");
    }

    public void deleteByPK(Map parameters) throws Exception {
        executeDml(parameters, "Delete");
    }
    
    public void execute(Map parameters) throws Exception {
        executeDml(parameters, "Execute");
    }

    public void parseParameter(ServiceContext context)
            throws ValidationException {
        String operation = mServiceContext.getOperation();
        CompositeMap parameter = context.getCurrentParameter();
        boolean parsed = parameter.getBoolean(
                ServiceContext.KEY_PARAMETER_PARSED, false);
        if (!parsed) {
            ParameterParser parser = ParameterParser.getInstance();
            List exceptions = null;           
            IParameterIterator params = mBusinessModel
                    .getParameterForOperation(operation);
            if (params != null) {
                exceptions = parser.parse(parameter, params);
                if (exceptions != null) {
                    ValidationException exp = new ValidationException(
                            parameter, exceptions);
                    throw exp;
                }
                parameter.putBoolean(ServiceContext.KEY_PARAMETER_PARSED, true);
            }
        }
    }

    void printTraceInfo() {
        ILogger logger = LoggingContext.getLogger(mServiceContext
                .getObjectContext(),
                DatabaseConstant.AURORA_DATABASE_LOGGING_TOPIC);
        SqlRunner runner = mServiceContext.getSqlRunner();
        DBUtil.printTraceInfo(mServiceContext.getOperation(), logger, runner);
    }

    public Configuration getConfiguration() {
        return mConfig;
    }
    
    public void onCreateSqlRunner( StringBuffer sql ) {
        //ThreadLocalUtil.appendDebugInfo(mServiceContext.getObjectContext().toXML());
        //ThreadLocalUtil.put("CURRENT_CONFIG", mServiceContext.getObjectContext().toXML());
        if(sql==null)
            throw new IllegalStateException("No SQL created in context");
        BusinessModelServiceContext bmsc = mServiceContext;
        Collection parameters = null;
        ISqlStatement stmt = bmsc.getStatement();
        if (stmt != null)
            if (stmt instanceof IStatementWithParameter)
                parameters = ((IStatementWithParameter) stmt).getParameters();
        ParsedSql s = new ParsedSql();
        if (parameters != null)
            s.defineParameters(parameters, true);
        List param_list = mBusinessModel.getParameterForOperationInList(mServiceContext.getOperation());
        s.defineParameters(param_list, false);
        s.parse(sql.toString());
        SqlRunner runner =createSqlRunner(bmsc,s);
        bmsc.setSqlRunner(runner);        
    }
    public static SqlRunner createSqlRunner(BusinessModelServiceContext bmsc,ParsedSql parsedSql){
    	SqlServiceContext context = null;
        CompositeMap root = bmsc.getObjectContext().getRoot();
        if (root != null)
            context = SqlServiceContext.createSqlServiceContext(root);
        else
            context = bmsc;
        SqlRunner runner = new SqlRunner(context, parsedSql);
        runner.setConnectionName(bmsc.getBusinessModel().getDataSourceName());        
    	return runner;
    }

    public void onExecuteDmlStatement(SqlRunner runner) throws Exception {
        runner.update(mServiceContext.getCurrentParameter());
    }

    public void onDecideSqlGenerationMode(String operation) {
        // check if there exists a predefined sql
        String predefined_sql = null;
        Operation op = mBusinessModel.getOperation(operation);
        if (op != null) {
            predefined_sql = op.getSql();
        }
        if (predefined_sql != null) {
            mServiceContext.setSqlGenerationMode("Predefined");
            mServiceContext.setSqlString(new StringBuffer(predefined_sql));
        } else {
            mServiceContext.setSqlGenerationMode("AutoGenerate");
            mServiceContext.setStatementType(operation);
            mServiceContext.setSqlString(null);
        }

    }
}
