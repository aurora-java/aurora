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
import uncertain.core.ILifeCycle;
import uncertain.core.UncertainEngine;
import uncertain.event.IContextListener;
import uncertain.event.RuntimeContext;
import uncertain.exception.ProgrammingException;
import uncertain.logging.BasicFileHandler;
import uncertain.logging.DefaultPerObjectLoggingConfig;
import uncertain.logging.ILoggerProvider;
import uncertain.logging.ILoggerProviderGroup;
import uncertain.logging.IPerObjectLoggingConfig;
import uncertain.logging.LoggerProvider;
import uncertain.logging.LoggerProviderGroup;
import uncertain.ocm.IConfigurable;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.OCManager;
import aurora.application.util.ILoggingConifg;
import aurora.application.util.LoggingConfig;
import aurora.service.ServiceInstance;

public class ServiceLogging extends LoggerProvider implements 
    IGlobalInstance, IContextListener, IConfigurable,ILifeCycle
{
    
    private static final String SERVICE_LOGGING_FILE = "SERVICE_LOGGING_FILE";
    UncertainEngine mEngine;
    IObjectRegistry mRegistry;
    DirectoryConfig mDirConfig;
    OCManager       mOcManager;
    String          mPattern;
    CompositeMap    mConfig;
    // file name -> BasicFileHandler
    // HashMap         mHandlerMap;
    boolean         mAppend;
    boolean         mEnablePerServiceConfig = false;
    // instance to provide service level logging config
    IPerObjectLoggingConfig     mPerObjectLoggingConfig;
    
    public ServiceLogging(UncertainEngine   engine){
        super();
        mEngine = engine;
        mRegistry = engine.getObjectRegistry();
        mOcManager = engine.getOcManager();
        //mHandlerMap = new HashMap();
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
        return createNewHandler(name);
        /*
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
        */
    }
    
    private LoggerProvider createDefaultLoggerProvider(){
        LoggerProvider provider = new LoggerProvider(getTopicManager());
        provider.setDefaultLogLevel(getDefaultLogLevel());     
        return provider;
    }

    public void onContextCreate( RuntimeContext context ){
        
        ServiceInstance svc = ServiceInstance.getInstance(context.getObjectContext());
        if(svc==null)
            throw new IllegalStateException("No ServiceInstance set in context");
        ILoggerProvider provider = null;
        boolean is_trace = false;
        
        if(mEnablePerServiceConfig){
            String name = svc.getName();
            is_trace = mPerObjectLoggingConfig.getTraceFlag(name);
            provider = mPerObjectLoggingConfig.getLoggerProvider(name);
            if( provider==null )
                provider = createDefaultLoggerProvider();
            if(! (provider instanceof LoggerProvider) )
                throw new ProgrammingException("Must return LoggerProvider instance");
            
        }else{
            is_trace = svc.isTraceOn();
            provider = createDefaultLoggerProvider();
        }

        
        if(!is_trace)
            return;
        // initialize trace file
        String file_name = getLogFilePath(svc);        
        BasicFileHandler handler = getLogHandler(file_name);
        ((LoggerProvider)provider).addHandles( new Handler[]{handler});     

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
    
    public void onInitialize(){
        // create default IPerObjectLoggingConfig instance if not set yet
        if(mEnablePerServiceConfig){
            mPerObjectLoggingConfig = (IPerObjectLoggingConfig)mRegistry.getInstanceOfType(IPerObjectLoggingConfig.class);
            if(mPerObjectLoggingConfig==null){
                mPerObjectLoggingConfig = new DefaultPerObjectLoggingConfig();
                mRegistry.registerInstance(IPerObjectLoggingConfig.class, mPerObjectLoggingConfig);
            }
            ILoggingConifg loggingConfig = (ILoggingConifg)mRegistry.getInstanceOfType(ILoggingConifg.class);
            if(loggingConfig==null){
            	loggingConfig = new LoggingConfig(mRegistry);
                mRegistry.registerInstance(ILoggingConifg.class, loggingConfig);
            }  
        }
    }
    
    public void onShutdown(){
        /*
        Iterator it = mHandlerMap.values().iterator();
        while(it.hasNext()){
           BasicFileHandler handler = (BasicFileHandler)it.next();
           handler.close();
        }
        */
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
    
    /**
     * If set to true, trace="true" flag in service config will be ignored,
     * IPerObjectLoggingConfig instance will be created if can't get from container,
     * trace flag will be get from IPerObjectLoggingConfig
     */
    public boolean getEnablePerServiceConfig() {
        return mEnablePerServiceConfig;
    }

    public void setEnablePerServiceConfig(boolean enablePerServiceConfig) {
        this.mEnablePerServiceConfig = enablePerServiceConfig;
    }

    public String getLogPath() {
        String path = super.getLogPath();
        if(path==null)
            return mDirConfig.getLogDirectory();
        else
            return mDirConfig.translateRealPath(path);
    }

	@Override
	public boolean startup() {
		onInitialize();
		return true;
	}

	@Override
	public void shutdown() {
	}
    
    
    
}
