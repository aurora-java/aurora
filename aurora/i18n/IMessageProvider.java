/*
 * Created on 2010-6-3 上午11:16:16
 * $Id$
 */
package aurora.i18n;

/**
 * Provide message lookup, to implement i18n in application 
 */
public interface IMessageProvider {
    
    /**
     * Get translated message for given languange
     * @param language_code code of languange, this is specific to application
     * @param message_code code of message
     * @return translated message for given language. If no translation found, 
     * the origin input code shall be returned
     */
    
    public String getMessage( String language_code, String message_code);
    
    public String getMessage( String language_code, String message_code, Object[] params );
    
    public String getLangPath();

	public String getDefaultLang();
    
    public ILocalizedMessageProvider getLocalizedMessageProvider( String language_code );

}
