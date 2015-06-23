/*
 * Created on 2010-6-3 下午01:49:49
 * $Id$
 */
package aurora.i18n;

public class DummyMessageProvider implements IMessageProvider {
    
    public static final DummyMessageProvider DEFAULT_INSTANCE = new DummyMessageProvider(); 

    public String getMessage(String language_code, String message_code) {
        return message_code;
    }

    public String getMessage(String language_code, String message_code, Object[] params) {
        return message_code;
    }

    public ILocalizedMessageProvider getLocalizedMessageProvider(String language_code) {
        return DummyLocalizedMessageProvider.DEFAULT_INSTANCE;
    }

	public String getDefaultLang() {
		return "ZHS";
	}

	public String getLangPath() {
		return "/session/@lang";
	}

}
