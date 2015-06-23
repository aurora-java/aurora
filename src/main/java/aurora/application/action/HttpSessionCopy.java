/*
 * Created on 2011-7-17 下午03:36:14
 * $Id$
 */
package aurora.application.action;

import java.util.Enumeration;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import uncertain.composite.CompositeMap;
import uncertain.exception.ProgrammingException;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;
import aurora.application.AuroraApplication;
import aurora.service.ServiceContext;
import aurora.service.ServiceInstance;
import aurora.service.http.HttpServiceInstance;

/**
 * Copy all fields from HttpSession to context /session path.
 * If HttpSession is not created yet, nothing will happend.
 */
public class HttpSessionCopy extends AbstractEntry {
    
    public static void copySession( CompositeMap context, HttpSession session ){
    	if(session == null)
    		return;
        ServiceContext svcctx = ServiceContext.createServiceContext(context);
        CompositeMap session_map = svcctx.getSession();
        Enumeration e = session.getAttributeNames();
        while(e.hasMoreElements()){
            String name = (String)e.nextElement();
            Object value = session.getAttribute(name);
            session_map.put(name, value);
        }        
    }

    public void run(ProcedureRunner runner) throws Exception {
        CompositeMap context = runner.getContext();
        HttpServiceInstance svc = (HttpServiceInstance)ServiceInstance.getInstance(context);
        if(svc==null)
            throw new ProgrammingException("Can't find HttpServiceInstance in current context");
        ILogger logger = LoggingContext.getLogger(context, AuroraApplication.AURORA_APP_SESSION_CHECK_LOGGING_TOPIC);
        HttpServletRequest request = svc.getRequest();
        if(request==null)
            throw new ProgrammingException("Can't get HttpServletRequest instance in current context");
        HttpSession session = request.getSession(false);
        if(session==null){
            logger.config("HTTP session not created");
            return;
        }else{
            copySession(context,session);
            logger.log(Level.CONFIG, "Session data copied to context");
        }

    }

}
