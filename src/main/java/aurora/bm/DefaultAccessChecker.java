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
    
    public static class ConstantChecker implements IBusinessModelAccessChecker {
        
        boolean result;
        
        public ConstantChecker(boolean b){
            result = b;
        }
        
        public boolean canPerformOperation(String operation) {
            return result;
        }        
    };
    
    public static final IBusinessModelAccessChecker ALWAYS_ALLOW = new ConstantChecker(true);
    public static final IBusinessModelAccessChecker ALWAYS_DENY = new ConstantChecker(false);
    
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
