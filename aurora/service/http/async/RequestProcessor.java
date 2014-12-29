/*
 * Created on 2014年12月24日 下午2:35:21
 * $Id$
 */
package aurora.service.http.async;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;

import pipe.base.IEndPoint;
import uncertain.core.UncertainEngine;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.IProcedureManager;
import uncertain.proc.IProcedureRegistry;
import uncertain.proc.Procedure;
import uncertain.proc.trace.StackTraceManager;
import uncertain.proc.trace.TraceElement;
import aurora.events.E_ServiceFinish;
import aurora.service.IConfigurableService;
import aurora.service.IService;
import aurora.service.ServiceContext;
import aurora.service.ServiceThreadLocal;
import aurora.service.http.AbstractFacadeServlet;
import aurora.transaction.ITransactionService;
import aurora.transaction.UserTransactionImpl;

public class RequestProcessor implements IEndPoint {
    
    IFacadeServlet owner;

    public RequestProcessor(IFacadeServlet owner) {
        this.owner = owner;
    }
    
    public void doService(AsyncContext context)

    {
        
        HttpServletRequest request = (HttpServletRequest)context.getRequest();
        HttpServletResponse response = (HttpServletResponse)context.getResponse();
        
        UncertainEngine uncertain_engine = owner.getUncertainEngine();
        IProcedureRegistry proc_registry = owner.getProcedureRegistry();
        IProcedureManager proc_manager = owner.getProcedureManager();
        
        // create transaction
        UserTransaction trans = null;
        IObjectRegistry or = uncertain_engine.getObjectRegistry();
        ITransactionService ts = (ITransactionService) or
                .getInstanceOfType(ITransactionService.class);
        if (ts == null)
            throw new RuntimeException("ITransactionService instance not found");       
        trans = ts.getUserTransaction();
        
        // begin service
        StackTraceManager  stm = new StackTraceManager();

        IService svc = null;
        ServiceContext ctx = null;
        boolean is_success = true;      

        try {
            request.setCharacterEncoding("UTF-8");//form post encoding            
            trans.begin();
            svc = owner.createServiceInstance(request, response);
            ctx = svc.getServiceContext();
            ctx.setStackTraceManager(stm);
            ServiceThreadLocal.setCurrentThreadContext(ctx.getObjectContext());
            ServiceThreadLocal.setSource(request.getRequestURI());
            owner.populateService(request, response, svc);

            Procedure pre_service_proc=null;
            
            if (proc_registry != null) {
                pre_service_proc = proc_registry.getProcedure(AbstractFacadeServlet.PRE_SERVICE);
                //post_service_proc = mProcRegistry.getProcedure(POST_SERVICE);
            }

            
            if (pre_service_proc != null)
                is_success = svc.invoke(pre_service_proc);

            if (is_success) {
                Procedure proc = AbstractFacadeServlet.getProcedureToRun(proc_manager, svc);
                if(proc!=null){
                    if(svc instanceof IConfigurableService){
                        IConfigurableService    cfsvc = (IConfigurableService)svc;
                        if(!cfsvc.isConfigParsed())
                            cfsvc.parseConfig();
                    }
                    is_success = svc.invoke(proc);
                    if(ctx.hasError())
                        is_success = false;
                }
            }
            /*
            if (is_success) {
                if (post_service_proc != null)
                    is_success = svc.invoke(post_service_proc);
            }
            */
            
        } catch (Throwable ex) {
            is_success = false;
            /*
            if(ctx.getException()==null)
                ctx.setException(ex);
                */
            uncertain_engine.logException("Error when executing service "
                    + request.getRequestURI(), ex);
            owner.handleException(request, response, ex);
        } finally {       
            context.complete();
            if (trans instanceof UserTransactionImpl) {             
                //((UserTransactionImpl) trans).initialize(svc);
                if(svc==null)
                    throw new RuntimeException("svc is null");
                if(svc.getServiceContext()==null)
                    throw new RuntimeException("svc.getServiceContext() is null");
                ((UserTransactionImpl) trans).setContext(svc.getServiceContext().getObjectContext());
            }
            if (is_success) {
                try {
                    trans.commit();
                } catch (Throwable e) {
                    uncertain_engine.logException("Error when commit service "
                            + request.getRequestURI(), e);
                }
            } else {
                try {
                    trans.rollback();
                } catch (Throwable e) {
                    uncertain_engine.logException(
                            "Error when rollback service "
                                    + request.getRequestURI(), e);
                }
            }
            
            // release resource
            svc.release();
            
            // set overall finish time
            TraceElement elm = stm.getRootNode();
            if(elm!=null)
                elm.setExitTime(System.currentTimeMillis());
            //System.out.println(elm.asCompositeMap().toXML());
            
            // fire ServiceFinish event
            if(svc!=null)
                if(svc.getConfig()!=null)
                    try{
                        svc.getConfig().fireEvent(E_ServiceFinish.EVENT_NAME, new Object[]{svc});
                    }catch(Throwable ex){
                        uncertain_engine.logException("Error when fire ServiceFinish", ex);
                    }
            
            ServiceThreadLocal.remove();
            owner.cleanUp(svc);
            ts.stop();
        }
        
    }

    @Override
    public void process(Object data) {
        AsyncContext context = (AsyncContext)data;
        doService(context);

    }

    @Override
    public void start() {


    }

    @Override
    public void stop() {


    }

}
