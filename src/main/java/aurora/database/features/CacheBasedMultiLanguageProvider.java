package aurora.database.features;

import java.text.MessageFormat;

import aurora.service.ServiceThreadLocal;
import uncertain.cache.ICache;
import uncertain.cache.INamedCacheFactory;
import uncertain.composite.TextParser;
import uncertain.core.IGlobalInstance;
import uncertain.exception.GeneralException;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectRegistry;

public class CacheBasedMultiLanguageProvider extends AbstractLocatableObject implements IGlobalInstance,ICacheBasedMultiLanguageProvider{

	private IObjectRegistry mObjectRegistry;
	private INamedCacheFactory mCacheFactory;
	
	private String cacheName;
	private String cacheKey="{0}.{1}";
	
	private ICache cache;
	
	public CacheBasedMultiLanguageProvider(IObjectRegistry objectRegistry,INamedCacheFactory cacheFactory) {
		this.mObjectRegistry = objectRegistry;
		this.mCacheFactory = cacheFactory;
	}
	public void onInitialize() throws Exception {
		cache = mCacheFactory.getNamedCache(cacheName);
		if (cache == null)
			throw new GeneralException("uncertain.cache.named_cache_not_found", new Object[] { cacheName }, this);
		mObjectRegistry.registerInstance(ICacheBasedMultiLanguageProvider.class, this);
	}
	
	public String getFullCacheKey(String description_id, String language) {
		String patten = TextParser.parse(cacheKey, ServiceThreadLocal.getCurrentThreadContext());
		return MessageFormat.format(patten, description_id,language);
	}

	public String getDescription(String description_id, String language) {
		return (String) cache.getValue(getFullCacheKey(description_id,language));
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
