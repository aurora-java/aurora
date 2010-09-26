package aurora.application.action;

import aurora.service.ServiceInstance;
import aurora.service.http.HttpServiceInstance;
import uncertain.composite.CompositeMap;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

public class CheckDispatch extends AbstractEntry {
	 
	public void run(ProcedureRunner runner) throws Exception {
		CompositeMap context = runner.getContext();
		String fieldvalue=context.getObject(this.getField()).toString();
		String checkvalue=this.getValue();
		if (fieldvalue.equals(checkvalue)){
			context.put("success","false");
			context.put("dispatch_url",this.getDispatchUrl());
		}else
		{
			 context.put("success","true");
		}
	}
	String name;
	String field;
	String value;
	String dispatchUrl;
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
