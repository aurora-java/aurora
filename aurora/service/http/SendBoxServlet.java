/*
 * Created on 2009-9-3 上午10:56:14
 * Author: Zhou Fan
 */
package aurora.service.http;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import aurora.service.IService;
import aurora.service.ServiceInstance;

public class SendBoxServlet extends FacadeServlet {

    protected void populateService(HttpServletRequest request,
            HttpServletResponse response, IService service) throws Exception {
    	final ServiceInstance svc = (ServiceInstance) service;
    	String content = request.getParameter("content");
		CompositeLoader cl = new CompositeLoader();
		final CompositeMap config = cl.loadFromString(content);
    	
        svc.setServiceConfigData(config,false);
        // set procedure name
        final String proc = mServiceFactory.getProcedureName("screen");
        svc.getController().setProcedureName(proc);
    }
}
