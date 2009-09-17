/*
 * Created on 2009-9-16 上午11:22:05
 * Author: Zhou Fan
 */
package aurora.application.features;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

import aurora.application.config.ScreenConfig;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.service.ServiceInstance;
import uncertain.composite.CompositeMap;
import uncertain.proc.ProcedureRunner;

public class DataSetInit {
    
    CompositeMap data ;
    
    public void onInitService( ProcedureRunner runner ){
        CompositeMap context = runner.getContext();
        ServiceInstance svc = ServiceInstance.getInstance(context);
        ScreenConfig screen = ScreenConfig.createScreenConfig(svc.getServiceConfigData());
        
        data = screen.getDataSetsConfig();
        System.out.println(data.toXML());
        
        
    }
    
    public void onPreparePageContent( BuildSession session, ViewContext context )
        throws Exception
    {
        

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(baos);
        BuildSession _session = new BuildSession(session.getPresentationManager());
        _session.setWriter(writer);
        _session.buildViews(context.getModel(), data.getChilds());   
        context.getMap().put("dataset.init", baos.toString());
        

    }

}
