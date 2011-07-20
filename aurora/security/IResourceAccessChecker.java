/*
 * Created on 2011-7-16 下午10:41:26
 * $Id$
 */
package aurora.security;

import uncertain.composite.CompositeMap;

public interface IResourceAccessChecker {
    
    public static final String RESULT_SUCCESS = "success";
    public static final String RESULT_LOGIN_REQUIRED = "login_required";
    public static final String RESULT_UNREGISTERED = "unregistered";
    public static final String RESULT_UNAUTHORIZED = "unauthorized";

    /**
     * Get result code that indicates whether given session can have access to specified resource
     * @param resource name of resource
     * @param session_context CompositeMap containing session data
     * @return a String code for check result.
     * login_required: User need login before accesses this resource
     * unauthorized: User can't access this resource
     * unregistered: The resource is not registered and can't perform access check
     */
    public String checkAccess( String resource, CompositeMap session_context );

}
