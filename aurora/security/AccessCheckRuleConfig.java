package aurora.security;

import uncertain.composite.CompositeMap;

public class AccessCheckRuleConfig implements IAccessCheckRuleProvider{
	CompositeMap accessCheckRuleMap;

	public void addAccessCheckRules(AccessCheckRule[] AccessCheckRules){
		int leng=AccessCheckRules.length;
		accessCheckRuleMap=new CompositeMap(leng);
		for(int i=0;i<leng;i++){
			IAccessRule rule=AccessCheckRules[i];
			accessCheckRuleMap.put(rule.getName(), rule);
		}
	}
	
	public IAccessRule getAccessRule(String name) {
		if(accessCheckRuleMap==null)
			throw new IllegalArgumentException("access-check-rule.config:access-check-rules is null");
		return (IAccessRule)accessCheckRuleMap.get(name);
	}
}
