package aurora.application.features;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import aurora.service.ServiceThreadLocal;

import uncertain.cache.ICache;
import uncertain.cache.INamedCacheFactory;
import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.core.IGlobalInstance;
import uncertain.exception.GeneralException;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectRegistry;
public class CacheBasedLookupCodeProvider extends AbstractLocatableObject implements ILookupCodeProvider, IGlobalInstance{

	private static final String DEFAULT_SORT_FIELD = "code_value_id";
	
	private INamedCacheFactory mCacheFactory;
	
	private String promptCacheName;
	private String listCacheName;
	private String promptCacheKey="{0}.{1}.{2}";
	private String listCacheKey="{0}.{1}";

	private String sortField;

	private ICache promptCache;
	private ICache listCache;
	
	public CacheBasedLookupCodeProvider(IObjectRegistry registry,INamedCacheFactory cacheFactory) {
		this.mCacheFactory = cacheFactory;
	}

	public List getLookupList(String language, String lookup_code) throws Exception {
		List result = (List) listCache.getValue(getFullListCacheKey(lookup_code, language));
		if (result != null)
			sorList(result);
		return result;
	}

	private void sorList(List result) {
		Collections.sort(result, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				CompositeMap r1 = (CompositeMap) arg0;
				CompositeMap r2 = (CompositeMap) arg1;
				String object1 = r1.getString(getSortField());
				String object2 = r2.getString(getSortField());
				return object1.compareTo(object2);
			}
		});
	}

	public String getLookupPrompt(String language, String lookup_code, Object lookup_value) {
		Object prompt = promptCache.getValue(getFullPromptCacheKey(lookup_code, lookup_value!=null?lookup_value.toString():null, language));
		if(prompt != null)
			return prompt.toString();
		return null;
	}

	public void onInitialize() throws Exception {
		promptCache = mCacheFactory.getNamedCache(promptCacheName);
		if (promptCache == null)
			throw new GeneralException("uncertain.cache.named_cache_not_found", new Object[] { promptCacheName }, this);
		listCache = mCacheFactory.getNamedCache(listCacheName);
		if (listCache == null)
			throw new GeneralException("uncertain.cache.named_cache_not_found", new Object[] { listCacheName }, this);
	}

	public String getSortField() {
		return sortField == null ? DEFAULT_SORT_FIELD: sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public String getFullListCacheKey(String code, String language) {
		String patten = TextParser.parse(listCacheKey, ServiceThreadLocal.getCurrentThreadContext());
		return MessageFormat.format(patten, code,language);
	}
	public String getFullPromptCacheKey(String code,String code_value, String language) {
		String patten = TextParser.parse(promptCacheKey, ServiceThreadLocal.getCurrentThreadContext());
		return MessageFormat.format(patten, code,code_value,language);
	}

	public String getListCacheName() {
		return listCacheName;
	}

	public void setListCacheName(String listCacheName) {
		this.listCacheName = listCacheName;
	}

	public String getPromptCacheName() {
		return promptCacheName;
	}

	public void setPromptCacheName(String promptCacheName) {
		this.promptCacheName = promptCacheName;
	}

	public String getListCacheKey() {
		return listCacheKey;
	}

	public void setListCacheKey(String listCacheKey) {
		this.listCacheKey = listCacheKey;
	}

	public String getPromptCacheKey() {
		return promptCacheKey;
	}

	public void setPromptCacheKey(String promptCacheKey) {
		this.promptCacheKey = promptCacheKey;
	}
}
