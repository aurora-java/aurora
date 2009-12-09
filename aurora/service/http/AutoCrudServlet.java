/*
 * Created on 2009-9-9 下午01:53:12
 * Author: Zhou Fan
 */
package aurora.service.http;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uncertain.composite.CompositeMap;
import aurora.application.config.ScreenConfig;
import aurora.database.actions.config.ActionConfigManager;
import aurora.database.actions.config.ModelQueryConfig;
import aurora.service.IService;
import aurora.service.controller.ControllerProcedures;

public class AutoCrudServlet extends AbstractAutoServiceServlet {

    protected void populateService(HttpServletRequest request,
            HttpServletResponse response, IService service) throws Exception {
        HttpServiceInstance svc = (HttpServiceInstance) service;
        svc.getController().setProcedureName(
                ControllerProcedures.INVOKE_SERVICE);
        String uri = request.getRequestURI();
        String[] args = uri.split("/");
        if (args.length < 3)
            throw new ServletException("Invalid request format");
        int start_index = args[0].length() == 0 ? 1 : 0;
        String object_name = args[start_index + 2];
        String action_name = args[start_index + 3];
        CompositeMap service_config = (CompositeMap) mServiceConfig.clone();
        svc.setName(object_name + "_" + action_name);
        ScreenConfig screen = ScreenConfig.createScreenConfig(service_config);
        if ("query".equals(action_name)) {
            ModelQueryConfig mq = ActionConfigManager
                    .createModelQuery(object_name);
            mq.setParameters(svc.getServiceContext().getParameter());
            screen.getInitProcedureConfig().addChild(0, mq.getObjectContext());
        }
        /*
         * else if("update".equals(action_name)){
         * 
         * }
         */
        svc.setServiceConfigData(service_config);
        // BusinessModelService bmsvc =
        // mDatabaseServiceFactory.getModelService(object_name);

    }

}
