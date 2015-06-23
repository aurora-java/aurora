/*
 * Created on 2010-12-15 下午04:01:32
 * $Id$
 */
package aurora.i18n;

import uncertain.composite.CompositeMap;
import uncertain.util.template.ITagContent;

public class LocalizedMessageTagContent implements ITagContent {
    
    /**
     * @param code
     * @param messageProvider
     */
    public LocalizedMessageTagContent(String code,
            ILocalizedMessageProvider messageProvider) {
        this.code = code;
        this.messageProvider = messageProvider;
    }

    String                          code;
    ILocalizedMessageProvider       messageProvider;

    public String getContent(CompositeMap context) {
    	if (messageProvider != null) {
		      String p = messageProvider.getMessage(code);
		      return p == null ? code : p;
		  } else {
		      return code;
		  }
//        return messageProvider.getMessage(code);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ILocalizedMessageProvider getMessageProvider() {
        return messageProvider;
    }

    public void setMessageProvider(ILocalizedMessageProvider messageProvider) {
        this.messageProvider = messageProvider;
    }

}
