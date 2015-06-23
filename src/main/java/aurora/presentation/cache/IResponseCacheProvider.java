/*
 * Created on 2011-4-30 下午11:50:05
 * $Id$
 */
package aurora.presentation.cache;

import java.io.File;

import uncertain.cache.ICache;

public interface IResponseCacheProvider {
    
    public ICache getCacheForResponse();
    
    /** Given a string key, return full key with prefix that can 
     *  represent all factors that may cause cached content to expire.
     *  For example, source file name, source file last modified date, etc */
    public String getFullCacheKey( String key );
    
    public String getFullCacheKey( File source_file, String key);

}
