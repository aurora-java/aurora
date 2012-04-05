package aurora.i18n;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import uncertain.cache.ICache;
import uncertain.cache.INamedCacheFactory;
import uncertain.composite.CompositeMap;
import uncertain.core.IGlobalInstance;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.exception.GeneralException;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectRegistry;
import aurora.database.FetchDescriptor;
import aurora.database.service.BusinessModelService;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.database.service.SqlServiceContext;

public class CacheBasedMessageProvider extends AbstractLocatableObject implements IMessageProvider, IGlobalInstance {

	private IDatabaseServiceFactory mFactory;
	private INamedCacheFactory mCacheFactory;
	
	private String langPath = "";
	private String defaultLang = "";
	private String languageColumn;
	private String keyColumn;
	private String valueColumn;
	private String bm;
	private String cacheName;
	
	private HashMap localMPMaps = new HashMap();
	private ICache cache;
	
	public CacheBasedMessageProvider(IDatabaseServiceFactory factory,INamedCacheFactory cacheFactory) {
		this.mFactory = factory;
		this.mCacheFactory = cacheFactory;
	}

	public void onInitialize() throws Exception {
		cache = mCacheFactory.getNamedCache(cacheName);
		if (cache == null)
			throw new GeneralException("uncertain.cache.named_cache_not_found", new Object[] { cacheName }, this);
		cacheMessage();
	}

	public ILocalizedMessageProvider getLocalizedMessageProvider(String language_code) {
		return (ILocalizedMessageProvider) localMPMaps.get(language_code);
	}

	public String getMessage(String language_code, String message_code) {
		return String.valueOf(cache.getValue(getFullCacheKey(message_code, language_code)));
	}

	public String getMessage(String language_code, String message_code, Object[] params) {
		ILocalizedMessageProvider localMessageProvider = (ILocalizedMessageProvider) localMPMaps.get(language_code);
		if (localMessageProvider == null)
			return message_code;
		return localMessageProvider.getMessage(message_code, params);
	}
	private void cacheMessage() throws Exception {
		CompositeMap resultMap = queryBM(new CompositeMap());
		if (resultMap != null) {
			List list = resultMap.getChildsNotNull();
			Iterator it = list.iterator();
			while (it.hasNext()) {
				CompositeMap message = (CompositeMap) it.next();
				String language = message.getString(getLanguageColumn());
				ILocalizedMessageProvider localMessageProvider = (ILocalizedMessageProvider) localMPMaps.get(language);
				if (localMessageProvider == null) {
					localMessageProvider = new CacheBasedLocalizedMessageProvider(language, cache);
					localMPMaps.put(language, localMessageProvider);
				}
				String code = message.getString(getKeyColumn());
				String description = message.getString(getValueColumn());
				localMessageProvider.putMessage(code, description);
			}
		}
	}
	private String getFullCacheKey(String code, String langugage) {
		return code + ICache.DEFAULT_CONNECT_CHAR + langugage;
	}
	public CompositeMap queryBM(CompositeMap queryMap) throws Exception {
		SqlServiceContext sqlContext = mFactory.createContextWithConnection();
		try {
			CompositeMap context = sqlContext.getObjectContext();
			if (context == null)
				context = new CompositeMap();
			BusinessModelService service = mFactory.getModelService(getBm(), context);
			CompositeMap resultMap = service.queryAsMap(queryMap, FetchDescriptor.fetchAll());
			return resultMap;
		} finally {
			if (sqlContext != null)
				sqlContext.freeConnection();
		}

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

	public String getLanguageColumn() {
		return languageColumn;
	}

	public void setLanguageColumn(String languageColumn) {
		this.languageColumn = languageColumn;
	}

	public String getKeyColumn() {
		return keyColumn;
	}

	public void setKeyColumn(String keyColumn) {
		this.keyColumn = keyColumn;
	}

	public String getValueColumn() {
		return valueColumn;
	}

	public void setValueColumn(String valueColumn) {
		this.valueColumn = valueColumn;
	}

	public String getBm() {
		return bm;
	}

	public void setBm(String bm) {
		this.bm = bm;
	}

	public String getCacheName() {
		return cacheName;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}
	
}
