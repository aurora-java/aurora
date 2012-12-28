package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class TreeConfig extends ComponentConfig {

	public static final String VERSION = "$Revision$";
	
	public static final String TAG_NAME = "tree";
	
	public static final String PROPERTITY_RENDERER = "renderer";
	public static final String PROPERTITY_FIELD_ID = "idfield";
	public static final String PROPERTITY_SHOWCHECKBOX = "showcheckbox";
	public static final String PROPERTITY_FIELD_PARENT = "parentfield";
	public static final String PROPERTITY_FIELD_DISPLAY = "displayfield";
	public static final String PROPERTITY_FIELD_EXPAND = "expandfield";
	public static final String PROPERTITY_FIELD_CHECKED = "checkfield";
	public static final String PROPERTITY_FIELD_SEQUENCE = "sequencefield";
	public static final String PROPERTITY_FIELD_ICON = "iconfield";
	
	private static final String DEFAULT_ID_FIELD = "id";
	private static final String DEFAULT_PARENT_FIELD = "pid";
	private static final String DEFAULT_DISPLAY_FIELD = "name";
	private static final String DEFAULT_EXPAND_FIELD ="expanded";
	private static final String DEFAULT_SEQUENCE_FIELD = "sequence";
	private static final String DEFAULT_CHECKED_FIELD = "checked";
	private static final String DEFAULT_ICON_FIELD = "icon";
	
	
	public static TreeConfig getInstance(){
		TreeConfig model = new TreeConfig();
		CompositeMap context = TreeConfig.createContext(null,TAG_NAME);
        model.initialize(context);
        return model;
    }
	
	public static TreeConfig getInstance(CompositeMap context){
		TreeConfig model = new TreeConfig();
		CompositeMap map = TreeConfig.createContext(context,TAG_NAME);
        model.initialize(map);
        return model;
    }
	
	
    public String getRenderer(){
        return getString(PROPERTITY_RENDERER);
    }
    public void setRenderer(String renderer){
        putString(PROPERTITY_RENDERER, renderer);
    }
    
    public String getIdField(){
        return getString(PROPERTITY_FIELD_ID, DEFAULT_ID_FIELD);
    }
    public void setIdField(String idf){
        putString(PROPERTITY_FIELD_ID, idf);
    }
    
    public String getParentField(){
        return getString(PROPERTITY_FIELD_PARENT, DEFAULT_PARENT_FIELD);
    }
    public void setParentField(String pf){
        putString(PROPERTITY_FIELD_PARENT, pf);
    }
    
    public String getDisplayField(){
        return getString(PROPERTITY_FIELD_DISPLAY, DEFAULT_DISPLAY_FIELD);
    }
    public void setDisplayField(String pf){
        putString(PROPERTITY_FIELD_DISPLAY, pf);
    }
    
    public String getExpandField(){
        return getString(PROPERTITY_FIELD_EXPAND, DEFAULT_EXPAND_FIELD);
    }
    public void setExpandField(String pf){
        putString(PROPERTITY_FIELD_EXPAND, pf);
    }
    
    public String getSequenceField(){
        return getString(PROPERTITY_FIELD_SEQUENCE, DEFAULT_SEQUENCE_FIELD);
    }
    public void setSequenceField(String pf){
        putString(PROPERTITY_FIELD_SEQUENCE, pf);
    }
    
    public String getCheckField(){
        return getString(PROPERTITY_FIELD_CHECKED, DEFAULT_CHECKED_FIELD);
    }
    public void setCheckField(String pf){
        putString(PROPERTITY_FIELD_CHECKED, pf);
    }
    
    public String getIconField(){
        return getString(PROPERTITY_FIELD_ICON, DEFAULT_ICON_FIELD);
    }
    public void setIconField(String pf){
        putString(PROPERTITY_FIELD_ICON, pf);
    }
    
    public boolean isShowCheckBox(){
    	return getBoolean(PROPERTITY_SHOWCHECKBOX, false);
    }
    public void setShowCheckBox(boolean showcheck){
    	putBoolean(PROPERTITY_SHOWCHECKBOX, showcheck);
    }
    
    
}
