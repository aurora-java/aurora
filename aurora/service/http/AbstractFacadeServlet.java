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

import uncertain.core.UncertainEngine;
import uncertain.event.Configuration;
import uncertain.proc.IProcedureManager;
import uncertain.proc.IProcedureRegistry;
import uncertain.proc.Procedure;
import uncertain.proc.ProcedureRegistry;
import aurora.application.Events;
import aurora.service.IService;
import aurora.service.ServiceController;

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
        config.fireEvent(Events.EVT_DETECT_PROCEDURE, new Object[]{ service } );
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
            svc.invoke(proc);
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
        invokeService(request, response);
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
