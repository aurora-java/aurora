package aurora.application.features;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.core.IGlobalInstance;
import uncertain.ocm.IObjectRegistry;
import aurora.database.FetchDescriptor;
import aurora.database.service.BusinessModelService;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.SqlServiceContext;
import aurora.service.ServiceThreadLocal;

/**
 * 
 * @version $Id: DefaultLookupCodeProvider.java v 1.0 2011-3-29 下午01:57:45 IBM Exp $
 * @author <a href="mailto:njq.niu@hand-china.com">vincent</a>
 * 
 */
@SuppressWarnings("unchecked")
public class DefaultLookupCodeProvider implements ILookupCodeProvider, IGlobalInstance {

    private String lookupType = "sql";

    private String lookupSql;

    private String lookupModel;
    
    private String sortField;
    
    private String needCache;

    private DatabaseServiceFactory factory;
    private IObjectRegistry registry;

    private boolean inited = false;

    private List cache = new ArrayList();
    private Map cacheMap = new HashMap();
    
    private static final String DEFAULT_SORT_FIELD = "code_value_id";
    
    public DefaultLookupCodeProvider(IObjectRegistry registry) {
        super();
        this.registry = registry;
    }

    public List getLookupList(String language, String lookup_code) throws Exception {
        if ("sql".equals(lookupType)) {
            return getListFromDataBase(language, lookup_code);
        } else {
            return getListFromCache(language, lookup_code);
        }
    }

    private List getListFromDataBase(String language, String lookup_code) throws Exception {
    	List result = null;
        result = (List)cacheMap.get(lookup_code);
        if(result != null) {
        	return result;
        }
        result = new ArrayList();
        
        CompositeMap context = ServiceThreadLocal.getCurrentThreadContext();
        if (context == null)
            throw new IllegalStateException("No service context set in ThreadLocal yet");

       
        BusinessModelService service = factory.getModelService(getLookupModel(), context);        
        Map map = new HashMap();
        map.put("code", lookup_code);
        map.put("language", language);
        CompositeMap resultMap = service.queryAsMap(map,FetchDescriptor.fetchAll());
        if (resultMap != null) {
            result = resultMap.getChilds();
            if (result != null)
                sorList(result);
        }
        if("true".equalsIgnoreCase(this.getNeedCache()))
        cacheMap.put(lookup_code, result);
        return result;
    }

    private List getListFromCache(String language, String lookup_code) {
        List result = new ArrayList();
        Iterator it = cache.iterator();

        if (lookup_code != null && language != null) {
            while (it.hasNext()) {
                CompositeMap record = (CompositeMap) it.next();
                String looupcode = record.getString("code");
                String lan = record.getString("language");
                if (lookup_code.equals(looupcode) && language.equals(lan)) {
                    result.add(record);
                }
            }
        }
        sorList(result);
        return result;
    }

    private void sorList(List result) {
        Collections.sort(result, new Comparator() {
            public int compare(Object arg0, Object arg1) {
                CompositeMap r1 = (CompositeMap) arg0;
                CompositeMap r2 = (CompositeMap) arg1;
                Integer id1 = r1.getInt(getSortField());
                Integer id2 = r2.getInt(getSortField());
                return id1.compareTo(id2);
            }
        });
    }

    public String getLookupPrompt(String language, String lookup_code,
            Object lookup_value) {
        return null;
    }

    public void onInitialize() throws Exception {
        factory = (DatabaseServiceFactory) registry.getInstanceOfType(DatabaseServiceFactory.class);
        init();
    }

    public void invalid() throws Exception {
        inited = false;
        cache = new ArrayList();
        init();
    }

    private void init() throws Exception {
        if (!"sql".equals(lookupType) && !inited) {
            SqlServiceContext context = factory.createContextWithConnection();
            try {
                BusinessModelService service = factory.getModelService(getLookupModel(), context.getObjectContext());
                CompositeMap resultMap = service.queryAsMap(new HashMap(),FetchDescriptor.fetchAll());
                if (resultMap != null) {
                    cache = resultMap.getChilds();
                }
                inited = true;
            } finally {
                if (context != null)
                    context.freeConnection();
            }
        }
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
		return sortField == null ? DEFAULT_SORT_FIELD : sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public String getNeedCache() {
		return needCache;
	}

	public void setNeedCache(String needCache) {
		this.needCache = needCache;
	}

}
