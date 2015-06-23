/*
 * Created on 2010-11-3 下午05:00:43
 * $Id$
 */
package aurora.bm;

/**
 * Defines abstract behavior to check if certain operation is enabled 
 * for specified BusinessModel
 */
public interface IBusinessModelAccessChecker {
    
    public boolean canPerformOperation( String operation );

}
