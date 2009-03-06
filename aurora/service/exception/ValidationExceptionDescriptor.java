/*
 * Created on 2008-7-2
 */
package aurora.service.exception;

import java.util.Iterator;
import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import aurora.service.ServiceContext;
import aurora.service.validation.ErrorMessage;
import aurora.service.validation.ValidationException;

public class ValidationExceptionDescriptor extends BaseExceptionDescriptor {
    
    IExceptionDescriptor   mDesconfig;
    
    public ValidationExceptionDescriptor( IExceptionDescriptor config ){
        mDesconfig = config;
    }
    
    public CompositeMap process( ServiceContext context, Throwable exception ) {
        ValidationException vexp = (ValidationException)exception;
        CompositeMap error = super.process(context, exception);
        ErrorMessage msg = (ErrorMessage)DynamicObject.cast(error, ErrorMessage.class);
        CompositeMap input = vexp.getInput();
        msg.setInput(input);
        List lst = vexp.getExceptionList();
        if(lst!=null){
            Iterator it = lst.iterator();
            while(it.hasNext()){
                Exception exp = (Exception)it.next();
                CompositeMap item = mDesconfig.process(context, exp);
                msg.addErrorMessage(item);
            }
        }
        return error;
    }

}
