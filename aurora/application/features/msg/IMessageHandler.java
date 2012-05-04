package aurora.application.features.msg;


public interface IMessageHandler extends IMessageListener{
	public String getName();
	public void setName(String name);
}
