package aurora.application.features.msg;

import uncertain.composite.CompositeMap;

public interface IMessage {
	public String getText() throws Exception;
	public CompositeMap getProperties() throws Exception;
}
