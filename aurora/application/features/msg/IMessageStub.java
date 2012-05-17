package aurora.application.features.msg;

public interface IMessageStub {
	
	public boolean isStarted();
	public IConsumer getConsumer(String topic);
	public IMessageDispatcher getDispatcher();
	public IMessageHandler getMessageHandler(String name);
}
