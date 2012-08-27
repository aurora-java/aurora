package aurora.application.script.engine;

import javax.script.ScriptException;

/**
 * to indicate that ,the exception is raised by user to force stop engine
 * running ( and also stop ProcedureRunner).<br/>
 * currently,
 * {@code raise_app_error(code) method in javascript will throw this exception}
 * 
 * @author jessen
 * 
 */
public class InterruptException extends ScriptException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4300709867924558399L;

	public InterruptException(String s) {
		super(s);
	}

}
