/*
 * Created on 2008-7-2
 */
package aurora.service.validation;

public class FieldValidationException extends Exception {
    
    String          mFieldName;
    
    public FieldValidationException( String field, Throwable cause ){
        super(cause);
        mFieldName = field;        
    }

    /**
     * @return the fieldName
     */
    public String getFieldName() {
        return mFieldName;
    }

    /**
     * @param fieldName the fieldName to set
     */
    public void setFieldName(String fieldName) {
        mFieldName = fieldName;
    }

}
