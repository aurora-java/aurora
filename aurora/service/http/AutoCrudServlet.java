/*
 * Created on 2009-9-9 下午01:53:12
 * Author: Zhou Fan
 */
package aurora.service.http;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uncertain.ocm.IObjectRegistry;
import aurora.database.service.BusinessModelService;
import aurora.database.service.DatabaseServiceFactory;
import aurora.service.IService;

public class AutoCrudServlet extends AbstractFacadeServlet {
    
    HttpServiceFactory          mServiceFactory;
    DatabaseServiceFactory      mDatabaseServiceFactory;    

    protected void cleanUp(IService service) {
        // TODO Auto-generated method stub

    }

    protected IService createServiceInstance(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String uri = request.getRequestURI();
        String[] args = uri.split("/");
        if(args.length<3) throw new ServletException("Invalid request format");
        int start_index = args[0].length()==0?1:0;
        String object_name = args[start_index+1];
        String action_name = args[start_index+2];
        BusinessModelService svc = mDatabaseServiceFactory.getModelService(object_name);
        //HttpServiceInstance svc = mServiceFactory.createHttpService(name, request, response, servlet)
        return null;
    }

    protected void handleException(HttpServletRequest request,
            HttpServletResponse response, Exception ex) throws IOException {
        
    }

    protected void populateService(HttpServletRequest request,
            HttpServletResponse response, IService service) throws Exception {
        
    }
    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        IObjectRegistry reg = mUncertainEngine.getObjectRegistry();    
        mServiceFactory = (HttpServiceFactory) reg.getInstanceOfType(HttpServiceFactory.class);
        mDatabaseServiceFactory = (DatabaseServiceFactory)reg.getInstanceOfType(DatabaseServiceFactory.class);
        if (mServiceFactory == null)
            throw new ServletException(
                    "No ServiceFactory instance registered in UncertainEngine");
        if (mDatabaseServiceFactory == null)
            throw new ServletException(
                    "No DatabaseServiceFactory instance registered in UncertainEngine");        
    }    


}
