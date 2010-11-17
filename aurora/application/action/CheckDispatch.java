package aurora.application.action;

import aurora.service.ServiceInstance;
import aurora.service.http.HttpServiceInstance;
import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

public class CheckDispatch extends AbstractEntry {
	String name;
	String field;
	String value;
	String dispatchUrl;
	String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void run(ProcedureRunner runner) throws Exception {
		CompositeMap context = runner.getContext();
		String fieldvalue = context.getObject(this.getField()).toString();
		String checkvalue = this.getValue();
		if (fieldvalue.equals(checkvalue)) {
			String errorMessage =context.getObject(this.getMessage()).toString();
			context.put("success", "false");
			context.put("error_msg",errorMessage);
			String url = TextParser.parse(this.getDispatchUrl(), context);
			context.put("dispatch_url", url);
		} else {
			context.put("success", "true");
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

}
