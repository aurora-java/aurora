/*
 * Created on 2010-12-10 上午11:12:53
 * $Id$
 */
package aurora.bm;

import java.util.Set;

/**
 * Simple Set based IBusinessModelAccessChecker implementation
 */
public class DefaultAccessChecker implements IBusinessModelAccessChecker {
    
    /**
     * @param enabledOperations
     */
    public DefaultAccessChecker(Set enabledOperations) {
        super();
        mEnabledOperations = enabledOperations;
    }

    Set     mEnabledOperations;

    public boolean canPerformOperation(String operation) {
        return mEnabledOperations.contains(operation);
    }

}
