package aurora.service.exception;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import aurora.service.ServiceContext;
import aurora.service.validation.ErrorMessage;
import aurora.service.validation.ImageValidationException;



public class ImageValidationExceptionDescriptor extends BaseExceptionDescriptor {
	
	public CompositeMap process( ServiceContext context, Throwable exception ) {
        ImageValidationException vexp = (ImageValidationException)exception;
        CompositeMap error = super.process(context, exception);
        ErrorMessage msg = (ErrorMessage)DynamicObject.cast(error, ErrorMessage.class);
        msg.setMessage(vexp.getMessage());
        return error;
    }
}
