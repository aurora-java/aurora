package aurora.application.features.cache;

import uncertain.ocm.IObjectRegistry;
import aurora.application.features.msg.IMessageListener;

public interface IEventHandler extends IMessageListener{
	
	public final static String SERPRATOR_CHAR=",";
	
	public enum OPERATIONS {
		insert, update, delete, reload
	}
	public void init(ICacheProvider provider,IObjectRegistry registry);
	public void setTopic(String topic);
	public String getTopic();
}
