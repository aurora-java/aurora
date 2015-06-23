package aurora.application.features.msg;

public interface IMessageStub {
	public static final int STOP_STATUS = 0;
	public static final int STARTING_STATUS = 1;
	public static final int STARTED_STATUS = 2;
	public boolean isStarted();
	public IConsumer getConsumer(String topic);
	public IMessageDispatcher getDispatcher();
	public IMessageHandler getMessageHandler(String name);
}
