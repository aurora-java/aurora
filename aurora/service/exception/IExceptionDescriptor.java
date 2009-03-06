/*
 * Created on 2007-12-26
 */
package aurora.service.exception;

import uncertain.composite.CompositeMap;
import aurora.service.ServiceContext;

public interface IExceptionDescriptor {
    
/*    
    public CompositeMap process( ServiceContext context, CompositeMap parameter, String field_name, Throwable exception );
*/
    
    public CompositeMap process( ServiceContext context, Throwable exception );

}