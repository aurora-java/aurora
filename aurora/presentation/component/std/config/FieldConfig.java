package aurora.presentation.component.std.config;

public class FieldConfig extends ComponentConfig {
	
	public static final String VERSION = "$Revision$";
	
	public static final String PROPERTITY_REQUIRED = "required";
	public static final String PROPERTITY_READONLY = "readonly";
	public static final String PROPERTITY_RENDERER = "renderer";
	
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
