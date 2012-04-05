package aurora.application.features.msg;


public interface INoticerConsumer extends IConsumer{
	public void addListener(String message,IMessageListener listener);
	public void removeListener(String message,IMessageListener listener);
}
