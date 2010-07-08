package aurora.application.features;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.core.IGlobalInstance;
import uncertain.ocm.IObjectRegistry;
import aurora.database.FetchDescriptor;
import aurora.database.service.BusinessModelService;
import aurora.database.service.DatabaseServiceFactory;

public class DefaultLookupCodeProvider implements ILookupCodeProvider ,IGlobalInstance{

	private String lookupType = "sql";

	private String lookupSql;
	
	private String lookupModel;
	
	private DatabaseServiceFactory factory;
	private IObjectRegistry registry;
	
	private boolean inited = false;
	
	private List cache = new ArrayList();
	
	public DefaultLookupCodeProvider(IObjectRegistry registry) {
		super();
		this.registry = registry;
	}
	
	public List getLookupList(CompositeMap session_context,String lookup_code) {
		List result = new ArrayList();
		Iterator it = cache.iterator();
		if(lookup_code!=null){
			while(it.hasNext()){
				CompositeMap record = (CompositeMap)it.next();
				String looupcode = record.getString("code");
				if(lookup_code.equals(looupcode)){
					result.add(record);
				}
			}
		}
		Collections.sort(result, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				CompositeMap r1 = (CompositeMap)arg0;
				CompositeMap r2 = (CompositeMap)arg1;
				Integer id1 = r1.getInt("code_value_id");
				Integer id2 = r2.getInt("code_value_id");
				return id1.compareTo(id2);
			}
		});
		return result;
	}

	public String getLookupPrompt(CompositeMap session_context,String lookup_code, Object lookup_value) {
		return null;
	}
	
	public void onInitialize() throws Exception {
		factory = (DatabaseServiceFactory)registry.getInstanceOfType(DatabaseServiceFactory.class);
		init();
	}
	
	public void invalid() throws Exception{		
		inited = false;
		cache = new ArrayList();
		init();
	}
	
	private void init() throws Exception{
		if(!inited){
			BusinessModelService service = factory.getModelService(getLookupModel());
			CompositeMap resultMap = service.queryAsMap(new HashMap(), FetchDescriptor.fetchAll());
			if(resultMap!=null){
				cache = resultMap.getChilds();
			}
			inited = true;
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

}
