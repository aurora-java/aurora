/*
 * Created on 2010-12-10 上午10:53:00
 * $Id$
 */
package aurora.bm;

import uncertain.composite.CompositeMap;

/**
 * Get IBusinessModelAccessChecker instance according to session data
 */
public interface IBusinessModelAccessCheckerFactory {
    
    /**
     * Get IBusinessModelAccessChecker instance according to session data
     * @param model_name name of model
     * @param session_context A CompositeMap containing all session data
     * @return A IBusinessModelAccessChecker instance that can check if certain operation is enabled
     */
    public IBusinessModelAccessChecker getChecker( String model_name, CompositeMap session_context ) throws Exception;

}
