/*
 * Created on 2009-9-1
 */
package aurora.service;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.event.Configuration;
import uncertain.event.IEventDispatcher;
import uncertain.event.RuntimeContext;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.proc.IProcedureManager;
import uncertain.proc.Procedure;
import uncertain.proc.ProcedureRunner;
import aurora.database.service.SqlServiceContext;
import aurora.events.E_PrepareServiceConfig;

public class ServiceInstance implements IService, IConfigurableService {

    public static final String KEY_SERVICE_NAME = "service_name";

    public static final String LOGGING_TOPIC = "aurora.application";

    protected IProcedureManager mProcManager;

    boolean     mContextInited = false;
    boolean     mConfigParsed = false;
    protected CompositeMap mContextMap;
    protected ServiceContext mServiceContext;
    protected ServiceController mController;

    protected Configuration mRootConfig;
    protected Configuration mConfig;
    protected CompositeMap mConfigMap;
    protected ProcedureRunner mRunner;

    private Object[] mEventArgs = { this };
    
    protected List<IResourceReleaser> mResourceReleasers = new LinkedList<IResourceReleaser>();
    
    static final String INSTANCE_KEY = RuntimeContext.getTypeKey(IService.class);

    public static ServiceInstance getInstance(CompositeMap context) {
        return (ServiceInstance) context.get(INSTANCE_KEY);
    }
    
    public static void setInstance( CompositeMap context, IService inst ){
        context.put(INSTANCE_KEY, inst);
    }

    public ServiceInstance(String name, IProcedureManager proc_manager) {
        // mConfigMap = config_map;
        mProcManager = proc_manager;
        CompositeMap context = new CompositeMap("context");
        setContextMap(context);
        setName(name);
        mConfig = mProcManager.createConfig();
    }

    /** Global participants can do service config population before it is parsed */
    public void parseConfig() {
        if (mConfig != null)
            mConfig.clear();
        if (mRootConfig != null)
            try {
                mRootConfig.fireEvent(E_PrepareServiceConfig.EVENT_NAME, mEventArgs);
            } catch (Exception ex) {
                throw new RuntimeException(
                        "Error in event PopulateServiceConfig", ex);
            }
        mConfig = mProcManager.createConfig();
        mConfig.addParticipant(this);
        mConfig.loadConfig(mConfigMap);
        if (mRootConfig != null)
            mConfig.setParent(mRootConfig);
        mConfigParsed = true;
    }
    
    void initContext(){
        if(!mContextInited){
            mProcManager.initContext(mContextMap);            
            mContextInited = true;
        }
    }

    public ServiceContext getServiceContext() {
        return mServiceContext;
    }

    public void setServiceContext(ServiceContext context) {
        mServiceContext = context;
    }

    void initProcedureRunner(Procedure proc) {
        mRunner = new ProcedureRunner();
        mRunner.setProcedure(proc);
        mRunner.setContext(mContextMap);
        mRunner.setConfiguration(mConfig);
    }
/*
    public boolean invoke() throws Exception {
        mProcManager.initContext(mContextMap);
        mConfig.fireEvent("DetectProcedure", mEventArgs);
        while (mController.getContinueFlag()) {
            String name = mController.getProcedureName();
            if (name == null) {
                // name=DEFAULT_RUNSCREEN_PROCEDURE;
                throw new IllegalStateException(
                        "No procedure name set in service context");
            }
            Procedure proc = mProcManager.loadProcedure(name);
            if (proc == null)
                throw new IllegalStateException("Can't load procedure " + name);
            initProcedureRunner(proc);
            mController.setContinueFlag(false);
            mRunner.run();
            if (mRunner.getException() != null)
                throw new RuntimeException(mRunner.getException());
        }
        ;
        return mServiceContext.isSuccess();
    }
*/
    public boolean invoke(Procedure proc) throws Exception {
        initContext();
        initProcedureRunner(proc);
        mRunner.run();
        if (mRunner.getException() != null){
             mServiceContext.setSuccess(false);
             throw new RuntimeException(mRunner.getException());
        }
        return mServiceContext.isSuccess();
    }

    public CompositeMap getContextMap() {
        return mContextMap;
    }
    
    public IEventDispatcher getConfig(){
        return mConfig;
    }

    public void setContextMap(CompositeMap contextMap) {
        mContextMap = contextMap;
        mServiceContext = (ServiceContext) DynamicObject.cast(contextMap,
                ServiceContext.class);
        mController = ServiceController.createServiceController(mContextMap);
        mServiceContext.setInstanceOfType(IService.class, this);
    }

    public CompositeMap getServiceConfigData() {
        return mConfigMap;
    }

    public void setServiceConfigData(CompositeMap configMap) {
        setServiceConfigData( configMap, true);
    }
    
    /**
     * Set config data for this service instance
     * @param configMap CompositeMap containing service config
     * @param parse whether parseConfig() should be invoked immediately
     */
    public void setServiceConfigData( CompositeMap configMap, boolean parse ){
        mConfigMap = configMap;
        if(parse)
            parseConfig();
    }

    public void setName(String name) {
        mContextMap.put(KEY_SERVICE_NAME, name);
    }

    public String getName() {
        return mContextMap.getString(KEY_SERVICE_NAME);
    }

    public Configuration getServiceConfig() {
        return mConfig;
    }

    protected void clearMap(CompositeMap data) {
        if (data != null)
            data.clear();
    }

    public boolean isTraceOn() {
        if (mConfigMap == null)
            return true;
        return mConfigMap.getBoolean("trace", false);
    }
    
    public boolean isConfigParsed(){
        return mConfigParsed;
    }

    public void clear() {
    	SqlServiceContext sqlServiceContext=(SqlServiceContext)mServiceContext.castTo(SqlServiceContext.class);
    	try {
			sqlServiceContext.freeConnection();
		} catch (SQLException e) {			
			 throw new RuntimeException(
                     "freeConnection failed", e);
		}finally{
	    	mProcManager.destroyContext(mContextMap);
	        clearMap(mContextMap);
	        clearMap(mConfigMap);
	        if (mConfig != null)
	            mConfig.clear();
		}
    }

    public ServiceController getController() {
        return mController;
    }

    public ServiceOutputConfig getServiceOutputConfig() {
        CompositeMap child = mConfigMap == null ? null : mConfigMap
                .getChild(ServiceOutputConfig.KEY_SERVICE_OUTPUT);
        if (child == null)
            return null;
        else
            return ServiceOutputConfig.getInstance(child);
    }

    /** Get/Set root Configuration instance that contains global participants */
    public Configuration getRootConfig() {
        return mRootConfig;
    }

    public void setRootConfig(Configuration rootConfig) {
        mRootConfig = rootConfig;
        mConfig.setParent(mRootConfig);
    }
    
    public ILogger getServiceLogger(){
        return LoggingContext.getLogger(mContextMap, LOGGING_TOPIC);
    }
    
    public void release(){
        if(mResourceReleasers.size()>0)
            for( IResourceReleaser rl : mResourceReleasers){
                try{
                    rl.doRelease(mServiceContext);
                }catch(Throwable thr){
                    mRunner.getLogger().log(Level.WARNING, "Error when releasing resource", thr);
                }
            }
    }
    
    public void addResourceReleaser( IResourceReleaser rl ){
        mResourceReleasers.add(rl);
    }

}
