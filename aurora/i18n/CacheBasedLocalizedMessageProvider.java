package aurora.i18n;

import java.text.MessageFormat;

import aurora.service.ServiceThreadLocal;
import uncertain.composite.TextParser;

public class CacheBasedLocalizedMessageProvider implements ILocalizedMessageProvider {

	private String lang;
	private String cacheKey="{0}.{1}";
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
		String patten = TextParser.parse(cacheKey, ServiceThreadLocal.getCurrentThreadContext());
		return MessageFormat.format(patten,code,lang);
	}
	public String getCacheKey() {
		return cacheKey;
	}
	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}

}
