package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;


public class ComboBoxConfig extends InputFieldConfig {

	public static final String VERSION = "$Revision$";
	
	public static final String TAG_NAME = "comboBox";
	public static final String PROPERTITY_POPWIDTH = "popwidth";
	public static final String PROPERTITY_VALUE_FIELD = "valuefield";
	public static final String PROPERTITY_DISPLAY_FIELD = "displayfield";
	public static final String PROPERTITY_OPTIONS = "options";
	public static final String PROPERTITY_RENDERER = "displayrenderer";
	public static final String PROPERTITY_FETCH_RECORD = "fetchrecord";
	
	private String DEFAULT_VALUE_FIELD = "code";
	private String DEFAULT_DISPLAY_FIELD = "name";
	
	public static ComboBoxConfig getInstance(){
		ComboBoxConfig model = new ComboBoxConfig();
        model.initialize(ComboBoxConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static ComboBoxConfig getInstance(CompositeMap context){
		ComboBoxConfig model = new ComboBoxConfig();
        model.initialize(ComboBoxConfig.createContext(context,TAG_NAME));
        return model;
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
	
	public String getRenderer(){
		return getString(PROPERTITY_RENDERER);
	}
	public void setRenderer(String renderer){
		putString(PROPERTITY_RENDERER, renderer);
	}
	public boolean isFetchRecord(){
		return getBoolean(PROPERTITY_FETCH_RECORD,true);
	}
	public void setFetchRecord(boolean fetchrecord){
		putBoolean(PROPERTITY_FETCH_RECORD, fetchrecord);
	}
	
}
