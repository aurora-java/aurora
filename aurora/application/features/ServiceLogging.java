/*
 * Created on 2009-4-21
 */
package aurora.application.features;

import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Handler;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.core.DirectoryConfig;
import uncertain.core.IGlobalInstance;
import uncertain.core.UncertainEngine;
import uncertain.event.IContextListener;
import uncertain.event.RuntimeContext;
import uncertain.logging.BasicFileHandler;
import uncertain.logging.ConfigurableLoggerProvider;
import uncertain.logging.ILoggerProvider;
import uncertain.logging.ILoggerProviderGroup;
import uncertain.logging.LoggerProviderGroup;
import uncertain.ocm.IConfigurable;
import uncertain.ocm.OCManager;
import aurora.service.ServiceInstance;

public class ServiceLogging extends ConfigurableLoggerProvider implements 
    IGlobalInstance, IContextListener, IConfigurable 
{
    
    private static final String SERVICE_LOGGING_FILE = "SERVICE_LOGGING_FILE";
    UncertainEngine mEngine;
    DirectoryConfig mDirConfig;
    OCManager       mOcManager;
    String          mPattern;
    CompositeMap    mConfig;
    // file name -> BasicFileHandler
    HashMap         mHandlerMap;
    boolean         mAppend;
    
    public ServiceLogging(UncertainEngine   engine){
        super();
        mEngine = engine;
        mOcManager = engine.getOcManager();
        mHandlerMap = new HashMap();
        mDirConfig = engine.getDirectoryConfig();
    }
    
    public String getLogFilePath( String prefix, CompositeMap context ){
        if(mPattern!=null) prefix = prefix + TextParser.parse(mPattern, context);  
        return prefix;
    }
    
    public String getLogFilePath(ServiceInstance svc){
        return getLogFilePath(svc.getName(), svc.getContextMap());
    }
    
    BasicFileHandler createNewHandler( String name ){
        BasicFileHandler handler = new BasicFileHandler();
        mOcManager.populateObject(mConfig, handler);
        handler.setLogFilePrefix(name);
        handler.setLogPath(getLogPath());
        return handler;
    }
    
    BasicFileHandler getLogHandler( String name ){
        BasicFileHandler handler = null;
        if(!mAppend){
            handler = createNewHandler(name);
        }else{
            handler = (BasicFileHandler)mHandlerMap.get(name);
            if(handler==null){
                handler = createNewHandler(name);
                mHandlerMap.put(name, handler);
            }
        }
        return handler;
    }

    public void onContextCreate( RuntimeContext context ){
        ServiceInstance svc = ServiceInstance.getInstance(context.getObjectContext());
        if(svc==null)
            throw new IllegalStateException("No ServiceInstance set in context");
        if( !svc.isTraceOn()) return;
        ConfigurableLoggerProvider provider = new ConfigurableLoggerProvider(getTopicManager());
        String file_name = getLogFilePath(svc);
        
        BasicFileHandler handler = getLogHandler(file_name);
        provider.addHandles( new Handler[]{handler});        
        context.setInstanceOfType(BasicFileHandler.class, handler);
        
        ILoggerProvider lp = (ILoggerProvider)context.getInstanceOfType(ILoggerProvider.class);
        if(lp==null){
            context.setInstanceOfType(ILoggerProvider.class, provider);
        }else{
            if( lp instanceof ILoggerProviderGroup){
                ((ILoggerProviderGroup)lp).addLoggerProvider(provider);
            }
            else{
                LoggerProviderGroup group = new LoggerProviderGroup();
                group.addLoggerProvider(provider);
                group.addLoggerProvider(lp);
                context.setInstanceOfType(ILoggerProvider.class, group );
            }
        }
        
        context.put(SERVICE_LOGGING_FILE, handler.getCurrentLogFile().getPath());
    }
    
    public void onContextDestroy( RuntimeContext context ){
        BasicFileHandler handler = (BasicFileHandler)context.getInstanceOfType(BasicFileHandler.class);
        if(handler!=null){
            handler.flush();
            if(!handler.getAppend())
                handler.close();
        }
    }

    /**
     * @return the pattern
     */
    public String getPattern() {
        return mPattern;
    }

    /**
     * @param pattern the pattern to set
     */
    public void setPattern(String pattern) {
        this.mPattern = pattern;
    }
    
    public void beginConfigure(CompositeMap config){
        mConfig = config;
    }
    
    /**
     * This method is called after this instance has been populated from container
     */
    public void endConfigure(){
        
    }
    
    public void onShutdown(){
        Iterator it = mHandlerMap.values().iterator();
        while(it.hasNext()){
           BasicFileHandler handler = (BasicFileHandler)it.next();
           handler.close();
        }
    }

    /**
     * @return the append
     */
    public boolean getAppend() {
        return mAppend;
    }

    /**
     * @param append the append to set
     */
    public void setAppend(boolean append) {
        mAppend = append;
    }
    
}
