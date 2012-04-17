package aurora.i18n;

import uncertain.cache.ICache;

public class CacheBasedLocalizedMessageProvider implements ILocalizedMessageProvider {

	private String lang;
	private CacheBasedMessageProvider messageProvider;
	
	public CacheBasedLocalizedMessageProvider(String language_code, CacheBasedMessageProvider messageProvider) {
		this.lang = language_code;
		this.messageProvider = messageProvider;
	}
	public String getMessage(String code) {
		return (String) messageProvider.getMessage(lang, code);
	}

	public String getMessage(String code, Object[] params) {
		return messageProvider.getMessage(lang, code, params);
	}

	public void putMessage(String code, String description) {
		messageProvider.setMessage(lang,code, description);
	}

	public void setLang(String lang) {
		this.lang = lang;

	}

	public String getLang() {
		return lang;
	}

	public String getKey(String code) {
		return code + ICache.DEFAULT_CONNECT_CHAR + lang;
	}

}
