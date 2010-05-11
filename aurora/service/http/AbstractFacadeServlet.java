/*
 * Created on 2009-9-3 上午10:56:14
 * Author: Zhou Fan
 */
package aurora.service.http;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;

import uncertain.core.UncertainEngine;
import uncertain.event.Configuration;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.IProcedureManager;
import uncertain.proc.IProcedureRegistry;
import uncertain.proc.Procedure;
import aurora.events.E_DetectProcedure;
import aurora.service.IService;
import aurora.service.ServiceController;
import aurora.transaction.ITransactionService;

public abstract class AbstractFacadeServlet extends HttpServlet {

    public static final String POST_SERVICE = "post-service";
    public static final String PRE_SERVICE = "pre-service";
    UncertainEngine     mUncertainEngine;
    IProcedureManager   mProcManager;
    ServletConfig       mConfig;
    ServletContext      mContext;
    IProcedureRegistry   mProcRegistry;
    
    Procedure           mPreServiceProc;
    Procedure           mPostServiceProc;

    protected abstract IService createServiceInstance(HttpServletRequest request,
            HttpServletResponse response) throws Exception;
    
    protected abstract void populateService( HttpServletRequest request,
            HttpServletResponse response, IService service )throws Exception;
    
    protected abstract void handleException( HttpServletRequest request,
            HttpServletResponse response, Exception ex ) throws IOException;

    protected abstract void cleanUp( IService service );  
    
    protected Procedure getProcedureToRun( IService service )
        throws Exception
    {
        String procedure_name = null;
        Configuration config = service.getConfig();
        config.fireEvent(E_DetectProcedure.EVT_DETECT_PROCEDURE, new Object[]{ service } );
        ServiceController controller = ServiceController.createServiceController(service.getServiceContext().getObjectContext());
        procedure_name = controller.getProcedureName();
        if(procedure_name==null);
        Procedure proc = mProcManager.loadProcedure(procedure_name);
        return proc;
    }
    
    protected boolean invokeService(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        IService svc = null;
        boolean is_success = true;
        try{
            svc = createServiceInstance( request, response );
            // Default procedure name will be determined by sub class's populateService()
            populateService(request,response,svc);
        }catch(Exception ex){
            is_success = false;
            mUncertainEngine.logException("Error when opening service "+request.getRequestURI(), ex);
            handleException(request,response,ex);
            return is_success;
        }
        try {
            if(mPreServiceProc!=null)
                is_success = is_success & svc.invoke(mPreServiceProc);
            if(!is_success)
                return false;
            Procedure proc = getProcedureToRun(svc);
            is_success=is_success&svc.invoke(proc);
            if(!is_success)
                return false;
            if(mPostServiceProc!=null)
                is_success = is_success & svc.invoke(mPostServiceProc);
        } catch (Exception ex) {
            mUncertainEngine.logException("Error when executing " + request.getRequestURI(), ex);
            throw new ServletException(ex);
        } finally {
            cleanUp(svc);
        }
        return is_success;
    }

    protected void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    	IObjectRegistry or=mUncertainEngine.getObjectRegistry();
        ITransactionService ts=(ITransactionService)or.getInstanceOfType(ITransactionService.class);
     	UserTransaction trans=ts.getUserTransaction();
     	try {
			trans.begin();		
			if(invokeService(request, response)){        	
				trans.commit();			 
			}else{
				trans.rollback();
			}
     	}catch(Exception e){
     		mUncertainEngine.logException("Error when executing " + request.getRequestURI(), e);
     	}finally{
     		ts.stop();
     	}            	
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        mConfig = config;
        mContext = config.getServletContext();
        mUncertainEngine = WebContextInit.getUncertainEngine(mContext);
        if (mUncertainEngine == null)
            throw new ServletException("Uncertain engine not initialized");
        mProcManager = mUncertainEngine.getProcedureManager();
        
        mProcRegistry = (IProcedureRegistry)mUncertainEngine.getObjectRegistry().getInstanceOfType(IProcedureRegistry.class);
        if(mProcRegistry!=null){
            mPreServiceProc = mProcRegistry.getProcedure(PRE_SERVICE);
            mPostServiceProc = mProcRegistry.getProcedure(POST_SERVICE);
        }
    }

}
