/*
 * Created on 2011-7-16 下午10:47:06
 * $Id$
 */
package aurora.security;

import aurora.application.ISessionInfoProvider;
import uncertain.cache.CacheBuiltinExceptionFactory;
import uncertain.cache.ICache;
import uncertain.cache.INamedCacheFactory;
import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.core.IGlobalInstance;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectRegistry;

public class CacheBasedResourceAccessChecker extends AbstractLocatableObject implements IResourceAccessChecker, IGlobalInstance {
    
    IObjectRegistry         mRegistry;
    INamedCacheFactory      mCacheFactory;
    ISessionInfoProvider    mSessionInfoProvider;
    ICache                  mResourceCache;
    ICache                  mAccessCache;
    
    String resourceCacheName;
    String accessCacheName;
    String accessCacheKeyPrefix;
    String loginFlag;
    String accessCheckFlag;
    
    public CacheBasedResourceAccessChecker(IObjectRegistry mRegistry) {
        this.mRegistry = mRegistry;
    }
    
    public void onInitialize(){
        mCacheFactory = (INamedCacheFactory)mRegistry.getInstanceOfType(INamedCacheFactory.class);
        if(mCacheFactory==null)
            throw BuiltinExceptionFactory.createInstanceNotFoundException(this, INamedCacheFactory.class, this.getClass().getName());
        
        mSessionInfoProvider = (ISessionInfoProvider)mRegistry.getInstanceOfType(ISessionInfoProvider.class);
        if(mSessionInfoProvider==null)
            throw BuiltinExceptionFactory.createInstanceNotFoundException(this, ISessionInfoProvider.class, this.getClass().getName());
            
        if(accessCacheName==null)
            throw BuiltinExceptionFactory.createAttributeMissing(this, "accessCacheName");

        if(resourceCacheName==null)
            throw BuiltinExceptionFactory.createAttributeMissing(this, "resourceCacheName");
        
        mResourceCache = mCacheFactory.getNamedCache(resourceCacheName);
        if(mResourceCache==null)
            throw CacheBuiltinExceptionFactory.createNamedCacheNotFound(this, resourceCacheName);
        
        mAccessCache = mCacheFactory.getNamedCache(accessCacheName);
        if(mAccessCache==null)
            throw CacheBuiltinExceptionFactory.createNamedCacheNotFound(this, accessCacheName);

    }
    

    public String checkAccess( String resource, CompositeMap session_context ){
    	if(mResourceCache == null){
    		throw new RuntimeException("resourceCache initialise failed, please check the log.");
    	}
        CompositeMap resource_map = (CompositeMap)mResourceCache.getValue(resource);
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
        String key = accessCacheKeyPrefix==null?resource:TextParser.parse(accessCacheKeyPrefix,session_context)+resource;
        Object v = mAccessCache.getValue(key);
        if(v==null)
            return IResourceAccessChecker.RESULT_UNAUTHORIZED;
        }
        
        return IResourceAccessChecker.RESULT_SUCCESS;
    }

    public String getResourceCacheName() {
        return resourceCacheName;
    }

    public void setResourceCacheName(String resourceCacheName) {
        this.resourceCacheName = resourceCacheName;
    }

    public String getAccessCacheName() {
        return accessCacheName;
    }

    public void setAccessCacheName(String accessCacheName) {
        this.accessCacheName = accessCacheName;
    }

    public String getAccessCacheKeyPrefix() {
        return accessCacheKeyPrefix;
    }

    public void setAccessCacheKeyPrefix(String accessCacheKeyPrefix) {
        this.accessCacheKeyPrefix = accessCacheKeyPrefix;
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
    
    public ICache getResourceCache(){
    	return mResourceCache;
    }
    

}
