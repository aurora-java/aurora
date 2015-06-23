/*
 * Created on 2008-7-2
 */
package aurora.database;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.core.ConfigurationError;
import aurora.application.util.LanguageUtil;
import aurora.service.ServiceContext;
import aurora.service.exception.BaseExceptionDescriptor;
import aurora.service.exception.IExceptionDescriptor;
import aurora.service.validation.ErrorMessage;

public class SQLExceptionDescriptor implements IExceptionDescriptor {
    
    // code -> CompositeMap of message config
    Map       mMessageMap;    
    String    mDefaultMessage;

    public CompositeMap process(ServiceContext context, Throwable exception) {        
        return getParsedError( context, getRootCause(exception) );
    }
    
    private Throwable getRootCause(Throwable thr) {
  		while (thr.getCause() != null)
  			thr = thr.getCause();
  		return thr;
  	}
    
    public ErrorMessage getErrorMessage(  SQLException sex ){
        int code = sex.getErrorCode();
        if(mMessageMap!=null){
            ErrorMessage msg =  (ErrorMessage)mMessageMap.get(Integer.toString(code));
            return msg;
        }else
            return null;
    }
    
    public CompositeMap getParsedError( ServiceContext context, Throwable exception ) {
        
        if(! (exception instanceof SQLException)){
            return null;
        }
        //ResourceBundle bundle = AppUtil.getResourceBundle(context);
        SQLException sex = (SQLException)exception;
        if(sex.getCause()!=null && sex.getCause() instanceof SQLException){
            sex = (SQLException)sex.getCause();
        }
        String error_msg = null;
        ErrorMessage msgObj = getErrorMessage(sex);
        if(msgObj!=null){
            error_msg = msgObj.getMessage();
        }
        if( error_msg==null ){
            if( mDefaultMessage != null )
                error_msg = mDefaultMessage;
        }
        if(error_msg==null)
            return null;
        error_msg = BaseExceptionDescriptor.getTranslatedMessage(error_msg, context);
        
        CompositeMap error = new CompositeMap(ErrorMessage.ERROR_MESSAGE);
        if(msgObj!=null)
            error.copy(msgObj.getObjectContext());
        else
            error.put(ErrorMessage.KEY_CODE, Integer.toString(sex.getErrorCode()));
        error.put(ErrorMessage.KEY_MESSAGE, error_msg);
        return error;
    }
    
    public void addErrorMessage( CompositeMap msg){
        ErrorMessage m = (ErrorMessage)DynamicObject.cast(msg, ErrorMessage.class);
        String code = m.getCode();
        if( code == null ) 
            throw new ConfigurationError("Must set 'code' property in "+m.getObjectContext().toXML());
        if(mMessageMap==null)
            mMessageMap = new HashMap();
        mMessageMap.put(code, m);
    }

    /**
     * @return the mDefaultMessage
     */
    public String getDefaultMessage() {
        return mDefaultMessage;
    }

    /**
     * @param defaultMessage the mDefaultMessage to set
     */
    public void setDefaultMessage(String defaultMessage) {
        mDefaultMessage = defaultMessage;
    }

}
