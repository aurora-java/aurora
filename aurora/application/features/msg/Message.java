package aurora.application.features.msg;

import javax.jms.JMSException;

import uncertain.composite.CompositeMap;

public class Message implements IMessage {

	private String text;
	private CompositeMap properties;
	
	public Message(String text, CompositeMap properties) {
		super();
		this.text = text;
		this.properties = properties;
	}


	@Override
	public String getText() throws JMSException {
		return text;
	}

	public void setText(String text) throws JMSException {
		this.text = text;
	}

	public CompositeMap getProperties() {
		return properties;
	}

	public void setProperties(CompositeMap properties) {
		this.properties = properties;
	}
	

}
