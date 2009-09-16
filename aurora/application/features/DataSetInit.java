/*
 * Created on 2009-9-16 上午11:22:05
 * Author: Zhou Fan
 */
package aurora.application.features;

import aurora.application.config.ScreenConfig;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.service.ServiceInstance;
import uncertain.composite.CompositeMap;
import uncertain.proc.ProcedureRunner;

public class DataSetInit {
    
    public void onInitService( ProcedureRunner runner ){
        CompositeMap context = runner.getContext();
        ServiceInstance svc = ServiceInstance.getInstance(context);
        ScreenConfig screen = ScreenConfig.createScreenConfig(svc.getServiceConfigData());
        
        CompositeMap data = screen.getDataSetsConfig();
        System.out.println(data.toXML());
        
        
    }
    
    public void onPreparePageContent( BuildSession session, ViewContext context ){
        System.out.println("page content:"+context.getContextMap().toXML());
    }

}
