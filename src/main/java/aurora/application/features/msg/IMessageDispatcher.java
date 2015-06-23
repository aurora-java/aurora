package aurora.application.features.msg;

import uncertain.composite.CompositeMap;

public interface IMessageDispatcher {
	public void send(String topic,IMessage message,CompositeMap context) throws Exception;
}
