/*
 * Created on 2010-12-15 下午04:11:24
 * $Id$
 */
package aurora.i18n;

import uncertain.util.template.ITagContent;
import uncertain.util.template.ITagCreator;

public class LocalizedMessageTagCreator implements ITagCreator {
    
    /**
     * @param messageProvider
     */
    public LocalizedMessageTagCreator(ILocalizedMessageProvider messageProvider) {
        this.messageProvider = messageProvider;
    }

    ILocalizedMessageProvider       messageProvider;
    public static final String LOCALIZED_MESSAGE_NAMESPACE = "l";

    public ITagContent createInstance(String namespace, String tag) {
        return new LocalizedMessageTagContent( tag, messageProvider );
    }

    public ILocalizedMessageProvider getMessageProvider() {
        return messageProvider;
    }

    public void setMessageProvider(ILocalizedMessageProvider messageProvider) {
        this.messageProvider = messageProvider;
    }

}
