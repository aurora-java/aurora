/*
 * Created on 2012-9-14 下午01:50:49
 * $Id$
 */
package aurora.bm;

import uncertain.composite.CompositeMap;

/**
 * 
 */

public interface ICachedDataProvider {
    
    public String getCacheName( BusinessModel model);
    
    public String getCacheKey( BusinessModel model);
    
    public String getParsedCacheKey(BusinessModel model,CompositeMap record);
    

}
