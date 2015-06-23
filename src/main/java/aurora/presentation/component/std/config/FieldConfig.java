package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class FieldConfig extends ComponentConfig {
	
	public static final String VERSION = "$Revision$";
	
	public static final String PROPERTITY_REQUIRED = "required";
	public static final String PROPERTITY_READONLY = "readonly";
	public static final String PROPERTITY_RENDERER = "renderer";
	
	public static FieldConfig getInstance(){
		FieldConfig model = new FieldConfig();
		CompositeMap context = TextFieldConfig.createContext(null,TAG_NAME);
        model.initialize(context);
        return model;
    }
	
	public static FieldConfig getInstance(CompositeMap context){
		FieldConfig model = new FieldConfig();
		CompositeMap map = TextFieldConfig.createContext(context,TAG_NAME);
        model.initialize(map);
        return model;
    }
	
    public boolean getRequired(){
        return getBoolean(PROPERTITY_REQUIRED, false);
    }
    public void setRequired(boolean required){
        putBoolean(PROPERTITY_REQUIRED, required);
    }
    
    public boolean getReadonly(){
        return getBoolean(PROPERTITY_READONLY, false);
    }
    public void setReadonly(boolean readonly){
        putBoolean(PROPERTITY_READONLY, readonly);
    }
    
    public String getRenderer(){
        return getString(PROPERTITY_RENDERER);
    }
    public void setRenderer(String renderer){
        putString(PROPERTITY_RENDERER, renderer);
    }
}
