package aurora.application.features.msg;

public interface IMessageHandler{
	public String getName();
	public void setName(String name);
	public void onMessage(IMessage msg);
}
