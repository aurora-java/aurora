/*
 * Created on 2010-6-3 下午01:59:08
 * $Id$
 */
package aurora.i18n;

public interface ILocalizedMessageProvider {
    
    public String getMessage( String message_code);
    
    public String getMessage( String message_code, Object[] params );    

    public void putMessage( String code, String description);    
}
