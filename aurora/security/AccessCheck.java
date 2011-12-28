/*
 * Created on 2011-12-28 下午04:07:36
 * $Id$
 */
package aurora.security;

import uncertain.composite.CompositeMap;

public class AccessCheck {
    
    IAccessCheckRuleProvider        mRuleProvider;

    public AccessCheck(IAccessCheckRuleProvider mRuleProvider) {
        this.mRuleProvider = mRuleProvider;
    }
    
    public void onAccessCheck( CompositeMap runtime_context )
    {
        
    }

}
