/*
 * Created on 2008-6-11
 */
package aurora.database.actions;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.logging.ILogger;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;
import aurora.database.service.BusinessModelService;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.SqlServiceContext;

public abstract class AbstractModelAction extends AbstractEntry  {
    
    String                  mModel;

    //boolean                 trace;
    
    DatabaseServiceFactory  mServiceFactory;
    
    BusinessModelService    mService;
    ILogger                 mLogger;  

    public AbstractModelAction( DatabaseServiceFactory  svcFactory) {
        this.mServiceFactory = svcFactory;
    }
    
    protected void prepareRun(ProcedureRunner runner)
        throws Exception
    {
        if(mModel==null)
            throw new IllegalArgumentException("Must set 'model' property");
        CompositeMap context = runner.getContext();
        
        mService = mServiceFactory.getModelService(TextParser.parse(mModel, runner.getContext()), context);
        mLogger = DatabaseServiceFactory.getLogger(context);
        //SqlServiceContext sqlContext=SqlServiceContext.createSqlServiceContext(context);
        //sqlContext.initConnection(mServiceFactory.getUncertainEngine().getObjectRegistry(), mService.getBusinessModel().getDataSourceName());        
        //service.setTrace(getTrace());
    }
    
    public BusinessModelService getService(){
        return mService;
    }
    
    public DatabaseServiceFactory getServiceFactory(){
        return mServiceFactory;
    }

    public abstract void run(ProcedureRunner runner) throws Exception;

    /**
     * @return the model
     */
    public String getModel() {
        return mModel;
    }

    /**
     * @param model the model to set
     */
    public void setModel(String model) {
        this.mModel = model;
    }
    
    protected ILogger getLogger(){
        return mLogger;
    }    
    
/*
    
    public boolean getTrace() {
        return trace;
    }

    public void setTrace(boolean trace) {
        this.trace = trace;
    }
*/
}
