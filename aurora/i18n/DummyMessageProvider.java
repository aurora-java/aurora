/*
 * Created on 2010-6-3 下午01:49:49
 * $Id$
 */
package aurora.i18n;

public class DummyMessageProvider implements IMessageProvider {
    
    public static final DummyMessageProvider DEFAULT_INSTANCE = new DummyMessageProvider(); 

    public String getMessage(String language_code, String message_code) {
        // TODO Auto-generated method stub
        return message_code;
    }

    public String getMessage(String language_code, String message_code,
            Object[] params) {
        // TODO Auto-generated method stub
        return message_code;
    }

}
