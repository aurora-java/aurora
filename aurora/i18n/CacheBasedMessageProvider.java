package aurora.i18n;

import java.text.MessageFormat;
import java.util.HashMap;

import aurora.service.ServiceThreadLocal;

import uncertain.cache.ICache;
import uncertain.cache.INamedCacheFactory;
import uncertain.composite.TextParser;
import uncertain.core.IGlobalInstance;
import uncertain.exception.GeneralException;
import uncertain.ocm.AbstractLocatableObject;

public class CacheBasedMessageProvider extends AbstractLocatableObject implements IMessageProvider, IGlobalInstance {

	private INamedCacheFactory mCacheFactory;
	
	private String langPath = "";
	private String defaultLang = "";
	private String cacheKey="{0}.{1}";
	private String cacheName;
	
	private HashMap<String,ILocalizedMessageProvider> localMPMaps = new HashMap<String,ILocalizedMessageProvider> ();
	private ICache cache;
	
	public CacheBasedMessageProvider(INamedCacheFactory cacheFactory) {
		this.mCacheFactory = cacheFactory;
	}

	public void onInitialize() throws Exception {
		cache = mCacheFactory.getNamedCache(cacheName);
		if (cache == null)
			throw new GeneralException("uncertain.cache.named_cache_not_found", new Object[] { cacheName }, this);
	}

	public ILocalizedMessageProvider getLocalizedMessageProvider(String language_code) {
		ILocalizedMessageProvider messageProvider = (ILocalizedMessageProvider)localMPMaps.get(language_code);
		if(messageProvider == null){
			messageProvider = new CacheBasedLocalizedMessageProvider(language_code,this);
		}
		return messageProvider;
	}

	public String getMessage(String language_code, String message_code) {
		Object message = cache.getValue(getFullCacheKey(message_code, language_code));
		if(message == null)
			return message_code;
		return String.valueOf(message);
	}
	
	public void setMessage(String language_code, String message_code,String message) {
		cache.setValue(getFullCacheKey(message_code, language_code),message);
	}

	public String getMessage(String language_code, String message_code, Object[] params) {
		//not support params.
		return getMessage(language_code,message_code);
	}
	private String getFullCacheKey(String code, String language) {
		String patten = TextParser.parse(cacheKey, ServiceThreadLocal.getCurrentThreadContext());
		return MessageFormat.format(patten, code,language);
	}
	//TODO 实现了IMessageProvider接口，应该去掉
	public String getLangPath() {
		return langPath;
	}

	public void setLangPath(String langPath) {
		this.langPath = langPath;
	}

	public String getDefaultLang() {
		return defaultLang;
	}

	public void setDefaultLang(String defaultLang) {
		this.defaultLang = defaultLang;
	}
	public String getCacheName() {
		return cacheName;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	public String getCacheKey() {
		return cacheKey;
	}

	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}
}
