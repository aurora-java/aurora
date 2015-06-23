/*
 * Created on 2011-5-25 ����10:06:28
 * $Id$
 */
package aurora.service.lock;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.core.ILifeCycle;
import uncertain.event.EventModel;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.ISingleton;
import uncertain.proc.ProcedureRunner;
import aurora.application.util.LanguageUtil;
import aurora.service.ServiceContext;
import aurora.service.ServiceInstance;
import aurora.service.validation.ErrorMessage;

public class SessionLockChecker implements ISingleton, ILifeCycle {
    
    IServiceSessionLock      mSessionLock;
    IObjectRegistry          mObjectRegistry;
    
    boolean                 featureEnabled = true;
    boolean                 defaultCheckAll = false;
    String                  sessionKey = "${/session/@session_id}";
    String                  errorMessage = "PROMPT.SERVICE_IN_LOCK";
    
    
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean getDefaultCheckAll() {
        return defaultCheckAll;
    }

    public void setDefaultCheckAll(boolean defaultCheckAll) {
        this.defaultCheckAll = defaultCheckAll;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String key) {
        this.sessionKey = key;
    }
    
    public boolean getFeatureEnabled() {
        return featureEnabled;
    }

    public void setFeatureEnabled(boolean featureEnabled) {
        this.featureEnabled = featureEnabled;
    }

    /**
     * @param reg
     */
    public SessionLockChecker(IObjectRegistry reg) {
        super();
        this.mObjectRegistry = reg;
    }
    
    public IServiceSessionLock getServiceSessionLock(){
        return mSessionLock;
    }
    
    public boolean startup(){
        mSessionLock = (IServiceSessionLock)mObjectRegistry.getInstanceOfType(IServiceSessionLock.class);
        if(mSessionLock==null){
            mSessionLock = new ServiceSessionLock();
            mObjectRegistry.registerInstance(IServiceSessionLock.class, mSessionLock);
        }
        mObjectRegistry.registerInstance(SessionLockChecker.class, this);
        return true;
    }
    
    public void shutdown(){
        
    }
    
    public int onCheckServiceLock( ProcedureRunner runner )
        throws Exception
    {
        if(!featureEnabled)
            return EventModel.HANDLE_NORMAL;
        ServiceContext  context = ServiceContext.createServiceContext(runner.getContext());
        if(doSessionLockCheck(context)){
            //runner.stop();
            context.put("success", false);
            runner.locateTo("CreateResponse");
            return EventModel.HANDLE_NO_SAME_SEQUENCE;
        }else{
            return EventModel.HANDLE_NORMAL;
        }
    }
    
    /**
     * 
     * @param ct
     * @return true if service is locked, false is service is not locked
     * @throws Exception
     */
    public boolean doSessionLockCheck(ServiceContext ct)
        throws Exception
    {
        //MainService svc = MainService.getServiceInstance(ct.getObjectContext());
        CompositeMap context = ct.getObjectContext();
        ServiceInstance svc = ServiceInstance.getInstance(context);
        CompositeMap svc_config = svc.getServiceConfigData();
        //String name = svc.getName();
        
        
        Boolean check_lock = svc_config.getBoolean("checksessionlock");
        if(check_lock==null)
            check_lock = getDefaultCheckAll();

        
       if(!check_lock.booleanValue())
           return false;
        
       String   lock_key = svc_config.getString("lockkey");
       if(lock_key==null)
           lock_key = getSessionKey();
       lock_key = TextParser.parse(lock_key, context);
       
       String service_name = svc_config.getString("lockservice"); 
           if(service_name==null)
               service_name = svc.getName();
           else
               service_name = TextParser.parse(service_name, context);
       
       IServiceSessionLock ss_lock = getServiceSessionLock();
       boolean locked = ss_lock.islocked(lock_key, service_name);
       if(locked){
           String error_message = svc_config.getString("lockerrormessage");
           if(error_message==null)
               error_message = getErrorMessage();
           error_message = LanguageUtil.getTranslatedMessage(mObjectRegistry, error_message, context);
           ErrorMessage msg = new ErrorMessage(null, error_message, null);
           ct.setError(msg.getObjectContext());
           return true;
       }else{
           ss_lock.lock(lock_key, service_name, 0);
           ServiceSessionLock.Unlocker unlocker = new ServiceSessionLock.Unlocker(ss_lock, lock_key, service_name);
           svc.addResourceReleaser(unlocker);
           return false;
       }
    }        

}
