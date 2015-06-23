package aurora.application.action;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.exception.ConfigurationFileException;
import uncertain.proc.Assert;
import uncertain.proc.ProcedureRunner;

public class Check extends Assert {

	boolean result = true;

	public boolean getResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	@Override
	public void run(ProcedureRunner runner) {

		if (field == null)
			// throw new
			// ConfigurationError("assert: 'field' property must be set");
			throw BuiltinExceptionFactory.createAttributeMissing(this, "field");
		if (operator == null)
			// throw new
			// ConfigurationError("assert: 'operator' property must be set");
			// throw BuiltinExceptionFactory.createAttributeMissing(this,
			// "operator");
			operator = "=";
		int opid = operatorID(operator);
		if (opid < 0)
			// throw new
			// ConfigurationError("assert: unknown operator "+Operator);
			throw new ConfigurationFileException(
					UNCERTAIN_PROC_ASSERT_UNKNOWN_OPERATOR,
					new Object[] { operator }, this);
		CompositeMap context = runner.getContext();
		Object test_field = context.getObject(field);

		// First, test for expression without operant
		switch (opid) {

		case NULL:
			if (test_field != null)
				result = false;
			break;
		case NOTNULL:
			if (test_field == null)
				result = false;
			break;
		// Then test for expression that must specify 'value'
		case EQUAL:
		case NOTEQUAL:
			value = TextParser.parse(value, context);
			if (opid == EQUAL) {
				if (test_field == null) {
					result = (value == null);
				} else if (isWrapClass(test_field.getClass())) {
					result = value == null ? false : (test_field.toString()
							.equals(value.toString()));
				} else
					result = test_field.equals(value);
			} else {
				if (test_field == null) {
					result = (value != null);
				} else if (isWrapClass(test_field.getClass())) {
					result = value == null ? true : (!test_field.toString()
							.equals(value.toString()));
				} else
					result = !test_field.equals(value);
			}
			break;
		// Here we got numeric expressions
		default:
			if (value == null)
				// throw new
				// ConfigurationError("assert: 'value' property must be set");
				throw BuiltinExceptionFactory.createAttributeMissing(this,
						"value");
			String parsed_value = TextParser.parse(value, context);
			Double d_value = null;
			if (test_field == null)
				result = false;
			try {
				d_value = new Double(Double.parseDouble(parsed_value));
			} catch (NumberFormatException ex) {
				// throw new
				// ConfigurationError("specified value '"+parsed_value+"' can't be parsed as number");
				throw BuiltinExceptionFactory.createValueNotNumberException(
						this, parsed_value);
			}
			Number d_test_field = null;
			// parse field to test into number
			if (test_field instanceof String) {
				d_test_field = new Double(
						Double.parseDouble((String) test_field));
			} else if (test_field instanceof Number) {
				d_test_field = (Number) test_field;
			} else
				result = false;
			// perform comparation
			switch (opid) {
			case GREAT_THAN:
				if (d_test_field.doubleValue() <= d_value.doubleValue())
					result = false;
				break;
			case LESS_THAN:
				if (d_test_field.doubleValue() >= d_value.doubleValue())
					result = false;
				break;
			case GOE:
				if (d_test_field.doubleValue() < d_value.doubleValue())
					result = false;
				break;
			case LOE:
				if (d_test_field.doubleValue() > d_value.doubleValue())
					result = false;
				break;
			} // end inner switch
		} // end outter switch
	}

	/**
	 * to check whether a class is a warp class of primitive type<br/>
	 * if clz is subclass of {@code Number} then return true(e.g.
	 * {@code BigInteger,BigDecimal})<br/>
	 * else if clz isPrimitive,return true<br/>
	 * else if clz is wrap of primitive class ,e.g.
	 * {@code Boolean,Integer,Double ,etc.}
	 * 
	 * @param clz
	 * @return
	 */
	private static boolean isWrapClass(Class<?> clz) {
		if (Number.class.isAssignableFrom(clz))
			return true;
		if (clz.isPrimitive())
			return true;
		try {
			return ((Class<?>) clz.getField("TYPE").get(null)).isPrimitive();
		} catch (Exception e) {
			return false;
		}
	}

}
