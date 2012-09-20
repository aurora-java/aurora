/*
 * Created on 2012-9-14 下午04:24:31
 * $Id$
 */
package aurora.bm;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import uncertain.cache.CacheBuiltinExceptionFactory;
import uncertain.cache.ICache;
import uncertain.cache.INamedCacheFactory;
import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.core.ILifeCycle;
import uncertain.ocm.IObjectRegistry;
import aurora.database.IResultSetConsumer;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.database.sql.Join;

public class BmCachedDataProvider implements ICachedDataProvider, ILifeCycle {
    
    String  cacheKey;
    String  cacheName;
    
    IObjectRegistry      mRegistry;
    IDatabaseServiceFactory dbServiceFactory;
    
    
    public BmCachedDataProvider(IObjectRegistry objectRegistry,IDatabaseServiceFactory dbs){
        this.mRegistry = objectRegistry;
        this.dbServiceFactory = dbs;
    }

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
		HashSet<String> fields = new HashSet<String>();
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

    @Override
    public boolean startup() {
        if(dbServiceFactory instanceof DatabaseServiceFactory){
        	 ((DatabaseServiceFactory)dbServiceFactory).setGlobalParticipant(this);
        }
        return true;
    }

    @Override
    public void shutdown() {
    }

    public void postFetchResultSet(BusinessModel model, IResultSetConsumer consumer) throws Exception {
		List<RelationFields> cacheJoinList = model.getCacheJoinList();
		if (cacheJoinList== null || cacheJoinList.size() == 0)
			return;
		
		Object result = consumer.getResult();
		if (!(result instanceof CompositeMap))
			return;
		CompositeMap data = (CompositeMap) result;
		if (data.getChilds() == null)
			return;
		
		INamedCacheFactory nameCacheFactory = (INamedCacheFactory) mRegistry.getInstanceOfType(INamedCacheFactory.class);
		String lookup_key = null;
		Relation relation = null;
		ICache cache = null;
		CompositeMap cachedRecord = null;
		for (@SuppressWarnings("unchecked")
		Iterator<CompositeMap> it = data.getChildIterator(); it.hasNext();) {
			CompositeMap record = it.next();
			for (RelationFields  relationField: cacheJoinList) {
				relation = relationField.getRelation();
				lookup_key = generateLookupCacheKey(relation,record);
				cache = nameCacheFactory.getNamedCache(relation.getReferenceModel());
				if(cache == null)
						throw CacheBuiltinExceptionFactory.createNamedCacheNotFound(null, relation.getReferenceModel());
				cachedRecord = (CompositeMap)cache.getValue(lookup_key);
				if (cachedRecord == null) {
					if (Join.TYPE_INNER_JOIN.equalsIgnoreCase(relation.getJoinType()+" JOIN"))
						it.remove();
					continue;
				}
				for(Field fld:relationField.getFieldSet()){
					record.put(fld.getName(), ((CompositeMap) cachedRecord).get(fld.getSourceField()));
				}
			}
		}
	}

	private String generateLookupCacheKey(Relation relation, CompositeMap record){
		String recordKey = generateKey(getRelationCacheLocalFields(relation));
		String lookupCacheKey = relation.getReferenceModel()+"."+TextParser.parse(recordKey, record);
		return lookupCacheKey;
	}
	private String generateKey(Set<String> fieldNames) {
		if(fieldNames == null)
			return null;
		StringBuffer sb = new StringBuffer();
		for (String fieldName : fieldNames) {
			sb.append("${@" + fieldName.toLowerCase() + "}.");
		}
		String cacheKey = sb.substring(0, sb.length() - 1);
		return cacheKey;
	}
	private HashSet<String> getRelationCacheLocalFields(Relation relation){
		HashSet<String> localFieldList = new HashSet<String>();
		Reference[] refs = relation.getReferences();
		if (refs != null) {
			for (int i = 0; i < refs.length; i++) {
				Reference ref = refs[i];
				// not support expression
				String exp = ref.getExpression();
				if (exp != null)
					return null;
				localFieldList.add(ref.getLocalField().toLowerCase());
			}
		}
		return localFieldList;
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
