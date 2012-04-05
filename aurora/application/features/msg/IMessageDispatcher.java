package aurora.application.features.msg;

import uncertain.composite.CompositeMap;

public interface IMessageDispatcher {
	public String getTopic();
	public void setTopic(String topic);
	public void send(IMessage message,CompositeMap context) throws Exception;
}
