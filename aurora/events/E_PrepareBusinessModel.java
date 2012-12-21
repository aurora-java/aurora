/*
 * Created on 2010-5-12 下午01:32:08
 * $Id$
 */
package aurora.events;

import uncertain.composite.CompositeMap;
import aurora.bm.BusinessModel;

/**
 * Fires when model factory prepares a business model service, before any model service got invoked 
 */
public interface E_PrepareBusinessModel {
    
    public static final String EVENT_NAME = "PrepareBusinessModel";
    
    public void onPrepareBusinessModel( BusinessModel model, CompositeMap context ) throws Exception;

}
