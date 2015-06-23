/*
 * Created on 2011-12-28 下午03:56:05
 * $Id$
 */
package aurora.security;

import uncertain.composite.CompositeMap;

public interface IAccessRule {
    
    public String getName();
    
    public boolean isValid( CompositeMap runtime_context ) throws Exception;   

}
