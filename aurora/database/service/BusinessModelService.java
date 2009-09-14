/*
 * Created on 2008-5-14
 */
package aurora.database.service;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.event.Configuration;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.proc.ProcedureRunner;
import aurora.bm.BusinessModel;
import aurora.database.CompositeMapCreator;
import aurora.database.Constant;
import aurora.database.DBUtil;
import aurora.database.FetchDescriptor;
import aurora.database.IResultSetConsumer;
import aurora.database.SqlRunner;
import aurora.service.ServiceContext;
import aurora.service.validation.IParameterIterator;
import aurora.service.validation.ParameterParser;
import aurora.service.validation.ValidationException;

public class BusinessModelService {
    
    public static final String PROC_UPDATE = "aurora.database.service.bm.update";

    public static final String PROC_DELETE = "aurora.database.service.bm.delete";

    public static final String PROC_INSERT = "aurora.database.service.bm.insert";
    
    public static final String PROC_QUERY = "aurora.database.service.bm.query";

    public static final String PROC_EXECUTE = "aurora.database.service.bm.execute";    

    Configuration                       config;
    BusinessModel                       model;
    DatabaseServiceFactory              serviceFactory;
    BusinessModelServiceContext         context;  
    CompositeMap                        context_map;
    ProcedureRunner                     runner;    
    String                              action = null;
    boolean                             trace;
    
    protected BusinessModelService(DatabaseServiceFactory factory, Configuration config, BusinessModel model, CompositeMap context_map ) {
        this.config = config;
        this.model = model;
        this.serviceFactory = factory;
        setContextMap(context_map);
    }
 
    protected void prepareForRun( String proc_name )
        throws ValidationException
    {
        action = proc_name.substring(proc_name.lastIndexOf('.')+1);
        if(action==null) action = proc_name;
        context.setAction(action);
        runner = serviceFactory.loadProcedure(proc_name, context_map);
        parseParameter(context);
    }
    
    public void  setContextMap( CompositeMap map ){
        this.context_map = map;
        if(context==null)
            context = new BusinessModelServiceContext();
        context.initialize(context_map);
        context.setBusinessModel(model);
    }

    public BusinessModelServiceContext getServiceContext(){
        return context;
    }
    
    public ProcedureRunner getRunner(){
        return runner;
    }
    
    public BusinessModel    getBusinessModel(){
        return model;
    }
    
    private void pushConfig(){
        Configuration old_config = context.getConfig();
        if(old_config!=null){
            config.setParent(old_config);
            context.setConfig(config);
        }
    }
    
    private void popConfig(){
        Configuration old_config = config.getParent();
        if(old_config!=null){
            context.setConfig(old_config);
            config.setParent(null);
        }
    }
    
    public void query( Map parameters,  IResultSetConsumer consumer, FetchDescriptor desc )
        throws Exception
    {
        //Configuration old_config = context.getConfig();
        pushConfig();
        try{
            prepareForRun(PROC_QUERY);
            if(parameters!=null) context.setCurrentParameter(parameters);            
            context.setResultsetConsumer(consumer);
            context.setFetchDescriptor(desc);
            runner.run();
            runner.checkAndThrow();
        } finally{            
            popConfig();
            printTraceInfo();            
        }
    }
    
    public CompositeMap queryAsMap( Map parameters, FetchDescriptor desc )
        throws Exception
    {
        CompositeMapCreator map_creator = new CompositeMapCreator();
        query(parameters, map_creator, desc);
        return map_creator.getCompositeMap();
    }
    
    public CompositeMap queryIntoMap( Map parameters, FetchDescriptor desc, CompositeMap root)
        throws Exception
    {
        CompositeMapCreator map_creator = new CompositeMapCreator(root);
        query(parameters, map_creator, desc);
        return map_creator.getCompositeMap();
    }
    
    public void query()
        throws Exception
    {
        CompositeMap        param = context.getCurrentParameter();
        IResultSetConsumer  consumer = context.getResultsetConsumer();
        if(consumer==null) throw new IllegalStateException("IResultSetConsumer instance is not set in service context");
        FetchDescriptor     desc = context.getFetchDescriptor();
        if(desc==null)
            desc = FetchDescriptor.createFromParameter(context.getParameter());
        query(param,consumer,desc);
    }
    
    public void updateByPK( Map parameters )
        throws Exception
    {
        pushConfig();
        try{
            prepareForRun(PROC_UPDATE);
            if(parameters!=null) context.setCurrentParameter(parameters);
            runner.run();
            runner.checkAndThrow();
        }finally{
            popConfig();
            printTraceInfo();             
        }
    }
    
    public void parseParameter( ServiceContext context )
        throws ValidationException
    {
        CompositeMap parameter = context.getCurrentParameter();
        boolean parsed = parameter.getBoolean(ServiceContext.KEY_PARAMETER_PARSED, false);
        if(!parsed){
            ParameterParser parser = ParameterParser.getInstance();
            List exceptions = null;
            IParameterIterator params = model.getParameterForAction(action);
            if(params!=null){
                exceptions = parser.parse( parameter,  params );
                if(exceptions!=null){ 
                    ValidationException exp = new ValidationException(parameter,exceptions);
                    throw exp;
                }
                parameter.putBoolean(ServiceContext.KEY_PARAMETER_PARSED, true);
            }
        }
    }

    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(String action) {
        this.action = action;
    }

    void printTraceInfo(){
        ILogger logger = LoggingContext.getLogger(context.getObjectContext(), Constant.AURORA_DATABASE_LOGGING_TOPIC);
        SqlRunner runner = context.getSqlRunner();
        /*
        SqlRunner runner = context.getSqlRunner();
        if(!getTrace()||runner==null) return;
        DBUtil.printTraceInfo( action, new PrintWriter(System.out), runner);
        */
        DBUtil.printTraceInfo(action, logger, runner);
    }    
    /**
     * @return the trace
     */
    public boolean getTrace() {
        return trace;
    }

    /**
     * @param trace the trace to set
     */
    public void setTrace(boolean trace) {
        this.trace = trace;
    } 

}
