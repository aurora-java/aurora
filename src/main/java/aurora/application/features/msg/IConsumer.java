package aurora.application.features.msg;

public interface IConsumer extends IMessageListener{
	
	public String getTopic();

	public void init(IMessageStub stub)throws Exception;

	public void onShutdown();

}
