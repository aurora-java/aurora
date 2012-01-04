/*
 * Created on 2011-12-28 下午04:07:36
 * $Id$
 */
package aurora.security;

import uncertain.proc.ProcedureRunner;

public class AccessCheck {
    
    IAccessCheckRuleProvider        mRuleProvider;
    String	name;   
    
    public AccessCheck(IAccessCheckRuleProvider mRuleProvider) {
        this.mRuleProvider = mRuleProvider;
    }
    
    public void onAccessCheck(ProcedureRunner runner) throws Exception
    {
    	IAccessRule accsessRule =this.mRuleProvider.getAccessRule(name);
    	boolean status=accsessRule.isValid(runner.getContext());
    	if(!status){
    		throw uncertain.exception.MessageFactory.createException("aurora.security.access_check_rule_error",(Throwable)null,new Object[]{name});
    		//throw new GeneralException("aurora.security.access_check_rule_error",new Object[]{name},(Throwable)null);
    	}
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
}
