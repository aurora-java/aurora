package aurora.security;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.core.IGlobalInstance;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectRegistry;
import aurora.application.ISessionInfoProvider;
import aurora.database.FetchDescriptor;
import aurora.database.service.BusinessModelService;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.database.service.SqlServiceContext;

public class DefaultResourceAccessChecker extends AbstractLocatableObject implements IResourceAccessChecker, IGlobalInstance {
    
    IObjectRegistry         mRegistry;
    ISessionInfoProvider    mSessionInfoProvider;
    IDatabaseServiceFactory mServiceFactory;
    
    String resourceBM;
    String accessBM;
    String accessKeyPrefix;
    String loginFlag;
    String accessCheckFlag;
    
    public DefaultResourceAccessChecker(IObjectRegistry mRegistry) {
        this.mRegistry = mRegistry;
    }
    
    public void onInitialize(){
    	mServiceFactory = (DatabaseServiceFactory)mRegistry.getInstanceOfType(IDatabaseServiceFactory.class);
    	if(mServiceFactory==null)
            throw BuiltinExceptionFactory.createInstanceNotFoundException(this, IDatabaseServiceFactory.class, this.getClass().getName());
        mSessionInfoProvider = (ISessionInfoProvider)mRegistry.getInstanceOfType(ISessionInfoProvider.class);
        if(mSessionInfoProvider==null)
            throw BuiltinExceptionFactory.createInstanceNotFoundException(this, ISessionInfoProvider.class, this.getClass().getName());
            
        if(accessBM==null)
            throw BuiltinExceptionFactory.createAttributeMissing(this, "accessBM");

        if(resourceBM==null)
            throw BuiltinExceptionFactory.createAttributeMissing(this, "resourceBM");
    }
    

    public String checkAccess( String resource, CompositeMap session_context ){
    	session_context.put("service_name", resource);
        CompositeMap resource_map;
		try {
			CompositeMap records = queryBM(resourceBM,session_context);
			if(records==null ||records.getChilds() == null)
	            //throw new ResourceNotDefinedException(resource);
	            return IResourceAccessChecker.RESULT_UNREGISTERED;
			resource_map = (CompositeMap)records.getChilds().get(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
        if(resource_map==null)
            //throw new ResourceNotDefinedException(resource);
            return IResourceAccessChecker.RESULT_UNREGISTERED;
        
        Boolean need_login = resource_map.getBoolean(loginFlag);
        if(need_login==null)
            throw new RuntimeException(loginFlag+" not defined in "+resource_map.toXML());

        Boolean need_access_check = resource_map.getBoolean(accessCheckFlag);
        if(need_access_check==null)
            throw new RuntimeException(accessCheckFlag+" not defined in "+resource_map.toXML());
        
        boolean is_logged_in = mSessionInfoProvider.isLoggedin(session_context);
        if(!is_logged_in && need_login.booleanValue()){
            return IResourceAccessChecker.RESULT_LOGIN_REQUIRED;
        }
        
        if(need_access_check.booleanValue()){
        String key = accessKeyPrefix==null?resource:TextParser.parse(accessKeyPrefix,session_context)+resource;
        CompositeMap access_map;
		try {
			CompositeMap records = queryBM(accessBM,session_context);
			if(records==null ||records.getChilds() == null)
	            return IResourceAccessChecker.RESULT_UNAUTHORIZED;
			access_map = (CompositeMap)records.getChilds().get(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
        if(access_map == null || !key.equals(access_map.get("role_service")))
            return IResourceAccessChecker.RESULT_UNAUTHORIZED;
        }
        
        return IResourceAccessChecker.RESULT_SUCCESS;
    }

    public String getResourceBM() {
        return resourceBM;
    }

    public void setResourceBM(String resourceBM) {
        this.resourceBM = resourceBM;
    }

    public String getAccessBM() {
        return accessBM;
    }

    public void setAccessBM(String accessBM) {
        this.accessBM = accessBM;
    }

    public String getAccessKeyPrefix() {
        return accessKeyPrefix;
    }

    public void setAccessKeyPrefix(String accessKeyPrefix) {
        this.accessKeyPrefix = accessKeyPrefix;
    }

    public String getLoginFlag() {
        return loginFlag;
    }

    public void setLoginFlag(String loginFlag) {
        this.loginFlag = loginFlag;
    }

    public String getAccessCheckFlag() {
        return accessCheckFlag;
    }

    public void setAccessCheckFlag(String accessCheckFlag) {
        this.accessCheckFlag = accessCheckFlag;
    }
    private CompositeMap queryBM(String bm,CompositeMap session_context) throws Exception{
    	SqlServiceContext context = mServiceFactory.createContextWithConnection();
    	try{
	//		CompositeMap context = ServiceThreadLocal.getCurrentThreadContext();
			BusinessModelService service = mServiceFactory.getModelService(bm,context.getObjectContext());
			CompositeMap resultMap = service.queryAsMap(session_context,FetchDescriptor.fetchAll());
			return resultMap;
    	}
	     finally {
	        if (context != null)
	            context.freeConnection();
	    }
    }

}
