/*
 * Created on 2011-8-19 下午04:31:51
 * $Id$
 */
package aurora.events;

import aurora.bm.BusinessModelReference;

/**
 * This event will be fired when a BusinessModel is loaded, and before send to use.
 */
public interface E_PopulateBusinessModel {
    
    public static final String EVENT_NAME = "PopulateBusinessModel";
    
    public int onPopulateBusinessModel( BusinessModelReference model_reference );  

}
