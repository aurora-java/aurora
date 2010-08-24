package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

/**
 * 
 * @version $Id$
 * @author <a href="mailto:njq.niu@hand-china.com">vincent</a>
 */
public class DataSetConfig extends ComponentConfig {
	
	public static final String TAG_NAME = "dataSet";
	
	public static final String PROPERTITY_AUTO_CREATE = "autocreate";
	public static final String PROPERTITY_AUTO_QUERY = "autoquery";
	public static final String PROPERTITY_AUTO_COUNT = "autocount";
	public static final String PROPERTITY_BINDNAME = "bindname";
	public static final String PROPERTITY_CAN_QUERY = "canquery";
	public static final String PROPERTITY_CAN_SUBMIT = "cansubmit";
	public static final String PROPERTITY_DATAS = "datas";
	public static final String PROPERTITY_DATASOURCE = "datasource";
	public static final String PROPERTITY_FETCHALL = "fetchall";
	public static final String PROPERTITY_FIELDS = "fields";
	public static final String PROPERTITY_HREF = "href";
	public static final String PROPERTITY_LOOKUP_CODE = "lookupcode";	
	public static final String PROPERTITY_MAPPING = "mapping";
	public static final String PROPERTITY_MAP = "map";
	public static final String PROPERTITY_MODEL = "model";
	public static final String PROPERTITY_PAGESIZE = "pagesize";
	public static final String PROPERTITY_PAGEID = "pageid";	
	public static final String PROPERTITY_QUERYURL = "queryurl";
	public static final String PROPERTITY_QUERYDATASET = "querydataset";
	public static final String PROPERTITY_SUBMITURL = "submiturl";
	public static final String PROPERTITY_SELECTABLE = "selectable";
	public static final String PROPERTITY_SELECTION_MODEL = "selectionmodel";
	public static final String PROPERTITY_VALID_LISTENER = "validlistener";
	
	private static final String DEFAULT_SELECTION_MODEL = "multiple";
	private static final int DEFAULT_PAGE_SIZE = 10;
	
	
	public static CompositeMap createContext(CompositeMap map,String tagName) {
		CompositeMap context = new CompositeMap(tagName);
		if(map != null){
			context.copy(map);
		}
		return context;		
	}
	
	public static DataSetConfig getInstance(){
		DataSetConfig model = new DataSetConfig();
        model.initialize(DataSetConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static DataSetConfig getInstance(CompositeMap context){
		DataSetConfig model = new DataSetConfig();
        model.initialize(DataSetConfig.createContext(context,TAG_NAME));
        return model;
    }
	
	public String getHref(){
		return getString(PROPERTITY_HREF);		
	}
	public void setHref(String href){
		putString(PROPERTITY_HREF, href);
	}
	
	public void setAutoQuery(boolean autoQuery){
		putBoolean(PROPERTITY_AUTO_QUERY, autoQuery);
	}
	public boolean isAutoQuery(){
		return getBoolean(PROPERTITY_AUTO_QUERY, false);
	}
	
	public String getQueryUrl(){
		return getString(PROPERTITY_QUERYURL, "");		
	}
	public void setQueryUrl(String url){
		putString(PROPERTITY_QUERYURL, url);
	}
	
	public String getSubmitUrl(){
		return getString(PROPERTITY_SUBMITURL, "");		
	}
	public void setSubmitUrl(String url){
		putString(PROPERTITY_SUBMITURL, url);
	}
	
	public String getQueryDataSet(){
		return getString(PROPERTITY_QUERYDATASET, "");		
	}
	public void setQueryDataSet(String id){
		putString(PROPERTITY_QUERYDATASET, id);
	}
	
	public void setModel(String model){
		putString(PROPERTITY_MODEL, model);
	}
	
	public String getModel(){
		return getString(PROPERTITY_MODEL);
	}
	
	public void setLookupCode(String code){
		putString(PROPERTITY_LOOKUP_CODE, code);
	}
	
	public String getLookupCode(){
		return getString(PROPERTITY_LOOKUP_CODE);
	}
	
	public void setFetchAll(boolean fetchAll){
		putBoolean(PROPERTITY_FETCHALL, fetchAll);
	}
	public boolean isFetchAll(){
		return getBoolean(PROPERTITY_FETCHALL, false);
	}
	
	public int getPageSize(){
		return getInt(PROPERTITY_PAGESIZE, DEFAULT_PAGE_SIZE);		
	}
	public void setPageSize(int size){
		putInt(PROPERTITY_PAGESIZE, size);
	}
	
	public void setAutoCount(boolean autocount){
		putBoolean(PROPERTITY_AUTO_COUNT, autocount);
	}
	public boolean isAutoCount(){
		return getBoolean(PROPERTITY_AUTO_COUNT, true);
	}
	
	public void setSelectable(boolean selectable){
		putBoolean(PROPERTITY_SELECTABLE, selectable);
	}
	public boolean isSelectable(){
		return getBoolean(PROPERTITY_SELECTABLE, true);
	}	
	
	public String getSelectionModel(){
		return getString(PROPERTITY_SELECTION_MODEL, DEFAULT_SELECTION_MODEL);		
	}
	public void setSelectionModel(String model){
		putString(PROPERTITY_SELECTION_MODEL, model);
	}
	
    public String getBindName(){
        return getString(PROPERTITY_BINDNAME, "");
    }
    public void setBindName(String name){
        putString(PROPERTITY_BINDNAME, name);
    }
    
    public String getValidListener(){
    	return getString(PROPERTITY_VALID_LISTENER);
    }
    public void setValidListener(String listener){
    	putString(PROPERTITY_VALID_LISTENER, listener);
    }
    
    public void setCanQuery(boolean canquery){
		putBoolean(PROPERTITY_CAN_QUERY, canquery);
	}
	public boolean isCanQuery(){
		return getBoolean(PROPERTITY_CAN_QUERY, true);
	}
	
    public void setCanSubmit(boolean cansubmit){
		putBoolean(PROPERTITY_CAN_SUBMIT, cansubmit);
	}
	public boolean isCanSubmit(){
		return getBoolean(PROPERTITY_CAN_SUBMIT, true);
	}
	
    public void setAutoCreate(boolean autocreate){
		putBoolean(PROPERTITY_AUTO_CREATE, autocreate);
	}
	public boolean isAutoCreate(){
		return getBoolean(PROPERTITY_AUTO_CREATE, false);
	}
    
    public CompositeMap getDatas(){
    	CompositeMap context = getObjectContext();
    	return context.getChild(PROPERTITY_DATAS);    	
    }
	
	public CompositeMap getFields(){
		CompositeMap context = getObjectContext();
    	CompositeMap fields = context.getChild(PROPERTITY_FIELDS);
    	if(fields == null){
    		fields = new CompositeMap(PROPERTITY_FIELDS);
    		context.addChild(fields);
    	}
    	return fields;  
	}
	
	public void addField(DataSetFieldConfig field){
		CompositeMap columns = getFields();
		columns.addChild(field.getObjectContext());
	}
}
