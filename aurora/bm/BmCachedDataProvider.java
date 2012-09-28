/*
 * Created on 2012-9-14 下午04:24:31
 * $Id$
 */
package aurora.bm;

import java.util.LinkedList;
import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;

public class BmCachedDataProvider implements ICachedDataProvider {
    
    String  cacheKey;
    String  cacheName;
    
	@Override
	public String getCacheName(BusinessModel model) {
		if(cacheName != null)
			return cacheName;
		return model.getName();
	}

	@Override
	public String getCacheKey(BusinessModel model) {
		if (cacheKey != null)
			return cacheKey;
		Field[] flds = model.getPrimaryKeyFields();
		if (flds == null || flds.length == 0)
			throw new IllegalArgumentException("Business model " + model.getName() + " must has primary key");
		List<String> fields = new LinkedList<String>();
		for (int i = 0; i < flds.length; i++) {
			fields.add(flds[i].getName());
		}
		return model.getName() + "." + generateKey(fields);
	}
	@Override
	public String getParsedCacheKey(BusinessModel model, CompositeMap record) {
		if(record == null)
			return null;
		String cacheKey = getCacheKey(model);
		return TextParser.parse(cacheKey, record);
	}
	public String generateKey(List<String> fieldNames) {
		if(fieldNames == null)
			return null;
		StringBuffer sb = new StringBuffer();
		for (String fieldName : fieldNames) {
			sb.append("${@" + fieldName.toLowerCase() + "}.");
		}
		String cacheKey = sb.substring(0, sb.length() - 1);
		return cacheKey;
	}

	public String getCacheKey() {
		return cacheKey;
	}

	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}

	public String getCacheName() {
		return cacheName;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}


}
