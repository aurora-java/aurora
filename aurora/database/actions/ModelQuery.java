/*
 * Created on 2008-6-11
 */
package aurora.database.actions;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.composite.TextParser;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.OCManager;
import aurora.database.DatabaseConstant;
import aurora.database.FetchDescriptor;
import aurora.database.IResultSetConsumer;
import aurora.database.service.BusinessModelService;
import aurora.database.service.BusinessModelServiceContext;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.SqlServiceContext;

public class ModelQuery extends AbstractQueryAction {
 
    String                      model;    
    
    DatabaseServiceFactory      svcFactory;
    
    BusinessModelService        service;
    
    BusinessModelServiceContext serviceContext; 

    public ModelQuery( DatabaseServiceFactory  svcFactory, OCManager manager) {
        super(manager);
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
    
    protected void doQuery( CompositeMap param, IResultSetConsumer consumer, FetchDescriptor desc )
        throws Exception
    {
        service.query(param, consumer, desc);
    }
    
    protected void prepare( CompositeMap context )
        throws Exception
    {
        if(model==null)
            throw new IllegalArgumentException("Must set 'model' property");
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
}
