/*
 * Created on 2008-7-1
 */
package aurora.service.exception;

import java.util.regex.Pattern;

import uncertain.composite.DynamicObject;

public class ExceptionDescriptor extends DynamicObject {
    
    public static final String KEY_EXCEPTION = "exception";
    
    public static final String KEY_EXCEPTION_PATTERN = "exceptionpattern";
    
    public static final String KEY_HANDLE_CLASS = "handleclass";
    
    Pattern     pattern;
    
    /**
     * @return the exception
     */
    public String getException() {
        return getString(KEY_EXCEPTION);
    }
    /**
     * @param exception the exception to set
     */
    public void setException(String exception) {
        putString(KEY_EXCEPTION, exception);
    }
    /**
     * @return the exceptionPattern
     */
    public String getExceptionPattern() {
        return getString(KEY_EXCEPTION_PATTERN);
    }
    /**
     * @param exceptionPattern the exceptionPattern to set
     */
    public void setExceptionPattern(String exceptionPattern) {
        putString(KEY_EXCEPTION_PATTERN, exceptionPattern);
    }
    /**
     * @return the handleClass
     */
    public String getHandleClass() {
        return getString(KEY_HANDLE_CLASS);
    }
    /**
     * @param handleClass the handleClass to set
     */
    public void setHandleClass(String handleClass) {
        putString(KEY_HANDLE_CLASS, handleClass);
    }
    /**
     * @return the pattern
     */
    public Pattern getPattern() {
        return pattern;
    }
    /**
     * @param pattern the pattern to set
     */
    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }


}
