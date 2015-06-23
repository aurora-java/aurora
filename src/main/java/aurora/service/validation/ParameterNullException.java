/**
 * Created on: 2002-11-14 21:14:08
 * Author:     zhoufan
 */
package aurora.service.validation;

/**
 * Exception to indicate that a required field is null
 */
public class ParameterNullException extends FieldValidationException {

	public ParameterNullException(	String parameter_name) {
		super(parameter_name, null);
	}
	
	public String toString(){
	    return "parameter "+mFieldName+" is null";
	}
	

}
