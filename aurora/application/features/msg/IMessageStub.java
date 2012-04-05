package aurora.application.features.msg;

import uncertain.composite.CompositeMap;

public interface IMessageStub {
	public IConsumer getConsumer(String topic);
	public IMessageDispatcher getDispatcher(String topic);
	public IMessageHandler getMessageHandler(String name);
	public void send(String topic,IMessage message,CompositeMap context) throws Exception;
}
