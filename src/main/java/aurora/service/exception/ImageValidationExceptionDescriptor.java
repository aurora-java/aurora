package aurora.service.exception;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import aurora.service.ServiceContext;
import aurora.service.validation.ErrorMessage;
import aurora.service.validation.ImageValidationException;

/**
 * 
 * @version $Id: ImageValidationExceptionDescriptor.java v 1.0 2010-8-13 下午01:31:24 IBM Exp $
 * @author <a href="mailto:njq.niu@hand-china.com">vincent</a>
 */
public class ImageValidationExceptionDescriptor extends BaseExceptionDescriptor {
	
	public CompositeMap process( ServiceContext context, Throwable exception ) {
        ImageValidationException vexp = (ImageValidationException)exception;
        CompositeMap error = super.process(context, exception);
        ErrorMessage msg = (ErrorMessage)DynamicObject.cast(error, ErrorMessage.class);
        msg.setCode("img_code_error");
        msg.setMessage(vexp.getMessage());
        return error;
    }
}
