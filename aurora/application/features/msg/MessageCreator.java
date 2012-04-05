package aurora.application.features.msg;


import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

public class MessageCreator extends AbstractEntry{
	public static final String MESSAGE_ATTR = "message";
	public static final String TOPIC_ATTR = "topic";
	
	private String message;
	private String topic;
	private Property[] properties;
	
	private IObjectRegistry mRegistry;
	
	public MessageCreator(IObjectRegistry registry) {
		this.mRegistry = registry;
	}

	@Override
	public void run(ProcedureRunner runner) throws Exception {
		if(message == null)
			throw BuiltinExceptionFactory.createAttributeMissing(this, "message");
		if(topic == null)
			throw BuiltinExceptionFactory.createAttributeMissing(this, "topic");
		CompositeMap context = runner.getContext();
        CompositeMap pps = new CompositeMap("property");
        String parsedTopic = TextParser.parse(topic, context);
        String parsedMessage = TextParser.parse(message, context);
        if(properties != null){
        	for(Property p:properties){
        		p.putToMap(context,pps,true);
        	}
        }
        IMessageStub messageStub = (IMessageStub)mRegistry.getInstanceOfType(IMessageStub.class);
        if(messageStub == null)
        	throw BuiltinExceptionFactory.createInstanceNotFoundException(null, IMessageStub.class, this.getClass().getCanonicalName());
        Message msg = new Message(parsedMessage,pps);
        messageStub.send(parsedTopic, msg, context);
	}

	public Property[] getProperties() {
		return properties;
	}

	public void setProperties(Property[] properties) {
		this.properties = properties;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}
	
}
