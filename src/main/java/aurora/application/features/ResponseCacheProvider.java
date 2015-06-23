/*
 * Created on 2011-4-30 下午11:50:30
 * $Id$
 */
package aurora.application.features;

import java.io.File;

import uncertain.cache.ICache;
import uncertain.cache.INamedCacheFactory;
import uncertain.core.IGlobalInstance;
import uncertain.core.ILifeCycle;
import uncertain.exception.GeneralException;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.util.resource.ISourceFile;
import uncertain.util.resource.ISourceFileManager;
import aurora.presentation.cache.IResponseCacheProvider;

public class ResponseCacheProvider extends AbstractLocatableObject implements IResponseCacheProvider, IGlobalInstance, ILifeCycle {
    
    public static final String DEFAULT_SCREEN_CACHE_NAME = "ResponseCache";
    //TODO add /session/theme
    public static final String DEFAULT_KEY_PREFIX = "${/session/@lang}";

    String              mResponseCacheKeyPrefix = DEFAULT_KEY_PREFIX;
    String              mResponseCacheName = DEFAULT_SCREEN_CACHE_NAME;
    ICache              mResponseCache;
    INamedCacheFactory  mCacheFactory;
    //IObjectRegistry     mRegistry;
    ISourceFileManager  mSourceFileManager;
    

    /*
    public ResponseCacheProvider(IObjectRegistry reg) {
        mRegistry = reg;
    }
    */
    
    public ResponseCacheProvider(INamedCacheFactory mCacheFactory,
            ISourceFileManager mSourceFileManager) {
        super();
        this.mCacheFactory = mCacheFactory;
        this.mSourceFileManager = mSourceFileManager;
    }

    public boolean startup(){
        mResponseCache = mCacheFactory.getNamedCache(mResponseCacheName);
        if(mResponseCache==null)
            throw new GeneralException("uncertain.cache.named_cache_not_found", new Object[]{mResponseCacheName}, this);
        return true;
    }
    
    public void shutdown(){
        
    }

    public String getFullCacheKey( String key){
        if(key==null)
            return mResponseCacheKeyPrefix;
        else
            return mResponseCacheKeyPrefix + key;
    }
    
    public String getFullCacheKey( File source_file, String key){
        StringBuffer full_key = new StringBuffer(source_file.getPath());
        if(mSourceFileManager!=null){
            ISourceFile latest_file = mSourceFileManager.getSourceFile(source_file);
            if(latest_file!=null){
                long last_modified = latest_file.getLastModified();
                full_key.append(last_modified);
            }
        }
        full_key.append(getFullCacheKey(key));
        return full_key.toString();
    }
    
    public ICache getCacheForResponse(){
        return mResponseCache;
    }

    
    public String getResponseCacheKeyPrefix() {
        return mResponseCacheKeyPrefix;
    }

    public void setResponseCacheKeyPrefix(String screenCacheKeyPrefix) {
        mResponseCacheKeyPrefix = screenCacheKeyPrefix;
    }

    public String getResponseCacheName() {
        return mResponseCacheName;
    }

    public void setResponseCacheName(String screenCacheName) {
        mResponseCacheName = screenCacheName;
    }

    
}
