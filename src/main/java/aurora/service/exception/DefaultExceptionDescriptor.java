/*
 * Created on 2007-12-28
 */
package aurora.service.exception;

import uncertain.composite.CompositeMap;
import uncertain.util.StackTraceUtil;
import aurora.service.ServiceContext;
import aurora.service.validation.ErrorMessage;

public class DefaultExceptionDescriptor implements IExceptionDescriptor {
    
    static DefaultExceptionDescriptor default_instance = new DefaultExceptionDescriptor();
    
    public static DefaultExceptionDescriptor getInstance(){
        return default_instance;
    }
    
    //public CompositeMap process( ServiceContext context, CompositeMap parameter, String field_name, Throwable exception ){
    public CompositeMap process( ServiceContext context, Throwable exception ){
        ErrorMessage msg = new ErrorMessage(exception.getClass().getName(), exception.getMessage(), null);
        CompositeMap map = msg.getObjectContext();
        //map.put("stackTrace", StackTraceUtil.toString(exception) );
        return map;
    }

}
