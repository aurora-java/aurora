package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class DataSetConfig extends ComponentConfig {
	
	public static final String TAG_NAME = "dataSet";
	
	public static final String PROPERTITY_HREF = "href";
	public static final String PROPERTITY_DEFAULTVALUE = "defaultvalue";
	public static final String PROPERTITY_FIELDS = "fields";
	public static final String PROPERTITY_DATAS = "datas";
	public static final String PROPERTITY_DATASOURCE = "datasource";
	public static final String PROPERTITY_CREATERECORD = "autocreate";
	public static final String PROPERTITY_AUTOQUERY = "autoquery";
	public static final String PROPERTITY_QUERYURL = "queryurl";
	public static final String PROPERTITY_SUBMITURL = "submiturl";
	public static final String PROPERTITY_QUERYDATASET = "querydataset";
	public static final String PROPERTITY_FETCHALL = "fetchall";
	public static final String PROPERTITY_PAGESIZE = "pagesize";
	public static final String PROPERTITY_AUTOCOUNT = "autocount";
	public static final String PROPERTITY_PAGEID = "pageid";	
	public static final String PROPERTITY_MAPPING = "mapping";
	public static final String PROPERTITY_MAP = "map";
	public static final String PROPERTITY_SELECTABLE = "selectable";
	public static final String PROPERTITY_SELECTIONMODEL = "selectionmodel";
	
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
		putBoolean(PROPERTITY_AUTOQUERY, autoQuery);
	}
	public Boolean isAutoQuery(){
		return getBoolean(PROPERTITY_AUTOQUERY);
	}
	
	public String getQueryUrl(){
		return getString(PROPERTITY_QUERYURL);		
	}
	public void setQueryUrl(String url){
		putString(PROPERTITY_QUERYURL, url);
	}
	
	public String getSubmitUrl(){
		return getString(PROPERTITY_SUBMITURL);		
	}
	public void setSubmitUrl(String url){
		putString(PROPERTITY_SUBMITURL, url);
	}
	
	public String getQueryDataSet(){
		return getString(PROPERTITY_QUERYDATASET);		
	}
	public void setQueryDataSet(String id){
		putString(PROPERTITY_QUERYDATASET, id);
	}
	
	public void setFetchAll(boolean fetchAll){
		putBoolean(PROPERTITY_FETCHALL, fetchAll);
	}
	public Boolean isFetchAll(){
		return getBoolean(PROPERTITY_FETCHALL);
	}
	
	public int getPageSize(){
		return getInt(PROPERTITY_PAGESIZE,10);		
	}
	public void setPageSize(int size){
		putInt(PROPERTITY_PAGESIZE, size);
	}
	
	public void setAutoCount(boolean autocount){
		putBoolean(PROPERTITY_AUTOCOUNT, autocount);
	}
	public Boolean isAutoCount(){
		return getBoolean(PROPERTITY_AUTOCOUNT);
	}
	
	public void setSelectable(boolean selectable){
		putBoolean(PROPERTITY_SELECTABLE, selectable);
	}
	public Boolean isSelectable(){
		return getBoolean(PROPERTITY_SELECTABLE);
	}	
	
	public String getSelectionModel(){
		return getString(PROPERTITY_SELECTIONMODEL);		
	}
	public void setSelectionModel(String model){
		putString(PROPERTITY_SELECTIONMODEL, model);
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
