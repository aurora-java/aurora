package aurora.application.action;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.OCManager;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;
import aurora.application.util.LanguageUtil;
import aurora.service.ServiceContext;
import aurora.service.validation.ErrorMessage;

public class Test extends AbstractEntry {
	String name;
	String field;
	String value;
	private boolean assertEquals = true;
	private String valueField;
	private OCManager oc_manager;

	public Test(OCManager oc_manager, IObjectRegistry registry) {
		this.oc_manager = oc_manager;
		this.registry = registry;
	}

	private String errorMessage;
	IObjectRegistry registry;

	public Test() {
	}

	public void run(ProcedureRunner runner) throws Exception {
		CompositeMap context = runner.getContext();
//		System.out.println(context.toXML());
		ServiceContext sc = ServiceContext.createServiceContext(context);
		Object object = getFieldValue(context, this.getField());
		String fieldvalue = object == null ? "null" : object.toString();
		Object checkvalue = this.getValue();
		if (checkvalue == null && this.getValueField() != null) {
			checkvalue = getFieldValue(context, this.getValueField());
		}
		if (assertEquals == fieldvalue.equals(checkvalue)) {
			context.putBoolean("success", false);
			String msg = errorMessage == null ? "" + checkvalue : errorMessage;
			msg = LanguageUtil.getTranslatedMessage(registry, msg, context);
			ErrorMessage em = new ErrorMessage("" + checkvalue, msg, null);
			sc.setError(em.getObjectContext());
			runner.stop();
		}
	}

	private Object getFieldValue(CompositeMap context, String field) {
		Object object = context.getObject(field);
		return object;
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

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getValueField() {
		return valueField;
	}

	public void setValueField(String valueField) {
		this.valueField = valueField;
	}

	public boolean getAssertEquals() {
		return assertEquals;
	}

	public void setAssertEquals(boolean assertEquals) {
		this.assertEquals = assertEquals;
	}

}