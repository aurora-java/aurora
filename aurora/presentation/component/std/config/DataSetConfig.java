package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

/**
 * 
 * @version $Id$
 * @author <a href="mailto:njq.niu@hand-china.com">vincent</a>
 */
public class DataSetConfig extends ComponentConfig {

	public static final String VERSION = "$Revision$";
	
	public static final String TAG_NAME = "dataSet";
	
	public static final String PROPERTITY_AUTO_CREATE = "autocreate";
	public static final String PROPERTITY_AUTO_QUERY = "autoquery";
	public static final String PROPERTITY_AUTO_COUNT = "autocount";
	public static final String PROPERTITY_BINDNAME = "bindname";
	public static final String PROPERTITY_CAN_QUERY = "canquery";
	public static final String PROPERTITY_CAN_SUBMIT = "cansubmit";
	public static final String PROPERTITY_DATAS = "datas";
	public static final String PROPERTITY_DATA_HEAD = "datahead";
	public static final String PROPERTITY_DATASOURCE = "datasource";
	public static final String PROPERTITY_FETCHALL = "fetchall";
	public static final String PROPERTITY_FIELDS = "fields";
	public static final String PROPERTITY_LOOKUP_CODE = "lookupcode";	
	public static final String PROPERTITY_MAPPING = "mapping";
	public static final String PROPERTITY_MAP = "map";
	public static final String PROPERTITY_MODEL = "model";
	public static final String PROPERTITY_LOADDATA = "loaddata";
	public static final String PROPERTITY_MAX_PAGESIZE = "maxpagesize";
	public static final String PROPERTITY_PAGESIZE = "pagesize";
	public static final String PROPERTITY_PAGEID = "pageid";	
	public static final String PROPERTITY_QUERYURL = "queryurl";
	public static final String PROPERTITY_QUERYDATASET = "querydataset";
	public static final String PROPERTITY_SUBMITURL = "submiturl";
	public static final String PROPERTITY_SHOW_CHECKALL = "showcheckall";
	public static final String PROPERTITY_SELECTABLE = "selectable";
	public static final String PROPERTITY_SELECTION_MODEL = "selectionmodel";
	public static final String PROPERTITY_SELECT_FUNCTION = "selectfunction";
	public static final String PROPERTITY_VALID_LISTENER = "validlistener";
	public static final String PROPERTITY_AUTO_PAGE_SIZE = "autopagesize";
	public static final String PROPERTITY_PROCESS_FUNCTION = "processfunction";
    public static final String PROPERTITY_TOTALCOUNT_FIELD = "totalcountfield";
	public static final String PROPERTITY_SORT_TYPE = "sorttype";
	public static final String PROPERTITY_NOTIFICATION = "notification";
	public static final String PROPERTITY_MODIFIED_CHECK = "modifiedcheck";
	
	//Hybris
	public static final String PROPERTITY_DTO_NAME = "dtoname";
	public static final String PROPERTITY_HYBRIS_KEY = "hybriskey";

	public static final String DEFAULT_SELECTION_MODEL = "multiple";
	public static final int DEFAULT_MAX_PAGE_SIZE = 1000;
	public static final int DEFAULT_PAGE_SIZE = 10;
	
	
	public String getSortType(){
		return getString(PROPERTITY_SORT_TYPE);
	}
	public void setSortType(String type){
		putString(PROPERTITY_SORT_TYPE,type);
	}
	
	public String getNotification(){
		return getString(PROPERTITY_NOTIFICATION,null);
	}
	public void setNotification(String notification){
		putString(PROPERTITY_NOTIFICATION,notification);
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
	
	public boolean getLoadData(){
		return getBoolean(PROPERTITY_LOADDATA, false);
	}
	public void setLoadData(boolean loadData){
		putBoolean(PROPERTITY_LOADDATA, loadData);
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
	
	public String getQueryUrl(CompositeMap model){
		String str = getString(PROPERTITY_QUERYURL, "");		
		return uncertain.composite.TextParser.parse(str, model);
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
	
	public void setProcessFunction(String pd){
		putString(PROPERTITY_PROCESS_FUNCTION,pd);
	}
	public String getProcessFunction(){
		return getString(PROPERTITY_PROCESS_FUNCTION,"");
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
	public boolean isFetchAll(CompositeMap model){
		String fetchAllStr = uncertain.composite.TextParser.parse(getString(PROPERTITY_FETCHALL,"false"),model);
		return "true".equals(fetchAllStr);
	}
	
	public int getMaxPageSize(){
		return getInt(PROPERTITY_MAX_PAGESIZE, DEFAULT_MAX_PAGE_SIZE);		
	}
	public void setMaxPageSize(int size){
		putInt(PROPERTITY_MAX_PAGESIZE, size);
	}
	public int getPageSize(CompositeMap model){
		String pageSizeStr = uncertain.composite.TextParser.parse(getString(PROPERTITY_PAGESIZE,""),model);
		if("".equals(pageSizeStr)){
			return DEFAULT_PAGE_SIZE;
		}else {
			return Integer.parseInt(pageSizeStr);
		}
	}
	public void setPageSize(int size){
		putInt(PROPERTITY_PAGESIZE, size);
	}
	
	public void setAutoCount(Boolean autocount){
		putBoolean(PROPERTITY_AUTO_COUNT, autocount);
	}
	public Boolean isAutoCount(){
		return getBoolean(PROPERTITY_AUTO_COUNT);
	}
	
	public void setSelectable(boolean selectable){
		putBoolean(PROPERTITY_SELECTABLE, selectable);
	}
	public boolean isSelectable(){
		return getBoolean(PROPERTITY_SELECTABLE, false);
	}	
	
	public void setShowCheckAll(boolean showCheckAll){
		putBoolean(PROPERTITY_SHOW_CHECKALL, showCheckAll);
	}
	public boolean isShowCheckAll(){
		return getBoolean(PROPERTITY_SHOW_CHECKALL, true);
	}
	
	public String getSelectionModel(){
		return getString(PROPERTITY_SELECTION_MODEL, DEFAULT_SELECTION_MODEL);		
	}
	public void setSelectionModel(String model){
		putString(PROPERTITY_SELECTION_MODEL, model);
	}
	public String getSelectFunction(){
		return getString(PROPERTITY_SELECT_FUNCTION);		
	}
	public void setSelectFunction(String function){
		putString(PROPERTITY_SELECT_FUNCTION, function);
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
    public void setAutoPageSize(boolean autoPageSize){
		putBoolean(PROPERTITY_AUTO_PAGE_SIZE, autoPageSize);
	}
	public boolean isAutoPageSize(CompositeMap model){
		String autoPageSize = uncertain.composite.TextParser.parse(getString(PROPERTITY_AUTO_PAGE_SIZE,"false"),model);
		return "true".equals(autoPageSize);
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
	public void setModifiedCheck(boolean modifiedCheck){
		putBoolean(PROPERTITY_MODIFIED_CHECK, modifiedCheck);
	}
	public boolean isModifiedCheck(){
		return getBoolean(PROPERTITY_MODIFIED_CHECK, true);
	}
	public boolean isModifiedCheck(boolean defaultValue){
		return getBoolean(PROPERTITY_MODIFIED_CHECK, defaultValue);
	}
    
    public String getTotalCountField(){
    	return getString(PROPERTITY_TOTALCOUNT_FIELD,"totalCount");
    }
    public void setTotalCountField(String value){
    	putString(PROPERTITY_TOTALCOUNT_FIELD, value);
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

	//Hybris
	public String getDtoName(){
		return getString(PROPERTITY_DTO_NAME, "");
	}

	public void setDtoName(final String value){
		putString(PROPERTITY_DTO_NAME, value);
	}
	public String getHybrisKey(){
		return getString(PROPERTITY_HYBRIS_KEY, "");
	}

	public void setHybrisKey(final String value){
		putString(PROPERTITY_HYBRIS_KEY, value);
	}
}
