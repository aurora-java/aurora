package aurora.service.ws;

import uncertain.composite.CompositeMap;
import uncertain.core.ILifeCycle;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.AbstractLocatableObject;

public class SOAPConfiguration extends AbstractLocatableObject implements ISOAPConfiguration,ILifeCycle{

	
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

	@Override
	public boolean startup() {
		if(defaultResponse == null)
			throw BuiltinExceptionFactory.createNodeMissing(this, "soapResponse");
		return true;
	}

	@Override
	public void shutdown() {
		defaultResponse.clear();
	}
}
