package aurora.application.features.msg;

import uncertain.exception.MessageFactory;

public final class MessageCodes {
	static{
		MessageFactory.loadResource("resources.aurora_msg_exception");
	}
	public static final String MESSAGE_TYPE_ERROR = "aurora.msg.message_type_error";
	public static final String MESSAGE_ERROR = "aurora.msg.message_error";
	public static final String JMSEXCEPTION_ERROR = "aurora.msg.jmsexception_error";
	public static final String SAX_ERRORR ="aurora.msg.sax_errorr";
	public static final String GENEANL_SYS_RAISE_ERROR ="aurora.msg.geneanl_sys_raise_error";
	public static final String HANDLER_NOT_FOUND_ERROR ="aurora.msg.handler_not_found_error";
}
