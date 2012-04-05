package aurora.i18n;

import java.util.Locale;

import uncertain.cache.ICache;
import uncertain.cache.MapBasedCache;

public class CacheBasedLocalizedMessageProvider implements ILocalizedMessageProvider {

	private ICache cache;
	private String lang;

	public CacheBasedLocalizedMessageProvider(String lang, ICache cache) {
		this.cache = cache;
		this.lang = lang;
		if (lang == null)
			this.lang = Locale.getDefault().getLanguage();
		if (cache == null)
			cache = new MapBasedCache();
	}
	public CacheBasedLocalizedMessageProvider() {
		lang = Locale.getDefault().getLanguage();
		cache = new MapBasedCache();
	}

	public String getMessage(String code) {
		return (String) cache.getValue(getKey(code));
	}

	public String getMessage(String code, Object[] params) {
		return null;
	}

	public void putMessage(String code, String description) {
		cache.setValue(getKey(code), description);
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
