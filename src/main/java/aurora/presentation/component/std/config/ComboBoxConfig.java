package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;


public class ComboBoxConfig extends InputFieldConfig {

	public static final String VERSION = "$Revision$";
	
	public static final String TAG_NAME = "comboBox";
	public static final String PROPERTITY_POPWIDTH = "popwidth";
	public static final String PROPERTITY_RENDERER = "displayrenderer";
	public static final String PROPERTITY_FETCH_RECORD = "fetchrecord";
	public static final String PROPERTITY_BLANK_OPTION = "blankoption";
	
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
	public boolean isBlankOption(){
		return getBoolean(PROPERTITY_BLANK_OPTION,false);
	}
	public void setBlankOption(boolean blankopton){
		putBoolean(PROPERTITY_FETCH_RECORD, blankopton);
	}
	public boolean isTransformCharacter(){
        return getBoolean(PROPERTITY_CHARA_TRANSFORM, false);
    }
}
