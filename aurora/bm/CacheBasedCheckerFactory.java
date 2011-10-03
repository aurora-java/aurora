/*
 * Created on 2011-7-15 下午03:58:28
 * $Id$
 */
package aurora.bm;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import uncertain.cache.CacheBuiltinExceptionFactory;
import uncertain.cache.ICache;
import uncertain.cache.INamedCacheFactory;
import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.core.IGlobalInstance;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectRegistry;

public class CacheBasedCheckerFactory extends AbstractLocatableObject implements
        IBusinessModelAccessCheckerFactory, IGlobalInstance {
    
    //IObjectRegistry         mRegistry;
    INamedCacheFactory      mCacheFactory;
    IModelFactory           mModelFactory;    
    ICache                  mBmDataCache;
    
    String                  mCacheName;
    String                  mCacheKeyPrefix;
    
    public static Set createOperationSet( Map map, String value_for_enable){
        Set s = new HashSet();
        Iterator it = map.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry e = (Map.Entry)it.next();
            if(value_for_enable.equals(e.getValue()))
                s.add(e.getKey());
        }
        return s;
    }
    
    static final IBusinessModelAccessChecker NO_ACCESS = new IBusinessModelAccessChecker(){
        
        public boolean canPerformOperation(String op){
            return false;
        }
        
    };
    
    /*
    public CacheBasedCheckerFactory(IObjectRegistry reg) {
        mRegistry = reg;
    }
    */
    
    public CacheBasedCheckerFactory(IModelFactory mf, INamedCacheFactory fact) {
        this.mCacheFactory = fact;
        this.mModelFactory = mf;
    }
    
    
    public void onInitialize(){
/*        
        mCacheFactory = (INamedCacheFactory)mRegistry.getInstanceOfType(INamedCacheFactory.class);
        if(mCacheFactory==null)
            throw BuiltinExceptionFactory.createInstanceNotFoundException(this, INamedCacheFactory.class, this.getClass().getName());
            
        mModelFactory = (IModelFactory)mRegistry.getInstanceOfType(IModelFactory.class);
        if(mModelFactory==null)
            throw BuiltinExceptionFactory.createInstanceNotFoundException(this, IModelFactory.class, this.getClass().getName());
*/        
        if(mCacheName==null)
            throw BuiltinExceptionFactory.createAttributeMissing(this, "cacheName");
        
        mBmDataCache = mCacheFactory.getNamedCache(mCacheName);
        if(mBmDataCache==null)
            throw CacheBuiltinExceptionFactory.createNamedCacheNotFound(this, mCacheName);
        
    }

    public IBusinessModelAccessChecker getChecker(String model_name,
            CompositeMap session_context) throws Exception {
        BusinessModel bm = mModelFactory.getModelForRead(model_name);
        assert bm!=null;
        BusinessModel bm_for_check = bm.getModelForAccessCheck();
        if(!bm_for_check.getNeedAccessControl())
            return DefaultAccessChecker.ALWAYS_ALLOW;
        String key_prefix = mCacheKeyPrefix==null?null:TextParser.parse(mCacheKeyPrefix, session_context);
        String key = model_name;
        if(key_prefix!=null)
            key = key_prefix + model_name;
        Map data = (Map)mBmDataCache.getValue(key);
        if(data==null)
            return null;
        else
            return new DefaultAccessChecker(createOperationSet(data,"Y"));
    }

    public String getCacheName() {
        return mCacheName;
    }

    public void setCacheName(String cacheName) {
        this.mCacheName = cacheName;
    }

    public String getCacheKeyPrefix() {
        return mCacheKeyPrefix;
    }

    public void setCacheKeyPrefix(String cacheKey) {
        mCacheKeyPrefix = cacheKey;
    }

}
