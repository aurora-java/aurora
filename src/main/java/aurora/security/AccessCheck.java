/*
 * Created on 2011-12-28 下午04:07:36
 * $Id$
 */
package aurora.security;

import java.util.Iterator;
import java.util.List;

import aurora.application.AuroraApplication;
import aurora.service.ServiceInstance;
import aurora.service.http.HttpServiceInstance;
import uncertain.composite.CompositeMap;
import uncertain.proc.ProcedureRunner;

public class AccessCheck {

	IAccessCheckRuleProvider mRuleProvider;
	String ACCSESS_TAG="access-check";
	String RULE_NAME_KEY="name";
	
	public AccessCheck(IAccessCheckRuleProvider mRuleProvider) {
		this.mRuleProvider = mRuleProvider;
	}

	public void onAccessCheck(ProcedureRunner runner) throws Exception {
		CompositeMap element;
		String ruleName;
		CompositeMap context_map=runner.getContext();
		HttpServiceInstance svc = (HttpServiceInstance) ServiceInstance.getInstance(context_map);
		List<CompositeMap> childs=svc.getServiceConfigData().getChilds();
		Iterator<CompositeMap> iterator=childs.iterator();
		while(iterator.hasNext()){
			element=iterator.next();
			if(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE.equalsIgnoreCase(element.getNamespaceURI())
				&&ACCSESS_TAG.equalsIgnoreCase(element.getName())){
				ruleName=element.getString(RULE_NAME_KEY);
				if(ruleName!=null)
					doAccessCheck(context_map, ruleName);
			}
		}
		
	}

	private void doAccessCheck(CompositeMap context_map, String ruleName)
			throws Exception {
		
		IAccessRule accsessRule = this.mRuleProvider.getAccessRule(ruleName);
		
		if (accsessRule == null)
			throw new IllegalArgumentException("The access-check-rule {" + ruleName
					+ "} is undefined");
		boolean status = accsessRule.isValid(context_map);
		if (!status) {
			throw uncertain.exception.MessageFactory.createException(
					"aurora.security.access_check_rule_error",
					(Throwable) null, new Object[] { ruleName });		
		}
	}
}
