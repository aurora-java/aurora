/*
 * Created on 2011-7-26 下午12:00:10
 * $Id$
 */
package aurora.security;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.IProcedureRegistry;
import uncertain.proc.Procedure;
import aurora.application.action.HttpSessionCopy;
import aurora.service.IServiceFactory;
import aurora.service.ServiceInvoker;
import aurora.service.http.WebContextInit;

public class HttpSessionCleaner implements HttpSessionListener {
    
    public static final String SERVICE_NAME = "session-destroy";

    public void sessionCreated(HttpSessionEvent event ) {

    }

    public void sessionDestroyed(HttpSessionEvent event) {
        
        HttpSession session = event.getSession();
        ServletContext context = session.getServletContext();
        
        UncertainEngine engine = WebContextInit.getUncertainEngine(context);
        IObjectRegistry reg = engine.getObjectRegistry();
        IServiceFactory factory = (IServiceFactory)reg.getInstanceOfType(IServiceFactory.class);
        IProcedureRegistry pr = (IProcedureRegistry)reg.getInstanceOfType(IProcedureRegistry.class);
        
        if(factory==null)
            throw BuiltinExceptionFactory.createInstanceNotFoundException(null, IServiceFactory.class, this.getClass().getName());
        
        if(pr==null)
            throw BuiltinExceptionFactory.createInstanceNotFoundException(null, IProcedureRegistry.class, this.getClass().getName());
        
        Procedure proc = pr.getProcedure(SERVICE_NAME);
        if(proc==null)
            throw new IllegalArgumentException("Must set a procedure named "+SERVICE_NAME+" in service-procedure.config");
        CompositeMap m = new CompositeMap("context");
        try{
            HttpSessionCopy.copySession(m, session);
            ServiceInvoker.invokeProcedureWithTransaction(SERVICE_NAME, proc, factory, m);
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }finally{
            m.clear();
        }

        
    }

}
