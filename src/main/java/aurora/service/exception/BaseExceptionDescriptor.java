/*
 * Created on 2008-7-1
 */
package aurora.service.exception;

import java.util.ResourceBundle;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.ocm.IConfigurable;
import aurora.application.util.LanguageUtil;
import aurora.i18n.ILocalizedMessageProvider;
import aurora.service.ServiceContext;
import aurora.service.validation.ErrorMessage;

public class BaseExceptionDescriptor implements IExceptionDescriptor, IConfigurable {
    
    public static String getTranslatedMessage( String msg_text, ServiceContext context ){
        ILocalizedMessageProvider lp = (ILocalizedMessageProvider)context.getInstanceOfType(ILocalizedMessageProvider.class);
        return LanguageUtil.getTranslatedMessage(lp, msg_text, context.getCurrentParameter());
    }
    
    public String mMessage;
    
    public String mCode;
    
    CompositeMap    mConfig;
    
    public BaseExceptionDescriptor(){
        
    }
    
    protected ErrorMessage getErrorMessage(ServiceContext context, Throwable exception){
        String c = mCode==null?exception.getClass().getName():mCode;
        String msg_text = mMessage;
        if(msg_text==null)
            msg_text = exception.getMessage();
        if(msg_text!=null){
            msg_text = getTranslatedMessage(msg_text, context);
        }
        ErrorMessage msg = new ErrorMessage( c, msg_text, null);
        msg.getObjectContext().putAll(mConfig);
        return msg;
    }

    public CompositeMap process(ServiceContext context, Throwable exception) {
        ErrorMessage msg = getErrorMessage(context,exception);        
        return msg.getObjectContext();
    }
    
    public void beginConfigure(CompositeMap config){
        mConfig = config;
    }

    public void endConfigure(){
        mConfig.remove("code");
        mConfig.remove("message");
        mConfig.remove(ExceptionDescriptor.KEY_EXCEPTION);
        mConfig.remove(ExceptionDescriptor.KEY_EXCEPTION_PATTERN);
        mConfig.remove(ExceptionDescriptor.KEY_HANDLE_CLASS);
    }

    /**
     * @return the code
     */
    public String getCode() {
        return mCode;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        mCode = code;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return mMessage;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        mMessage = message;
    }

}
