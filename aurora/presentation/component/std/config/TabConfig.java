package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class TabConfig extends ComponentConfig {
	
	public static final String VERSION = "$Revision$";
	
	public static final String TAG_NAME = "tab";

	public static final String PROPERTITY_TAB = "tab";
	public static final String PROPERTITY_TAB_CLASS = "tabclassname";
	public static final String PROPERTITY_TAB_STYLE = "tabstyle";
	public static final String PROPERTITY_BODY_CLASS = "bodyclassname";
	public static final String PROPERTITY_BODY_STYLE = "bodystyle";
	public static final String PROPERTITY_REF = "ref";
	public static final String PROPERTITY_SELECTED = "selected";
	public static final String PROPERTITY_CLOSEABLE = "closeable";
	public static final String PROPERTITY_DISABLED = "disabled";
	

	public static TabConfig getInstance() {
		TabConfig model = new TabConfig();
		model.initialize(GridConfig.createContext(null, TAG_NAME));
		return model;
	}

	public static TabConfig getInstance(CompositeMap context) {
		TabConfig model = new TabConfig();
		model.initialize(GridConfig.createContext(context, TAG_NAME));
		return model;
	}

	public String getRef(){
		return getString(PROPERTITY_REF, "");
	}
	
	public void setRef(String ref){
		putString(PROPERTITY_REF, ref);
	}
	
	public String getBodyClass(){
		return getString(PROPERTITY_BODY_CLASS, "");
	}
	
	public void setBodyClass(String bodyClass){
		putString(PROPERTITY_BODY_CLASS, bodyClass);
	}
	
	public String getBodyStyle(){
		return getString(PROPERTITY_BODY_STYLE, "");
	}
	
	public void setBodyStyle(String bodyStyle){
		putString(PROPERTITY_BODY_STYLE, bodyStyle);
	}
	
	public String getTabClass(){
		return getString(PROPERTITY_TAB_CLASS, "");
	}
	
	public void setTabClass(String bodyClass){
		putString(PROPERTITY_TAB_CLASS, bodyClass);
	}
	
	public String getTabStyle(){
		return getString(PROPERTITY_TAB_STYLE, "");
	}
	
	public void setTabStyle(String tabStyle){
		putString(PROPERTITY_TAB_STYLE, tabStyle);
	}
	
	public boolean isSelected(){
		return getBoolean(PROPERTITY_SELECTED, false);
	}
	
	public void setSelected(String selected){
		putString(PROPERTITY_SELECTED, selected);
	}
	
	public boolean isCloseable(){
		return getBoolean(PROPERTITY_CLOSEABLE, false);
	}
	
	public void setCloseable(String closeable){
		putString(PROPERTITY_CLOSEABLE, closeable);
	}
	
	public boolean isDisabled(){
		return getBoolean(PROPERTITY_DISABLED, false);
	}
	
	public void setDisabled(String disabled){
		putString(PROPERTITY_DISABLED, disabled);
	}
	
}
