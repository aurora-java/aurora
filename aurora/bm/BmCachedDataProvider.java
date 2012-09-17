/*
 * Created on 2012-9-14 下午04:24:31
 * $Id$
 */
package aurora.bm;

import java.util.Iterator;

import uncertain.cache.ICache;
import uncertain.cache.INamedCacheFactory;
import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.core.ILifeCycle;
import aurora.database.IResultSetConsumer;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.IDatabaseServiceFactory;

public class BmCachedDataProvider implements ICachedDataProvider, ILifeCycle {
    
    String  cacheKey;
    String  cacheName;
    String  cacheFactoryName;
    
    INamedCacheFactory      cacheFactory;
    IDatabaseServiceFactory dbServiceFactory;
    
    public BmCachedDataProvider(INamedCacheFactory factory, IDatabaseServiceFactory dbs){
        this.cacheFactory = factory;
        this.dbServiceFactory = dbs;
    }

    @Override
    public ICache getCachedData(String business_model_name) {
        return cacheFactory.getNamedCache(business_model_name);
    }

    @Override
    public INamedCacheFactory getCacheFactoryForData() {
        return cacheFactory;
    }
/*
    @Override
    public String getCacheKey(BusinessModel model, CompositeMap record) {
        String key = null;
        if(cacheKey==null)
            key = generateDefaultCacheKey(model,record);
        else
            key = TextParser.parse(cacheKey, record);
        return model.getName()+"." +key;
    }
    
    private String generateDefaultCacheKey(BusinessModel model, CompositeMap record){
        Field[] flds = model.getPrimaryKeyFields();
        if(flds==null || flds.length==0)
            throw new IllegalArgumentException("Business model "+model.getName()+" must has primary key");
        StringBuffer key = new StringBuffer();
        for(int i=0; i<flds.length; i++){
            if(i>0)key.append(".");
            Object value = record.get(flds[i].getName());
            if(value==null)
                throw new IllegalArgumentException("primary key field can not be null:"+record.toXML());
            key.append(value);
        }
        return key.toString();
    }
*/    
    private String generateLookupCacheKey(String foreign_bm_name, Field ref_field, CompositeMap record){
        Relation relation = null;
        String rname = ref_field.getRelationName();
        if(rname==null){
            for(Relation r:ref_field.getOwner().getRelations())
                if(r.getReferenceModel().equals( ref_field.getSourceModel())){
                    relation = r;
                    break;
                }
                        
        }else{
            relation = ref_field.getOwner().getRelation(rname);
        }
        
        StringBuffer key = new StringBuffer(foreign_bm_name+".");
        for(int i=0; i<relation.getReferences().length; i++){
            Reference ref = relation.getReferences()[i];
            if(i>0)key.append(".");
            Object value = record.get(ref.getLocalField());
            if(value==null) value = "";
            key.append(value);
        }
        
        return key.toString();
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
    
    public void onFetchResultSet(BusinessModel model, IResultSetConsumer rs){
        if(!model.hasCacheJoinFields())
            return;
        Object o = rs.getResult();
        if(!(o instanceof CompositeMap))
            return;
        CompositeMap data = (CompositeMap)o;
        Iterator it = data.getChildIterator();
        if(it==null)
            return;
        if(model.getFields()==null)
            return;
        while(it.hasNext()){
            CompositeMap record = (CompositeMap)it.next();
            for(Field f:model.getFields()){
                if(!f.isCacheJoinField())
                    continue;
                BusinessModel bm = f.getRefferedModel();
                ICache cache = getCachedData(bm.getName());
                if(cache==null)
                    throw new IllegalArgumentException(bm.getName()+ "has no cached data");
                String lookup_key = generateLookupCacheKey(bm.getName(),f,record);
                CompositeMap master_data = (CompositeMap)cache.getValue(lookup_key);
                if(master_data!=null){
                    record.put(f.getName(), master_data.get(f.getSourceField()));
                }
            }
            System.out.println();
        }
    }

}
