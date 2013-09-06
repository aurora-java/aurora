package aurora.application.action;

import uncertain.composite.CompositeMap;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;
import aurora.service.ServiceContext;

public class JavaScriptEncode extends AbstractEntry {

	public String key = null;

	@Override
	public void run(ProcedureRunner runner) throws Exception {
		CompositeMap context = runner.getContext();
		ServiceContext service = ServiceContext.createServiceContext(context);
		CompositeMap params = service.getParameter();
		String key = getKey();
		if (key != null) {
			String keyValue = params.getString(key);
			if(keyValue!=null){
				params.put(key, filter(keyValue));				
			}
		}
	}

	public String filter(String value) {
		if (value == null) {
			return null;
		}
		StringBuffer result = new StringBuffer(value.length());
		for (int i = 0; i < value.length(); ++i) {
			switch (value.charAt(i)) {
			case '<':
				result.append("&lt;");
				break;
			case '>':
				result.append("&gt;");
				break;
			case '"':
				result.append("&quot;");
				break;
			case '\'':
				result.append("&#39;");
				break;
			case '%':
				result.append("&#37;");
				break;
			case ';':
				result.append("&#59;");
				break;
			case '(':
				result.append("&#40;");
				break;
			case ')':
				result.append("&#41;");
				break;
			case '&':
				result.append("&amp;");
				break;
			case '+':
				result.append("&#43;");
				break;
			default:
				result.append(value.charAt(i));
				break;
			}
		}
		return result.toString();
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
