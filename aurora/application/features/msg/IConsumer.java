package aurora.application.features.msg;

public interface IConsumer {
	
	public String getTopic();

	public void init(IMessageStub stub)throws Exception;
	
	public void onMessage(IMessage msg)throws Exception;

	public void onShutdown();

}
