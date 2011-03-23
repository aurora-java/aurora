/*
 * Created on 2011-3-22 下午05:48:06
 * $Id$
 */
package aurora.presentation.component;

import java.io.IOException;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeMap;
import uncertain.core.ConfigurationError;
import uncertain.ocm.ISingleton;
import uncertain.proc.IProcedureManager;
import uncertain.proc.Procedure;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;
import aurora.service.IServiceFactory;
import aurora.service.ServiceInstance;
import aurora.service.controller.ControllerProcedures;
import aurora.service.http.AbstractFacadeServlet;
import aurora.service.http.HttpServiceFactory;
import aurora.service.http.HttpServiceInstance;

/**
 * Build an existing screen at current position
 * <code>
 * <a:screen-include screen="sys/sysFunction.screen">
 *  <a:variables>
 *      <a:variable path="/parameter@order_id" sourcePath="/model/workflow/@instance_param" />
 *  </a:variables>
 * </a:screen-include> 
 * </code>
 */
public class ScreenInclude implements IViewBuilder, ISingleton {
    
    HttpServiceFactory      mServiceFactory;
    IProcedureManager       mProcedureManager;
    
    public ScreenInclude( HttpServiceFactory fact, IProcedureManager pm ){
        mServiceFactory = (HttpServiceFactory)fact;
        mProcedureManager = pm;
    }
    
    public HttpServiceInstance createSubInstance( String name, ViewContext view_context)
        throws SAXException, IOException
    {
        CompositeMap context = view_context.getModel().getRoot();
        HttpServiceInstance parent = (HttpServiceInstance)ServiceInstance.getInstance(context);
        HttpServiceInstance svc = mServiceFactory.createHttpService(name, parent);
        final CompositeMap config = mServiceFactory.loadServiceConfig(name);
        svc.setServiceConfigData(config);
        svc.getController().setProcedureName(ControllerProcedures.RUN_INCLUDED_SCREEN);
        return svc;
    }

    public void buildView(BuildSession session, ViewContext view_context)
            throws IOException, ViewCreationException {
        // Get screen name
        CompositeMap view = view_context.getView();
        String screen_name = view.getString("screen");
        if(screen_name==null)
            throw new ConfigurationError("'screen' property must be set for <screen-include>");
        screen_name = session.parseString(screen_name, view_context.getModel());
        // Run service
        try{
            HttpServiceInstance sub_instance = createSubInstance(screen_name, view_context);
            Procedure proc = AbstractFacadeServlet.getProcedureToRun(mProcedureManager, sub_instance);
            sub_instance.invoke(proc);
        }catch(Exception ex){
            throw new ViewCreationException("Error when invoking screen config file +" + screen_name,ex);            
        }
    }

    public String[] getBuildSteps(ViewContext context) {
        return null;
    }

}
