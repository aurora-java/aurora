/*
 * Created on 2009-9-18 下午06:33:00
 * Author: Zhou Fan
 */
package aurora.service.http;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uncertain.composite.CompositeMap;

import aurora.application.config.BaseServiceConfig;
import aurora.database.service.RawSqlService;
import aurora.service.IService;
import aurora.service.controller.ControllerProcedures;

public class AutoSqlInvokeServlet extends AbstractAutoServiceServlet {

    protected void populateService(HttpServletRequest request,
            HttpServletResponse response, IService service) throws Exception {        
        // parse requested service name
        String uri = request.getRequestURI();
        String[] args = uri.split("/");
        if (args.length < 3)
            throw new ServletException("Invalid request format");
        int start_index = args[0].length() == 0 ? 1 : 0;
        String object_name = args[start_index + 2];
        // Get HttpServiceInstance & set procedure to run
        HttpServiceInstance svc = (HttpServiceInstance) service;
        svc.getController().setProcedureName(
                ControllerProcedures.INVOKE_SERVICE);
        // Get ServiceConfig
        //CompositeMap service_config = (CompositeMap) mServiceConfig.clone();
        //BaseServiceConfig svcConfig = BaseServiceConfig.createServiceConfig(service_config);
                
        // load sql service
        RawSqlService sqlSvc = super.mDatabaseServiceFactory.getSqlService(object_name);
        if(sqlSvc==null)
            throw new IllegalArgumentException("service not found:"+object_name);
        if(sqlSvc.isQuery()){
            //svcConfig.getInitProcedureConfig().addChild(child);
        }else{
            
        }
    }

}
