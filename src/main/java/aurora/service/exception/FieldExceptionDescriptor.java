/*
 * Created on 2008-7-2
 */
package aurora.service.exception;

import java.util.ResourceBundle;

import uncertain.composite.CompositeMap;
import aurora.application.util.LanguageUtil;
import aurora.i18n.ILocalizedMessageProvider;
import aurora.service.ServiceContext;
import aurora.service.validation.DatatypeMismatchException;
import aurora.service.validation.ErrorMessage;
import aurora.service.validation.FieldValidationException;

public class FieldExceptionDescriptor extends BaseExceptionDescriptor {
    
    public static String replaceQuick( String input, String search_str, String replace ){
        int id = input.indexOf(search_str);
        if(id>=0){
            StringBuffer buf = new StringBuffer(input.substring(0, id));
            buf.append(replace);
            buf.append(input.substring(id+search_str.length()));
            return buf.toString();
        }else
            return input;
    }
    
    void processDataType( ErrorMessage message, DatatypeMismatchException ex,  ILocalizedMessageProvider msgProvider ){
        Class cls = ex.getExpectedClass();
        String msg = mMessage;
        if(cls!=null){
            String name = cls.getName();
            if( mMessage != null ) 
                msg = replaceQuick( mMessage, "${datatype}", name );
        }
        if( msgProvider != null)
            msg = msgProvider.getMessage(msg);
        message.setMessage(msg);        
    }

    public CompositeMap process( ServiceContext context, Throwable exception) {
        ErrorMessage message = super.getErrorMessage(context, exception);      
        ILocalizedMessageProvider lp = (ILocalizedMessageProvider)context.getInstanceOfType(ILocalizedMessageProvider.class);
        if( exception instanceof FieldValidationException){
            FieldValidationException fvex = (FieldValidationException) exception;
            message.setField(fvex.getFieldName());            
            if( exception instanceof DatatypeMismatchException)
                //TODO pass ILocalizedMessageProvider
                processDataType( message, (DatatypeMismatchException)exception, lp );
        }
        return message.getObjectContext();
    }


}
