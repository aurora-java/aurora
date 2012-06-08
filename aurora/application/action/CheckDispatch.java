package aurora.application.action;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;
import aurora.application.util.LanguageUtil;
import aurora.i18n.ILocalizedMessageProvider;
import aurora.service.ServiceContext;
import aurora.service.validation.ErrorMessage;

public class CheckDispatch extends AbstractEntry {
	String name;
	String field;
	String value;
	String dispatchUrl;
	String dispathcType;
	String message;
	IObjectRegistry        registry;
	
	public CheckDispatch(IObjectRegistry r){
	    this.registry = r;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void run(ProcedureRunner runner) throws Exception {
		CompositeMap context = runner.getContext();
		ServiceContext sc = ServiceContext.createServiceContext(context);
		
		String fieldvalue = (String)context.getObject(this.getField());
		String checkvalue = this.getValue();
		if (fieldvalue!= null && fieldvalue.equals(checkvalue)) {
			context.putBoolean("success", false);
            String url = TextParser.parse(this.getDispatchUrl(), context);
			context.put("dispatch_url", url);
			context.put("dispatch_type", getDispathcType());
			
            String msg = message==null?checkvalue:message;
            msg = LanguageUtil.getTranslatedMessage(registry, msg, context);
			
			ErrorMessage em = new ErrorMessage(checkvalue, msg, null);
			sc.setError(em.getObjectContext());
		} 
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDispatchUrl() {
		return dispatchUrl;
	}

	public void setDispatchUrl(String dispatchUrl) {
		this.dispatchUrl = dispatchUrl;
	}

	public String getDispathcType() {
		return dispathcType == null ? "redirect" : dispathcType;
	}

	public void setDispathcType(String dispathcType) {
		this.dispathcType = dispathcType;
	}

}
