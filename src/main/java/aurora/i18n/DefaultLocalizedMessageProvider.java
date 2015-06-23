package aurora.i18n;

import java.util.HashMap;

public class DefaultLocalizedMessageProvider implements
		ILocalizedMessageProvider {
	
	private HashMap cache = new HashMap();

	public String getMessage(String code) {
		return (String)cache.get(code);
	}

	public String getMessage(String code, Object[] params) {
		// TODO Auto-generated method stub
		return null;
	}

	public void putMessage(String code, String description) {
		cache.put(code, description);
	}

}
