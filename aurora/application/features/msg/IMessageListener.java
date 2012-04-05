package aurora.application.features.msg;

public interface IMessageListener {
	public void notice(IMessage message) throws Exception;
}
