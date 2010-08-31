/*
 * Created on 2009-9-1
 */
package aurora.service;

import java.sql.SQLException;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.event.Configuration;
import uncertain.event.RuntimeContext;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.proc.IProcedureManager;
import uncertain.proc.Procedure;
import uncertain.proc.ProcedureRunner;
import aurora.database.service.SqlServiceContext;
import aurora.events.E_PrepareServiceConfig;

public class ServiceInstance implements IService {

    public static final String LOGGING_TOPIC = "aurora.application";

    protected IProcedureManager mProcManager;

    boolean     mContextInited = false;
    protected CompositeMap mContextMap;
    protected ServiceContext mServiceContext;
    protected ServiceController mController;

    protected Configuration mRootConfig;
    protected Configuration mConfig;
    protected CompositeMap mConfigMap;
    protected ProcedureRunner mRunner;

    private Object[] mEventArgs = { this };

    public static ServiceInstance getInstance(CompositeMap context) {
        return (ServiceInstance) context.get(RuntimeContext
                .getTypeKey(IService.class));
    }

    public ServiceInstance(String name, IProcedureManager proc_manager) {
        // mConfigMap = config_map;
        mProcManager = proc_manager;
        CompositeMap context = new CompositeMap("context");
        setContextMap(context);
        setName(name);
    }

    /** Global participants can do service config population before it is parsed */
    void parseConfig() {
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
        mController.setContinueFlag(false);
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
    
    public Configuration getConfig(){
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
        mConfigMap = configMap;
        parseConfig();
    }

    public void setName(String name) {
        mContextMap.put("service_name", name);
    }

    public String getName() {
        return mContextMap.getString("service_name");
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
    }
    
    public ILogger getServiceLogger(){
        return LoggingContext.getLogger(mContextMap, LOGGING_TOPIC);
    }

}
