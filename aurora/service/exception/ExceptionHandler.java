/*
 * Created on 2008-7-1
 */
package aurora.service.exception;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.core.IGlobalInstance;
import uncertain.ocm.ISingleton;
import uncertain.proc.ProcedureRunner;
import aurora.service.ServiceContext;

public class ExceptionHandler implements ISingleton {
    
    IExceptionDescriptor   mDesconfig;
    
    public ExceptionHandler( IExceptionDescriptor config ){
        mDesconfig = config;
    }
    
    public void onHandleException( ProcedureRunner runner ){
        Throwable thr = runner.getException();
        if(thr==null) return;
        ServiceContext   context = (ServiceContext)DynamicObject.cast(runner.getContext(), ServiceContext.class);
        CompositeMap error = mDesconfig.process(context, thr);
        if(error!=null)
            context.setError(error);
    }

}
