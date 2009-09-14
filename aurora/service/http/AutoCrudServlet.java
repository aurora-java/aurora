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

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import aurora.database.actions.config.ActionConfigManager;
import aurora.database.actions.config.ModelQueryConfig;
import aurora.database.service.DatabaseServiceFactory;
import aurora.service.IService;
import aurora.service.controller.ControllerProcedures;

public class AutoCrudServlet extends FacadeServlet {
    
    static final String CONFIG_PROTOTYPE_FILE = AutoCrudServlet.class.getPackage().getName()+".EmptyServiceConfig";
    
    DatabaseServiceFactory      mDatabaseServiceFactory; 
    CompositeMap                mServiceConfig;
    CompositeLoader             mCompositeLoader;
    
    protected void handleException(HttpServletRequest request,
            HttpServletResponse response, Exception ex) throws IOException {
        
    }

    protected void populateService(HttpServletRequest request,
            HttpServletResponse response, IService service) throws Exception {
        HttpServiceInstance svc = (HttpServiceInstance)service;
        svc.getController().setProcedureName(ControllerProcedures.INVOKE_SERVICE);
        String uri = request.getRequestURI();
        String[] args = uri.split("/");
        if(args.length<3) throw new ServletException("Invalid request format");
        int start_index = args[0].length()==0?1:0;
        String object_name = args[start_index+2];
        String action_name = args[start_index+3];
        CompositeMap service_config = (CompositeMap)mServiceConfig.clone();
        if("query".equals(action_name)){
            ModelQueryConfig mq = ActionConfigManager.createModelQuery(object_name);
            mq.setRootPath("list");            
            service_config.getChild("init-procedure").addChild(0,mq.getObjectContext());
            svc.setServiceConfigData(service_config);
        }
        //BusinessModelService bmsvc = mDatabaseServiceFactory.getModelService(object_name);
        
    }
    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        IObjectRegistry reg = mUncertainEngine.getObjectRegistry();    
        mDatabaseServiceFactory = (DatabaseServiceFactory)reg.getInstanceOfType(DatabaseServiceFactory.class);
        if (mDatabaseServiceFactory == null)
            throw new ServletException(
                    "No DatabaseServiceFactory instance registered in UncertainEngine");
        mCompositeLoader = CompositeLoader.createInstanceForOCM();
        try{
            mServiceConfig = mCompositeLoader.loadFromClassPath(CONFIG_PROTOTYPE_FILE, "xml");
        }catch(Exception ex){
            throw new ServletException("Can't load builtin resource:"+CONFIG_PROTOTYPE_FILE, ex);
        }
    }    


}
