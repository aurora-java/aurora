package aurora.application.features;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import uncertain.cache.ICache;
import uncertain.cache.INamedCacheFactory;
import uncertain.composite.CompositeMap;
import uncertain.core.IGlobalInstance;
import uncertain.exception.GeneralException;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectRegistry;
public class CacheBasedLookupCodeProvider extends AbstractLocatableObject implements ILookupCodeProvider, IGlobalInstance{

	private static final String DEFAULT_SORT_FIELD = "code_value_id";
	
	private INamedCacheFactory mCacheFactory;
	
	private String promptCacheName;
	private String listCacheName;
	
	private String lookupType = "cache";
	private String lookupSql;
	private String lookupModel;
	private String sortField;

	private ICache promptCache;
	private ICache listCache;
	
	public CacheBasedLookupCodeProvider(IObjectRegistry registry,INamedCacheFactory cacheFactory) {
		this.mCacheFactory = cacheFactory;
	}

	public List getLookupList(String language, String lookup_code) throws Exception {
		List result = (List) listCache.getValue(getFullCacheKey(lookup_code, language));
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
		Object prompt = promptCache.getValue(getFullCacheKey(lookup_code, lookup_value!=null?lookup_value.toString():null, language));
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

	public String getLookupType() {
		return lookupType;
	}

	public void setLookupType(String lookupType) {
		this.lookupType = lookupType;
	}

	public String getLookupSql() {
		return lookupSql;
	}

	public void setLookupSql(String lookupSql) {
		this.lookupSql = lookupSql;
	}

	public String getLookupModel() {
		return lookupModel;
	}

	public void setLookupModel(String lookupModel) {
		this.lookupModel = lookupModel;
	}

	public String getSortField() {
		return sortField == null ? DEFAULT_SORT_FIELD: sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public String getFullCacheKey(String code, String language) {
		return code + ICache.DEFAULT_CONNECT_CHAR + language;
	}
	public String getFullCacheKey(String code,String code_value, String language) {
		return code + ICache.DEFAULT_CONNECT_CHAR +code_value+ICache.DEFAULT_CONNECT_CHAR+language;
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
}
