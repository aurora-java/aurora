/**
 * Created on: 2002-11-14 17:18:32
 * Author:     zhoufan
 */
package aurora.service.validation;


/**
 * 
 */
public class DatatypeMismatchException extends FieldValidationException {
	
	Class      mExpectedClass;
    Object     mInputValue;

	/**
	 * Constructor for DatatypeMismatchException.
	 */
	public DatatypeMismatchException(
		Class  expected_class,
		String parameter_name,
		Object input_value,
		Exception parse_exception
		) 
	{
		super(parameter_name, parse_exception);
        mExpectedClass = expected_class;
        mInputValue = input_value;
	}


	public Class getExpectedClass(){
		return mExpectedClass;
	}

}
