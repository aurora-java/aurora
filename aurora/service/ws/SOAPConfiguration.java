package aurora.service.ws;

import uncertain.composite.CompositeMap;
import uncertain.core.IGlobalInstance;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.AbstractLocatableObject;

public class SOAPConfiguration extends AbstractLocatableObject implements ISOAPConfiguration,IGlobalInstance{

	
	private CompositeMap errorResponseTemplate;
	@Override
	public CompositeMap getErrorResponseTemplate() {
		return errorResponseTemplate;
	}
	public void setErrorResponseTemplate(CompositeMap errorResponseTemplate){
		this.errorResponseTemplate = errorResponseTemplate;
		String ert_text = errorResponseTemplate.getText();
		if (ert_text == null || "".equals(ert_text)) {
			throw BuiltinExceptionFactory.createCDATAMissing(this, "errorResponseTemplate");
		}
	}

}
