/*
 * Created on 2012-9-14 下午04:24:31
 * $Id$
 */
package aurora.bm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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

public class BmCachedJoin implements ILifeCycle {
    
  
    IObjectRegistry      mRegistry;
    ICachedDataProvider      cacheDataProvider;
    IDatabaseServiceFactory dbServiceFactory;
    
    
    public BmCachedJoin(IObjectRegistry objectRegistry,ICachedDataProvider cacheDataProvider,IDatabaseServiceFactory dbs){
        this.mRegistry = objectRegistry;
        this.cacheDataProvider = cacheDataProvider;
        this.dbServiceFactory = dbs;
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
		HashMap<String,CompositeMap> selectedCache = new HashMap<String,CompositeMap>();
		for (@SuppressWarnings("unchecked")
		Iterator<CompositeMap> it = data.getChildIterator(); it.hasNext();) {
			CompositeMap record = it.next();
			for (RelationFields  relationField: cacheJoinList) {
				relation = relationField.getRelation();
				lookup_key = generateLookupCacheKey(relation,record);
				cache = nameCacheFactory.getNamedCache(relation.getReferenceModel());
				if(cache == null)
						throw CacheBuiltinExceptionFactory.createNamedCacheNotFound(null, relation.getReferenceModel());
				cachedRecord = getCacheValue(cache,selectedCache,lookup_key);
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
		selectedCache.clear();
	}
    private CompositeMap getCacheValue(ICache cache,HashMap<String,CompositeMap> selectedCache,String lookup_key){
    	int max_selectedCache_size = 10000;
    	CompositeMap cachedRecord = null;
    	cachedRecord = selectedCache.get(lookup_key);
    	if(cachedRecord != null){
    		if(cachedRecord.size() == 0)
    			return null;
    		else
    			return cachedRecord;
    	}
    	cachedRecord = (CompositeMap)cache.getValue(lookup_key);
    	if(selectedCache.size() < max_selectedCache_size){
	    	if (cachedRecord != null) {
	    		selectedCache.put(lookup_key, cachedRecord);
	    	}else{
	    		selectedCache.put(lookup_key, new CompositeMap());
	    	}
    	}
    	return cachedRecord;
    }

	private String generateLookupCacheKey(Relation relation, CompositeMap record){
		String recordKey = cacheDataProvider.generateKey(getRelationCacheLocalFields(relation));
		String lookupCacheKey = relation.getReferenceModel()+"."+TextParser.parse(recordKey, record);
		return lookupCacheKey;
	}

	private List<String> getRelationCacheLocalFields(Relation relation){
		List<String> localFieldList = new LinkedList<String>();
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
}
