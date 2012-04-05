package aurora.application.features.msg;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectRegistry;

public class DefaultNoticeConsumer extends AbstractLocatableObject implements INoticerConsumer{

	private IObjectRegistry registry;
	
	private String topic;
	
	private Map<String,List<IMessageListener>> messageListeners = new HashMap<String,List<IMessageListener>>(); 
	private ILogger logger;
	
	public DefaultNoticeConsumer(IObjectRegistry registry){
		this.registry = registry;
	}
	
	@Override
	public void init(IMessageStub stub) throws Exception {
		if(topic ==null){
			throw BuiltinExceptionFactory.createAttributeMissing(this, "topic");
		}
		logger = LoggingContext.getLogger(this.getClass().getCanonicalName(), registry);
		logger.log(Level.CONFIG,"start Consumer for topic:"+topic+" successfull!");
	}

	@Override
	public void onMessage(IMessage msg) throws Exception {
		String messageText = null;
		messageText = msg.getText();
		List<IMessageListener> listeners = messageListeners.get(messageText);
		if(listeners != null){
			for(IMessageListener l:listeners){
				try {
					l.notice(msg);
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Listener:"+l.toString()+" occur exception.", e);
					throw new RuntimeException("Listener:"+l.toString()+" occur exception.",e);
				}
			}
		}
		
	}

	@Override
	public void onShutdown() {
		messageListeners.clear();
	}

	public void addListener(String message, IMessageListener listener) {
		List<IMessageListener> listeners = messageListeners.get(message);
		if(listeners == null){
			listeners = new LinkedList<IMessageListener>();
			messageListeners.put(message, listeners);
		}
		if(!listeners.contains(listener))
			listeners.add(listener);
	}


	public void removeListener(String message, IMessageListener listener) {
		List<IMessageListener> listeners = messageListeners.get(message);
		if(listeners == null){
			return;
		}
		listeners.remove(listener);
	}

	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}

}
