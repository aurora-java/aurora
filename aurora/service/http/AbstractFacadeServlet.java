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
import aurora.service.IService;

public abstract class AbstractFacadeServlet extends HttpServlet {

    UncertainEngine mUncertainEngine;
    ServletConfig mConfig;
    ServletContext mContext;

    protected abstract IService createServiceInstance(HttpServletRequest request,
            HttpServletResponse response) throws Exception;
    
    protected abstract void populateService( HttpServletRequest request,
            HttpServletResponse response, IService service )throws Exception;
    
    protected abstract void handleException( HttpServletRequest request,
            HttpServletResponse response, Exception ex ) throws IOException;

    protected abstract void cleanUp( IService service );    
    
    protected void invokeService(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        IService svc = null;
        try{
            svc = createServiceInstance( request, response );
            populateService(request,response,svc);
        }catch(Exception ex){
            mUncertainEngine.logException("Error when opening service "+request.getRequestURI(), ex);
            handleException(request,response,ex);
            return;
        }

        try {
            svc.invoke();
        } catch (Exception ex) {
            mUncertainEngine.logException("Error when executing " + request.getRequestURI(), ex);
            throw new ServletException(ex);
        } finally {
            cleanUp(svc);
        }
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
    }

}
