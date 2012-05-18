/*
 * Created on 2009-9-2 下午08:17:54
 * Author: Zhou Fan
 */
package aurora.service.http;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xml.sax.SAXException;

import uncertain.cache.CacheFactoryConfig;
import uncertain.cache.ICache;
import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;
import uncertain.event.Configuration;
import uncertain.event.IParticipantManager;
import aurora.application.action.HttpSessionCopy;
import aurora.application.features.HttpRequestTransfer;
import aurora.service.controller.ControllerProcedures;

public class HttpServiceFactory {
    
    public static final String KEY_WEB_RESOURCE_CACHE = "WebResource";

    public static final String KEY_PROCEDURE_MAPPING = "procedure-mapping";

    public static final String KEY_PROCEDURE = "procedure";

    public static final String KEY_EXTENSION = "extension";

    static String BUILTIN_PROCEDURE_PACKAGE = ControllerProcedures.class.getPackage().getName();
    
    static Map  BUILTIN_MAPPING = new CompositeMap(KEY_PROCEDURE_MAPPING);
    static
    {
        BUILTIN_MAPPING.put("screen", ControllerProcedures.RUN_SCREEN);
        BUILTIN_MAPPING.put("svc", ControllerProcedures.INVOKE_SERVICE);
    }
    
    private CompositeLoader createCompositeLoader(){
        CompositeLoader l = new CompositeLoader();
        l.setSaveNamespaceMapping(true);
        l.ignoreAttributeCase();
        ICache c = CacheFactoryConfig.getNamedCache(mUncertainEngine.getObjectRegistry(), KEY_WEB_RESOURCE_CACHE);
        if(c!=null){
            l.setCache(c);
            l.setCacheEnabled(true);
        }
        return l;
    }
    
    /**
     * @param uncertainEngine
     */
    public HttpServiceFactory(UncertainEngine uncertainEngine) {
        super();
        mUncertainEngine = uncertainEngine;
        mCompositeLoader = createCompositeLoader();
        mParticipantManager = (IParticipantManager)mUncertainEngine.getObjectRegistry().getInstanceOfType(IParticipantManager.class);
        if(mParticipantManager!=null){
            mServiceParentConfig = mParticipantManager.getParticipantsAsConfig(IParticipantManager.SERVICE_SCOPE);
        }
        //mCompositeLoader.setCacheEnabled(true);
    }

    UncertainEngine      mUncertainEngine;
    CompositeLoader      mCompositeLoader;
    IParticipantManager  mParticipantManager;
    Map                  mProcedureMapping = BUILTIN_MAPPING;
    Configuration        mServiceParentConfig;
    
    public CompositeLoader getCompositeLoader(){
        return mCompositeLoader;
    }
    
    public CompositeMap loadServiceConfig( String name )
        throws IOException, SAXException
    {
        CompositeMap service_config = mCompositeLoader.loadByFile(name);
        return service_config;
    }
    
    /**
     * Create instance by specify name of server
     * @param name
     * @param request
     * @param response
     * @param servlet
     * @return
     */
    public HttpServiceInstance createHttpService( String name, HttpServletRequest request, HttpServletResponse response, HttpServlet servlet )
    {
        HttpServiceInstance svc = new HttpServiceInstance( name, mUncertainEngine.getProcedureManager() );
        svc.setRequest(request);
        svc.setResponse(response);
        svc.setServlet(servlet);   
        svc.setRootConfig(mServiceParentConfig);
        HttpRequestTransfer.copyRequest(svc);
        HttpSessionCopy.copySession(svc.getContextMap(), request.getSession(false));
        return svc;
    }
    
    public HttpServiceInstance createHttpService( String name, HttpServiceInstance parent )
    {
        HttpServiceInstance svc = new HttpServiceInstance( name, mUncertainEngine.getProcedureManager() );
        svc.setRequest(parent.getRequest());
        svc.setResponse(parent.getResponse());
        svc.setServlet(parent.getServlet());
        svc.setRootConfig(mServiceParentConfig);
        svc.setContextMap(parent.getContextMap());
        return svc;
    }
    
    /**
     * Add mapping between file extension and procedure to run
     * @param config A CompositeMap with childs containing mapping config, such as
     * <code>
     *   <mapping extension="screen" procedure="RunScreen" />
     * </code>
     */
    public void addProcedureMapping( CompositeMap config ){
        mProcedureMapping = new CompositeMap(KEY_PROCEDURE_MAPPING);
        Iterator it = config.getChildIterator();
        while(it.hasNext()){
            CompositeMap item = (CompositeMap)it.next();
            String extension = item.getString(KEY_EXTENSION);
            String procedure = item.getString(KEY_PROCEDURE);
            mProcedureMapping.put(extension, procedure);
        }
        
    }
    
    public String getProcedureName( String type ){
        return (String)mProcedureMapping.get(type);
    }

}
