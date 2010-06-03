/*
 * Created on 2010-6-3 下午02:11:44
 * $Id$
 */
package aurora.i18n;

public class DummyLocalizedMessageProvider implements ILocalizedMessageProvider {
    
    public static final DummyLocalizedMessageProvider DEFAULT_INSTANCE = new DummyLocalizedMessageProvider(); 

    public String getMessage(String message_code) {
        // TODO Auto-generated method stub
        return message_code;
    }

    public String getMessage(String message_code, Object[] params) {
        // TODO Auto-generated method stub
        return message_code;
    }
    
    

}
