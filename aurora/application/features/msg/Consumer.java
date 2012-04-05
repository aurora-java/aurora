package aurora.application.features.msg;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectRegistry;

public class Consumer extends AbstractLocatableObject implements IConsumer {
	
	private IObjectRegistry registry;
	
	private String topic;
	private String client;
	private Event[] events;
	
	private Map<String,String> eventMap = new HashMap<String,String>(); 
	private ILogger logger;

	private IMessageStub stub;
    public Consumer(IObjectRegistry registry) {
        this.registry = registry;
    }
	public void init(IMessageStub stub) throws Exception {
		if(topic ==null){
			throw BuiltinExceptionFactory.createAttributeMissing(this, "topic");
		}
		this.stub = stub;
		logger = LoggingContext.getLogger(this.getClass().getCanonicalName(), registry);
		logger.log(Level.CONFIG,"start Consumer for topic:"+topic+" successfull!");
	}
	public void onMessage(IMessage msg) throws Exception {
		String messageText = msg.getText();
		String handlerName = (String)eventMap.get(messageText);
		if(handlerName != null){
			IMessageHandler handler = (IMessageHandler)stub.getMessageHandler(handlerName);
			if(handler == null){
				logger.log(Level.SEVERE,"Error when handle jsm message."+" Can't find the handler for name:'"+handlerName+"'.");
				throw new IllegalStateException("Can't find the handler for name:'"+handlerName+"'.");
				
			}
			handler.onMessage(msg);
		}
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public void setEvents(Event[] events) {
		this.events = events;
		if(events != null){
			for (int i = 0; i < events.length; i++) {
				Event event = events[i];
				if(event.getHandler() != null)
					eventMap.put(event.getMessage(), event.getHandler());
			}
		}
	}
	public Event[] getEvents() {
		return events;
	}
	public String getClient() {
		return client;
	}
	public void setClient(String client) {
		this.client = client;
	}
	@Override
	public void onShutdown() {
		eventMap.clear();
	}
}
