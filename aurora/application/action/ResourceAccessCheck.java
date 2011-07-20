/*
 * Created on 2011-7-16 下午09:18:31
 * $Id$
 */
package aurora.application.action;

import java.util.logging.Level;

import uncertain.composite.CompositeMap;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.exception.ProgrammingException;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;
import aurora.application.AuroraApplication;
import aurora.security.IResourceAccessChecker;

public class ResourceAccessCheck extends AbstractEntry {
    
    IResourceAccessChecker mChecker;
    
    String                 resultPath;
    String                 serviceNamePath = "@service_name";

    /**
     * @param mChecker
     */
    public ResourceAccessCheck(IResourceAccessChecker mChecker) {
        super();
        this.mChecker = mChecker;
    }

    public void run(ProcedureRunner runner) throws Exception {
        if(resultPath==null)
            throw BuiltinExceptionFactory.createAttributeMissing(this, "resultPath");
        CompositeMap context = runner.getContext();
        ILogger logger = LoggingContext.getLogger(context, AuroraApplication.AURORA_APP_SESSION_CHECK_LOGGING_TOPIC);
        String service_name = (String)context.getObject(serviceNamePath);
        if(service_name==null)
            throw new ProgrammingException("'service_name' from "+serviceNamePath+" is null");
        
        String result = mChecker.checkAccess(service_name, context);
        
        logger.log(Level.CONFIG, "Access check result for {0} => {1}", new Object[]{service_name, result} );
        context.putObject(resultPath, result, true);
    }

    public String getResultPath() {
        return resultPath;
    }

    public void setResultPath(String resultPath) {
        this.resultPath = resultPath;
    }

    public String getServiceNamePath() {
        return serviceNamePath;
    }

    public void setServiceNamePath(String serviceNamePath) {
        this.serviceNamePath = serviceNamePath;
    }

}
