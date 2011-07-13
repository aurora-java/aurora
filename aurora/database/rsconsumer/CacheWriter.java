/*
 * Created on 2011-7-13 下午01:49:43
 * $Id$
 */
package aurora.database.rsconsumer;

import uncertain.cache.CacheBuiltinExceptionFactory;
import uncertain.cache.ICache;
import uncertain.cache.INamedCacheFactory;
import uncertain.composite.TextParser;
import uncertain.exception.BuiltinExceptionFactory;

public class CacheWriter extends CompositeMapCreator {
    
    INamedCacheFactory  mCacheFactory;
    String              mCacheName;
    String              mRecordKey;
    ICache              mCache;

    /**
     * @param mCacheFactory
     */
    public CacheWriter(INamedCacheFactory mCacheFactory) {
        this.mCacheFactory = mCacheFactory;
    }
    
    public String getCacheName() {
        return mCacheName;
    }
    
    public void setCacheName(String cacheName) {
        this.mCacheName = cacheName;
    }
    
    public ICache getCache(){
        if(mCacheName==null)
            return mCacheFactory.getCache();
        else
            return mCacheFactory.getNamedCache(mCacheName);
    }

    public void endRow() {
        String key = TextParser.parse(mRecordKey, currentRecord);
        mCache.setValue(key, currentRecord);
    }

    public void begin(String root_name) {
        if(mRecordKey==null)
            throw BuiltinExceptionFactory.createAttributeMissing(this, "recordKey");
        mCache = getCache();
        if(mCache==null)
            throw CacheBuiltinExceptionFactory.createNamedCacheNotFound(this, mCacheName);
        // do nothing
    }

    public String getRecordKey() {
        return mRecordKey;
    }

    public void setRecordKey(String recordKey) {
        this.mRecordKey = recordKey;
    }


}
