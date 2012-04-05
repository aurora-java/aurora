package aurora.application.features.msg;

import uncertain.composite.CompositeMap;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.IConfigurable;

public class Event implements IConfigurable{
	public static final String MESSAGE_ATTR="message";
	public static final String HANDLER_ATTR="handler";
	private String message;
	private String handler;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getHandler() {
		return handler;
	}
	public void setHandler(String handler) {
		this.handler = handler;
	}
	public void beginConfigure(CompositeMap config) {
		if(config.get(MESSAGE_ATTR)==null){
			throw BuiltinExceptionFactory.createAttributeMissing(config.asLocatable(), MESSAGE_ATTR);
		}
//		if(config.get(HANDLER_ATTR)==null){
//			throw BuiltinExceptionFactory.createAttributeMissing(config.asLocatable(), HANDLER_ATTR);
//		}
	}
	public void endConfigure() {
	}
}
