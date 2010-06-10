/*
 * Created on 2010-6-3 下午02:11:44
 * $Id$
 */
package aurora.i18n;

public class DummyLocalizedMessageProvider implements ILocalizedMessageProvider {
    
    public static final DummyLocalizedMessageProvider DEFAULT_INSTANCE = new DummyLocalizedMessageProvider(); 

    public String getMessage(String message_code) {
        return message_code;
    }

    public String getMessage(String message_code, Object[] params) {
        return message_code;
    }

	public void putMessage(String code, String description) {
		
	}
}
