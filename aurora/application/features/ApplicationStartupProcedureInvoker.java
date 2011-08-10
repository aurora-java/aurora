/*
 * Created on 2011-7-24 下午09:57:00
 * $Id$
 */
package aurora.application.features;

import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;
import uncertain.event.Configuration;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.IProcedureRegistry;
import uncertain.proc.Procedure;
import aurora.application.AuroraApplication;
import aurora.service.IServiceFactory;
import aurora.service.ServiceInstance;
import aurora.service.ServiceInvoker;

public class ApplicationStartupProcedureInvoker extends AbstractLocatableObject {
    
    public static final String PROCEDURE_NAME_APPLICATION_START = "application-start";

    IObjectRegistry         mRegistry;
    IServiceFactory         mServiceFactory;
    IProcedureRegistry      mProcRegistry;
    UncertainEngine         mEngine;


    public ApplicationStartupProcedureInvoker(IObjectRegistry mRegistry) {
        super();
        this.mRegistry = mRegistry;
        mEngine = (UncertainEngine)mRegistry.getInstanceOfType(UncertainEngine.class);
    }
    
    public void postInitialize()
        throws Exception
    {
        
        ILogger logger = LoggingContext.getLogger(AuroraApplication.AURORA_APP_LOGGING_TOPIC, mRegistry);
        
        mServiceFactory = (IServiceFactory)mRegistry.getInstanceOfType(IServiceFactory.class);
        if(mServiceFactory==null)
            throw BuiltinExceptionFactory.createInstanceNotFoundException(this, IServiceFactory.class);
        
        mProcRegistry = (IProcedureRegistry)mRegistry.getInstanceOfType(IProcedureRegistry.class);
        
        
        if(mProcRegistry!=null){
            
            Procedure app_proc = mProcRegistry.getProcedure(PROCEDURE_NAME_APPLICATION_START);
            if(app_proc!=null){
                String source = app_proc.getOriginSource()==null?"":app_proc.getOriginSource();
                logger.info("Running application startup procedure from "+ source);
                CompositeMap context = new CompositeMap();
                
                String service_name = "application_init";
                try{
                    ServiceInvoker.invokeProcedureWithTransaction(service_name, app_proc, mServiceFactory, context);
                }finally{
                    context.clear();
                }
                
            }
        }

    }

}
