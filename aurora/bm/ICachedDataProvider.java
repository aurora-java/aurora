/*
 * Created on 2012-9-14 下午01:50:49
 * $Id$
 */
package aurora.bm;

import uncertain.cache.ICache;
import uncertain.cache.INamedCacheFactory;
import uncertain.composite.CompositeMap;

/**
 * 
 */

public interface ICachedDataProvider {
    
    public ICache getCachedData( String business_model_name );
    
    public INamedCacheFactory getCacheFactoryForData();
    
    //public String getCacheKey( BusinessModel model, CompositeMap record );

}
