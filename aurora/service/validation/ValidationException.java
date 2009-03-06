/**
 * Created on: 2002-11-13 14:59:09
 * Author:     zhoufan
 */
package aurora.service.validation;

import java.util.List;

import uncertain.composite.CompositeMap;

/**
 * Exception to indicate that input parameter is invalid
 */
public class ValidationException extends Exception {
	
    List            mExceptionList;
    CompositeMap    mInput;

    /**
     * Default constructor
     */
    public ValidationException() {
        super();
    }

    /**
     * @param message 
     * @param exception_list A List of Exception instance
     */
    public ValidationException(String message, List exception_list) {
        super(message);
        setExceptionList(exception_list);
        
    }

    /**
     * @param message
     */
    public ValidationException(String message) {
        super(message);
    }


    public ValidationException(CompositeMap input, List  exception_list) {
        super();
        setExceptionList(exception_list);
        setInput(input);
    }

    /**
     * @return the mExceptionList
     */
    public List getExceptionList() {
        return mExceptionList;
    }

    /**
     * @param exceptionList the mExceptionList to set
     */
    public void setExceptionList(List exceptionList) {
        mExceptionList = exceptionList;
    }
    
    public String toString(){
        StringBuffer buf = new StringBuffer(this.getClass().getName());
        if(mExceptionList!=null)
            buf.append(":").append(mExceptionList);
        return buf.toString();
    }

    /**
     * @return the mInput
     */
    public CompositeMap getInput() {
        return mInput;
    }

    /**
     * @param input the mInput to set
     */
    public void setInput(CompositeMap input) {
        mInput = input;
    }
    
    
	
}