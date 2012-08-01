/*
 * Created on 2008-6-11
 */
package aurora.database.actions;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.composite.TextParser;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.OCManager;
import aurora.bm.BusinessModel;
import aurora.database.DatabaseConstant;
import aurora.database.FetchDescriptor;
import aurora.database.IResultSetConsumer;
import aurora.database.service.BusinessModelService;
import aurora.database.service.BusinessModelServiceContext;
import aurora.database.service.DatabaseServiceFactory;

public class ModelQuery extends AbstractQueryAction {
 
    String                      model;    
    
    DatabaseServiceFactory      svcFactory;
    
    BusinessModelService        service;
    
    BusinessModelServiceContext serviceContext; 

    public ModelQuery( DatabaseServiceFactory  svcFactory, OCManager manager, IObjectRegistry reg) {
        super(manager, reg);
        this.svcFactory = svcFactory;
    }
    
    public BusinessModelService getService(){
        return service;
    }
    
    public DatabaseServiceFactory getServiceFactory(){
        return svcFactory;
    }

    /**
     * @return the model
     */
    public String getModel() {
        return model;
    }
    
    public String getName(){
        return super.getName()+"[" + getModel() + "]";
    }
    
    protected void doQuery( CompositeMap param, IResultSetConsumer consumer, FetchDescriptor desc )
        throws Exception
    {
    	if(getAttribFromRequest()){            	
        	BusinessModel bm=service.getBusinessModel();
        	
        	if(bm.getAllowFetchAll()!=null&&bm.getAllowFetchAll().booleanValue()==false){
        		fetchAll=false; 
        		desc.setFetchAll(fetchAll);
        	}
        	
        	if(!fetchAll){
	        	int maxPageSize=-1;
	        	if(bm.getMaxPageSize()!=null){        		
	        		maxPageSize=bm.getMaxPageSize().intValue();        		
	        	}else{
	        		Object defaultPageSize=svcFactory.getDatabaseFactory().getProperty("maxpagesize");
	        		if(defaultPageSize!=null)
	        			maxPageSize=Integer.parseInt((String)defaultPageSize);
	        	}
	        	if(maxPageSize!=-1&&desc.getPageSize()>maxPageSize&&pageSize==null)        		
	    			desc.setPageSize(maxPageSize);
        	}
        }
        service.query(param, consumer, desc);
    }
    
    protected void prepare( CompositeMap context )
        throws Exception
    {
        if(model==null)
            throw BuiltinExceptionFactory.createAttributeMissing(this, "model");
        ILogger logger = LoggingContext.getLogger(context, DatabaseConstant.AURORA_DATABASE_LOGGING_TOPIC);
        String parsed_model = TextParser.parse(model, context);
        logger.config("===================================== prepare to run model-query "+parsed_model+"==============================");
        service = svcFactory.getModelService(parsed_model, context);
        setConnectionName(service.getBusinessModel().getDataSourceName());
        /*
        SqlServiceContext
        .createSqlServiceContext(context).initConnection(svcFactory.getUncertainEngine().getObjectRegistry(), connectionName);
        */ 
        //service.setTrace(getTrace());    
        serviceContext = (BusinessModelServiceContext)DynamicObject.cast(context, BusinessModelServiceContext.class);
    }  
    
    protected void cleanUp( CompositeMap context ){
    }

    /**
     * @param model the model to set
     */
    public void setModel(String model) {
        this.model = model;
    }
/*
    public String getRootPath() {
        // TODO Auto-generated method stub
        String path =  super.getRootPath();
        if(path==null)
            return service.getBusinessModel().getBaseTable();
        else
            return path;
    }
*/

    public void beginConfigure(CompositeMap config) {
        super.beginConfigure(config);
    }
    
}
