/*
 * Created on 2011-12-28 下午03:55:05
 * $Id$
 */
package aurora.security;

public interface IAccessCheckRuleProvider {
    
    public IAccessRule  getAccessRule( String name );

}
