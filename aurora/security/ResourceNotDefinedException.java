/*
 * Created on 2011-7-16 下午11:02:10
 * $Id$
 */
package aurora.security;

import uncertain.exception.GeneralException;

public class ResourceNotDefinedException extends GeneralException {
    
    public ResourceNotDefinedException( String resource_name ){
        super("aurora.validation.resource_not_defined", new Object[]{resource_name}, (Throwable)null);
    }

}
