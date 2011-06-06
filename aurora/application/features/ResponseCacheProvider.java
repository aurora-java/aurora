/*
 * Created on 2011-4-30 下午11:50:30
 * $Id$
 */
package aurora.application.features;

import java.io.File;

import aurora.presentation.cache.IResponseCacheProvider;
import uncertain.cache.ICache;
import uncertain.cache.INamedCacheFactory;
import uncertain.core.IGlobalInstance;
import uncertain.event.EventModel;
import uncertain.ocm.IObjectRegistry;
import uncertain.util.resource.ISourceFile;
import uncertain.util.resource.ISourceFileManager;

public class ResponseCacheProvider implements IResponseCacheProvider, IGlobalInstance {
    
    public static final String DEFAULT_SCREEN_CACHE_NAME = "ResponseCache";
    //TODO add /session/theme
    public static final String DEFAULT_KEY_PREFIX = "${/session/@lang}";

    String              mResponseCacheKeyPrefix = DEFAULT_KEY_PREFIX;
    String              mResponseCacheName = DEFAULT_SCREEN_CACHE_NAME;
    ICache              mResponseCache;
    INamedCacheFactory  mCacheFactory;
    IObjectRegistry     mRegistry;
    ISourceFileManager  mSourceFileManager;
    
    
    /**
     * @param mCacheFactory
     */
    public ResponseCacheProvider(IObjectRegistry reg) {
        mRegistry = reg;
    }
    
    public void onInitialize(){
        mCacheFactory = (INamedCacheFactory)mRegistry.getInstanceOfType(INamedCacheFactory.class);
        mResponseCache = mCacheFactory.getNamedCache(mResponseCacheName);
        mSourceFileManager = (ISourceFileManager)mRegistry.getInstanceOfType(ISourceFileManager.class);
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
