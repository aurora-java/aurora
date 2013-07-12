package aurora.service.ws;

import uncertain.composite.CompositeMap;
import uncertain.core.IGlobalInstance;
import uncertain.ocm.AbstractLocatableObject;

public class SOAPConfiguration extends AbstractLocatableObject implements ISOAPConfiguration,IGlobalInstance{

	
	private CompositeMap defaultResponse;
	private String model;
	@Override
	public CompositeMap getDefaultResponse() {
		return defaultResponse;
	}
	
	public CompositeMap getSoapResponse() {
		return defaultResponse;
	}
	public void setSoapResponse(CompositeMap defaultResponse){
		this.defaultResponse = defaultResponse;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
}
