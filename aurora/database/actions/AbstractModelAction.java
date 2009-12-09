/*
 * Created on 2008-6-11
 */
package aurora.database.actions;

import java.sql.Connection;

//import org.lwap.controller.MainService;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;
import aurora.database.service.BusinessModelService;
import aurora.database.service.BusinessModelServiceContext;
import aurora.database.service.DatabaseServiceFactory;

public abstract class AbstractModelAction extends AbstractEntry  {
    
    String                  model;

    //boolean                 trace;
    
    DatabaseServiceFactory  svcFactory;
    
    BusinessModelService    service;

    public AbstractModelAction( DatabaseServiceFactory  svcFactory) {
        this.svcFactory = svcFactory;
    }
    
    protected void prepareRun(ProcedureRunner runner)
        throws Exception
    {
        if(model==null)
            throw new IllegalArgumentException("Must set 'model' property");
        CompositeMap context = runner.getContext();
        service = svcFactory.getModelService(model, context);
        //service.setTrace(getTrace());
    }
    
    public BusinessModelService getService(){
        return service;
    }
    
    public DatabaseServiceFactory getServiceFactory(){
        return svcFactory;
    }

    public abstract void run(ProcedureRunner runner) throws Exception;

    /**
     * @return the model
     */
    public String getModel() {
        return model;
    }

    /**
     * @param model the model to set
     */
    public void setModel(String model) {
        this.model = model;
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
