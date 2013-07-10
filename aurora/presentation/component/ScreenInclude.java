/*
 * Created on 2011-3-22 下午05:48:06
 * $Id$
 */
package aurora.presentation.component;

import java.io.IOException;
import java.io.Writer;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.core.ConfigurationError;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.ISingleton;
import uncertain.proc.IProcedureManager;
import uncertain.proc.Procedure;
import aurora.application.ApplicationConfig;
import aurora.application.ApplicationViewConfig;
import aurora.application.IApplicationConfig;
import aurora.application.AuroraApplication;
import aurora.application.config.ScreenConfig;
import aurora.application.features.CachedScreenListener;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;
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
public class ScreenInclude implements IViewBuilder{
    
    public static final String KEY_SCREEN = "screen";

    public static CompositeMap  createScreenIncludeConfig( String screen_name ){
        CompositeMap config = new CompositeMap("a", AuroraApplication.AURORA_FRAMEWORK_NAMESPACE, "screen-include");
        config.put(KEY_SCREEN, screen_name);
        return config;
    }
    
    public static final String DEFAULT_INCLUDED_SCREEN_TEMPLATE = "defaultincludedscreentemplate";
    IObjectRegistry         mObjectRegistry;
    HttpServiceFactory      mServiceFactory;
    IProcedureManager       mProcedureManager;
    ApplicationConfig       mApplicationConfig;
    CachedScreenListener    mCacheListener;
    
    String                  mDefaultPackage = "aurora.ui.std";
    String                  mDefaultIncludedScreenTemplate = "defaultIncludedScreen";
    
    public ScreenInclude( IObjectRegistry reg ){
        mObjectRegistry = reg;
        HttpServiceFactory fact = (HttpServiceFactory)reg.getInstanceOfType(HttpServiceFactory.class);
        IProcedureManager  pm = (IProcedureManager)reg.getInstanceOfType(IProcedureManager.class);
        IApplicationConfig config = (IApplicationConfig)reg.getInstanceOfType(IApplicationConfig.class);
        mCacheListener = (CachedScreenListener)reg.getInstanceOfType(CachedScreenListener.class);
        init(fact, pm, config);
    }
    /*
    public ScreenInclude( HttpServiceFactory fact, IProcedureManager pm, IApplicationConfig config ){
        init(fact, pm, config);
    }
    */
    private void init(HttpServiceFactory fact, IProcedureManager pm, IApplicationConfig config ){
        mServiceFactory = fact;
        mProcedureManager = pm;
        mApplicationConfig = (ApplicationConfig)config;
        if (mApplicationConfig != null) {
            ApplicationViewConfig view_config = mApplicationConfig
                    .getApplicationViewConfig();
            if (view_config != null) {
                mDefaultPackage = view_config.getDefaultPackage();
                mDefaultIncludedScreenTemplate = view_config.getString(DEFAULT_INCLUDED_SCREEN_TEMPLATE);
                if(mDefaultIncludedScreenTemplate==null)
                    mDefaultIncludedScreenTemplate = "defaultIncludedScreen";
            }
        }
    }

    
    public HttpServiceInstance createSubInstance( String name, CompositeMap context)
        throws SAXException, IOException
    {
        //CompositeMap context = view_context.getModel().getRoot();
        HttpServiceInstance parent = (HttpServiceInstance)ServiceInstance.getInstance(context);
//        HttpServiceInstance svc = mServiceFactory.createHttpService(name, parent);
        
        HttpServiceInstance svc = mServiceFactory.createHttpService(name, parent.getRequest(),parent.getResponse(),parent.getServlet());
        
        
        final CompositeMap config = mServiceFactory.loadServiceConfig(name);
        ScreenConfig scc = ScreenConfig.createScreenConfig(config);
        CompositeMap view_config = scc.getViewConfig();
        view_config.put("template", mDefaultIncludedScreenTemplate);
        view_config.put("package", mDefaultPackage);
        
        svc.setServiceConfigData(config);
        svc.getController().setProcedureName(ControllerProcedures.RUN_INCLUDED_SCREEN);
        return svc;
    }
    
    public void doScreenInclude( BuildSession session, CompositeMap model, CompositeMap view, CompositeMap root)
        throws Exception
    {
        String screen_name = view.getString(KEY_SCREEN);
        if(screen_name==null)
            throw BuiltinExceptionFactory.createAttributeMissing(view.asLocatable(), "screen"); 
            //new ConfigurationError("'screen' property must be set for <screen-include>");
        //screen_name = TextParser.parse(screen_name, model);
        screen_name = session.parseString(screen_name, model );
        // Added by mark.ma -- parse parameter
        int parameterpositiion = screen_name.lastIndexOf("?");
        CompositeMap para = new CompositeMap("parameter");;
        if (parameterpositiion != -1) {
            String parameter = screen_name.substring(parameterpositiion + 1,
                    screen_name.length());
            screen_name = screen_name.substring(0, parameterpositiion);
            String[] parameters = parameter.split("&");
//            CompositeMap pcm = root.getChild("parameter");
            
            for (int i = 0; i < parameters.length; i++) {
                String p = parameters[i];
                String key = p.substring(0, p.indexOf("="));
                key = session.parseString(key, model);
                String value = p.substring(p.indexOf("=") + 1, p.length());
                value = session.parseString(value, model);
                para.put( key, value);
            }
        }        
        // end
        ServiceInstance old_inst = ServiceInstance.getInstance(root);
        // Run service
        try{
        	
            HttpServiceInstance sub_instance = createSubInstance(screen_name, root);
            sub_instance.getContextMap().replaceChild("parameter", para);
            ScreenConfig sub_config = ScreenConfig.createScreenConfig(sub_instance.getServiceConfigData());
            if(sub_config.isCacheEnabled()){
                if(mCacheListener!=null){
                    CachedScreenListener.CacheResult result = mCacheListener.getCachedContent(sub_instance);
                    if(result.isHit()){
                        Writer out = session.getWriter();
                        out.write(result.getContent().toString());
                        out.flush();
                        return;
                    }
                }
            }
            ServiceInstance.setInstance(root, sub_instance);
            Procedure proc = AbstractFacadeServlet.getProcedureToRun(mProcedureManager, sub_instance);
            if(proc!=null)
                sub_instance.invoke(proc);          
        }finally{
            ServiceInstance.setInstance(root, old_inst);
        }        
    }

    public void buildView(BuildSession session, ViewContext view_context)
            throws IOException, ViewCreationException {
        CompositeMap view = view_context.getView();
        CompositeMap model = view_context.getModel();
        try{
            doScreenInclude(session, model, view_context.getView(), model.getRoot());
        }catch(Exception ex){
            throw new ViewCreationException("aurora.presentation.component.screen_include_invoke_error", 
                    new Object[]{view.toXML()}, 
                    ex,
                    view);
        }
    }

    public String[] getBuildSteps(ViewContext context) {
        return null;
    }

}
