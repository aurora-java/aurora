/*
 * Created on 2007-11-8
 */
package aurora.service.validation;

import java.util.Locale;

import java.util.ResourceBundle;

import uncertain.composite.CompositeMap;

import uncertain.composite.DynamicObject;

public class ErrorMessage extends DynamicObject {
    
    public static final String ERROR_MESSAGE = "error-message";

    public static final String KEY_MESSAGE = "message";
    
    public static final String KEY_CODE = "code";
    
    public static final String KEY_FIELD = "field";
    
    public static final String KEY_INPUT = "input";
    
    public static final String KEY_ERRORS = "errors";
    
    public ErrorMessage(){
        super();
    }
    
    public static ErrorMessage createErrorMessage( CompositeMap context ){
        ErrorMessage msg = new ErrorMessage();
        msg.initialize(context);
        return msg;
    }
    
    public ErrorMessage(String code, String message, String field){
        CompositeMap m = new CompositeMap(10);
        m.setName(ERROR_MESSAGE);
        initialize(m);
        setField(field);
        setCode(code);
        setMessage(message);
    }
    
    public void localize( Locale locale, ResourceBundle bundle ){
        //String localized_msg = bundle.getString(key)
    }
    
    /**
     * @return the code
     */
    public String getCode() {
        return getString(KEY_CODE);
    }
    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        put(KEY_CODE, code);
    }
    /**
     * @return the message
     */
    public String getMessage() {
        return getString(KEY_MESSAGE);
    }
    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        put(KEY_MESSAGE, message);
    }

    /**
     * @return the field
     */
    public String getField() {
        return getString(KEY_FIELD);
    }

    /**
     * @param field the field to set
     */
    public void setField(String field) {
        put(KEY_FIELD, field);
    }
    
    public CompositeMap getInput(){
        return (CompositeMap)get(KEY_INPUT);
    }
    
    public void setInput( CompositeMap input ){
        input.setParent(getObjectContext());
        put(KEY_INPUT, input);
    }
    
    public CompositeMap getErrors(){
        CompositeMap child = getObjectContext().getChild(KEY_ERRORS);
        if( child==null ) child = getObjectContext().createChild(KEY_ERRORS);
        return child;
    }
    
    public void addErrorMessage( CompositeMap error ){
        getErrors().addChild(error);
    }
    
    public void addErrorMessage( ErrorMessage msg ){
        getErrors().addChild(msg.getObjectContext());
    }

}
