package aurora.presentation.component.std.config;

import aurora.application.AuroraApplication;
import uncertain.composite.CompositeMap;

public class DataSetFieldConfig extends ComponentConfig  {

	public static final String VERSION = "$Revision$";
	
	public static final String TAG_NAME = "field";
	
	public static final String PROPERTITY_REQUIRED = "required";
	public static final String PROPERTITY_REQUIRED_MESSAGE = "requiredMessage";
	public static final String PROPERTITY_READONLY = "readonly";
	public static final String PROPERTITY_EDITABLE = "editable";
    public static final String PROPERTITY_PROMPT = "prompt";
    public static final String PROPERTITY_RETURN_FIELD = "returnfield";
    public static final String PROPERTITY_VALIDATOR = "validator";
    public static final String PROPERTITY_DEFAULTVALUE = "defaultvalue";   
    public static final String PROPERTITY_TOOLTIP = "tooltip";
    public static final String PROPERTITY_AUTO_COMPLETE = "autocomplete";
    public static final String PROPERTITY_AUTO_COMPLETE_FIELD = "autocompletefield";    
	public static final String PROPERTITY_VALUE_FIELD = "valuefield";
	public static final String PROPERTITY_DISPLAY_FIELD = "displayfield";
	public static final String PROPERTITY_OPTIONS = "options";
	
	public static final String PROPERTITY_TITLE = "title";
	public static final String PROPERTITY_LOV_URL = "lovurl";
	public static final String PROPERTITY_LOV_MODEL = "lovmodel";
	public static final String PROPERTITY_LOV_SERVICE = "lovservice";
	public static final String PROPERTITY_LOV_WIDTH = "lovwidth";
	public static final String PROPERTITY_LOV_AUTO_QUERY = "lovautoquery";
	public static final String PROPERTITY_LOV_LABEL_WIDTH = "lovlabelwidth";
	public static final String PROPERTITY_LOV_HEIGHT = "lovheight";
	public static final String PROPERTITY_LOV_GRID_HEIGHT = "lovgridheight";
	public static final String PROPERTITY_FETCH_REMOTE = "fetchremote";
	public static final String PROPERTITY_AUTOCOMPLETE_RENDERER = "autocompleterenderer";
	public static final String PROPERTITY_FETCH_SINGLE = "fetchsingle";
	public static final String PROPERTITY_LOV_PAGESIZE = "lovpagesize";
	public static final String PROPERTITY_DATA_TYPE = "datatype";
	public static final String PROPERTITY_FUZZY_FETCH = "fuzzyfetch";
	
	
	
	
	private String DEFAULT_VALUE_FIELD = "code";
	private String DEFAULT_DISPLAY_FIELD = "name";
	
	public static DataSetFieldConfig getInstance(){
		DataSetFieldConfig model = new DataSetFieldConfig();
        model.initialize(DataSetFieldConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static DataSetFieldConfig getInstance(CompositeMap context){
		DataSetFieldConfig model = new DataSetFieldConfig();
        model.initialize(DataSetFieldConfig.createContext(context,TAG_NAME));
        return model;
    }
    
    public boolean getRequired(){
    	return getBoolean(PROPERTITY_REQUIRED, false);
    }
    public void setRequired(boolean required){
    	putBoolean(PROPERTITY_REQUIRED, required);
    }
    public String getRequiredMessage(){
    	return getString(PROPERTITY_REQUIRED_MESSAGE);
    }
    public void setRequiredMessage(String requiredMessage){
    	putString(PROPERTITY_REQUIRED_MESSAGE, requiredMessage);
    }
    
    public boolean getReadOnly(){
    	return getBoolean(PROPERTITY_READONLY, false);
    }
    public void setReadOnly(boolean readonly){
    	putBoolean(PROPERTITY_READONLY, readonly);
    }
    public boolean getEditable(){
    	return getBoolean(PROPERTITY_EDITABLE, true);
    }
    public void setEditable(boolean editable){
    	putBoolean(PROPERTITY_EDITABLE, editable);
    }
    public boolean getAutoComplete(){
    	return getBoolean(PROPERTITY_AUTO_COMPLETE, false);
    }
    public void setAutoComplete(boolean autocomplete){
    	putBoolean(PROPERTITY_AUTO_COMPLETE, autocomplete);
    }
    public void setAutoCompleteField(String autocompletefield){
    	putString(PROPERTITY_AUTO_COMPLETE_FIELD, autocompletefield);
    }
    
    public String getAutoCompleteField(){
    	return getString(PROPERTITY_AUTO_COMPLETE_FIELD);
    }
    
    public String getPrompt(){
    	return getString(PROPERTITY_PROMPT);
    }
    public void setPrompt(String prompt){
    	putString(PROPERTITY_PROMPT, prompt);
    }
    
    public String getValidator(){
    	return getString(PROPERTITY_VALIDATOR, "");
    }
    public void setValidator(String validator){
    	putString(PROPERTITY_VALIDATOR, validator);
    }
    
    public String getReturnField(){
    	return getString(PROPERTITY_RETURN_FIELD);
    }
    public void setReturnField(String field){
    	putString(PROPERTITY_RETURN_FIELD, field);
    }
    
	public String getOptions(){
		return getString(PROPERTITY_OPTIONS);
	}
	public void setOptions(String options){
		putString(PROPERTITY_OPTIONS, options);
	}
	
	public String getValueField(){
		return getString(PROPERTITY_VALUE_FIELD,DEFAULT_VALUE_FIELD);		
	}
	public void setValueField(String field){
		putString(PROPERTITY_VALUE_FIELD, field);
	}
	
	public String getDisplayField(){
		return getString(PROPERTITY_DISPLAY_FIELD,DEFAULT_DISPLAY_FIELD);		
	}
	public void setDisplayField(String field){
		putString(PROPERTITY_DISPLAY_FIELD, field);
	}
    
    public String getDefaultValue(){
    	return getString(PROPERTITY_DEFAULTVALUE);
    }
    public void setDefaultValue(String value){
    	putString(PROPERTITY_DEFAULTVALUE, value);
    }
    
    public void setTooltip(String tooltip){
    	putString(PROPERTITY_TOOLTIP, tooltip);
    }
    
    public String getTooltip(){
    	return getString(PROPERTITY_TOOLTIP);
    }
    
    public void addMap(CompositeMap map){
    	CompositeMap context = getObjectContext();
    	CompositeMap mapping = context.getChild(DataSetConfig.PROPERTITY_MAPPING);
    	if(mapping == null){
    		mapping = new CompositeMap("mapping");
    		mapping.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
    		context.addChild(mapping);
    	}
    	mapping.addChild(map);
    }
    
    public CompositeMap getMapping(){
    	CompositeMap context = getObjectContext();
    	return context.getChild(DataSetConfig.PROPERTITY_MAPPING);
    }
    
    
	public String getTitle() {
		return getString(PROPERTITY_TITLE, "");
	}
	
	public void setTitle(String title) {
		putString(PROPERTITY_TITLE, title);
	}
	
	public String getLovUrl() {
		return getString(PROPERTITY_LOV_URL, "");
	}
	
	public void setLovUrl(String lovUrl) {
		putString(PROPERTITY_LOV_URL, lovUrl);
	}
	
	public String getLovModel() {
		return getString(PROPERTITY_LOV_MODEL, "");
	}
	
	public void setLovModel(String lovModel) {
		putString(PROPERTITY_LOV_MODEL, lovModel);
	}
	
	public String getLovService() {
		return getString(PROPERTITY_LOV_SERVICE, "");
	}
	
	public void setLovService(String lovService) {
		putString(PROPERTITY_LOV_SERVICE, lovService);
	}
	
	public int getLovWidth() {
		return getInt(PROPERTITY_LOV_WIDTH, 400);
	}
	
	public void setLovWidth(int width) {
		putInt(PROPERTITY_LOV_WIDTH, width);
	}
	
	public boolean getLovAutoQuery() {
		return getBoolean(PROPERTITY_LOV_AUTO_QUERY, true);
	}
	
	public void setLovAutoQuery(boolean lovAutoQuery) {
		putBoolean(PROPERTITY_LOV_AUTO_QUERY, lovAutoQuery);
	}
	
	public int getLovLabelWidth() {
		return getInt(PROPERTITY_LOV_LABEL_WIDTH, 75);
	}
	
	public void setLovLabelWidth(int lovLabelWidth) {
		putInt(PROPERTITY_LOV_LABEL_WIDTH, lovLabelWidth);
	}
	
	public int getLovHeight() {
		return getInt(PROPERTITY_LOV_HEIGHT, 400);
	}
	
	public void setLovHeight(int height) {
		putInt(PROPERTITY_LOV_HEIGHT, height);
	}
	
	public int getLovGridHeight() {
		return getInt(PROPERTITY_LOV_GRID_HEIGHT, 350);
	}
	
	public void setLovGridHeight(int lovGridHeight) {
		putInt(PROPERTITY_LOV_GRID_HEIGHT, lovGridHeight);
	}
	
	public boolean getFetchRemote() {
		return getBoolean(PROPERTITY_FETCH_REMOTE, true);
	}
	
	public void setFetchRemote(boolean fetchRemote) {
		putBoolean(PROPERTITY_FETCH_REMOTE, fetchRemote);
	}
	
	public String getAutocompleteRenderer() {
		return getString(PROPERTITY_AUTOCOMPLETE_RENDERER);
	}
	
	
	public void setAutocompleteRenderer(String renderer) {
		putString(PROPERTITY_AUTOCOMPLETE_RENDERER, renderer);
	}
	
	public void setDataType(String type) {
		putString(PROPERTITY_DATA_TYPE, type);
	}
	
	public String getDataType() {
		return getString(PROPERTITY_DATA_TYPE);
	}
	
	public boolean getFetchSingle() {
		return getBoolean(PROPERTITY_FETCH_SINGLE, false);
	}
	public void setFetchSingle(boolean fetchSingle) {
		putBoolean(PROPERTITY_FETCH_SINGLE, fetchSingle);
	}
	public Boolean getFuzzyFetch() {
		return getBoolean(PROPERTITY_FUZZY_FETCH);
	}
	public void setFuzzyFetch(boolean fuzzyFetch) {
		putBoolean(PROPERTITY_FUZZY_FETCH, fuzzyFetch);
	}
	
	public int getLovPageSize() {
		return getInt(PROPERTITY_LOV_PAGESIZE, 10);
	}
	
	public void setLovPageSize(int size) {
		putInt(PROPERTITY_LOV_PAGESIZE, size);
	}

}
