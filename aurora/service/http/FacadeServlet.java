/*
 * Created on 2009-9-3 上午10:56:14
 * Author: Zhou Fan
 */
package aurora.service.http;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeMap;
import aurora.service.IService;
import aurora.service.ServiceInstance;

public class FacadeServlet extends AbstractFacadeServlet {

    // public static final String DEFAULT_RUNSCREEN_PROCEDURE =
    // "aurora.service.controller.RunScreen";

    /**
     * 
     */
    private static final long serialVersionUID = -175392627883779860L;
    HttpServiceFactory mServiceFactory;

    public static String getServiceName(HttpServletRequest request) {
        String service_name = request.getServletPath();
        if (service_name.charAt(0) == '/')
            service_name = service_name.substring(1);
        return service_name;
    }

    protected IService createServiceInstance(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final String name = getServiceName(request);
        final HttpServiceInstance svc = mServiceFactory.createHttpService(name, request, response, this);
        return svc;
    }

    protected void populateService(HttpServletRequest request,
            HttpServletResponse response, IService service) throws Exception {
        final ServiceInstance svc = (ServiceInstance) service;

        // load configure map
        final String name = svc.getName();
        final CompositeMap config = mServiceFactory.loadServiceConfig(name);
        svc.setServiceConfigData(config,false);
        // set procedure name
        final String extension = name.substring(name.lastIndexOf('.') + 1);
        final String proc = mServiceFactory.getProcedureName(extension);
        svc.getController().setProcedureName(proc);
    }

    protected void handleException(HttpServletRequest request,
            HttpServletResponse response, Throwable ex) throws IOException, ServletException {
        Throwable thr = ex.getCause();
        if (thr == null)
            thr = ex;
//        if (thr instanceof IOException)
//            response.sendError(404, request.getRequestURI());
        if (thr instanceof SAXException)
            response.sendError(500, "error when parse screen file:"
                    + thr.getMessage());
        else {
            throw new ServletException(thr);
        }
    }

    protected void cleanUp(IService svc) {
        ((ServiceInstance) svc).clear();
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        mServiceFactory = (HttpServiceFactory) mUncertainEngine
                .getObjectRegistry()
                .getInstanceOfType(HttpServiceFactory.class);
        if (mServiceFactory == null)
            throw new ServletException(
                    "No ServiceFactory instance registered in UncertainEngine");
    }

}
