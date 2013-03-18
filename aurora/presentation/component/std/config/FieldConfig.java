package aurora.presentation.component.std.config;

public class FieldConfig extends ComponentConfig {
	
	public static final String VERSION = "$Revision$";
	
	public static final String PROPERTITY_REQUIRED = "required";
	public static final String PROPERTITY_READONLY = "readonly";
	public static final String PROPERTITY_MAX_LENGHT = "maxlength";
	public static final String PROPERTITY_RENDERER = "renderer";
	public static final String PROPERTITY_CHARA_TRANSFORM = "transformcharacter";
	
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
    
    public Integer getMaxLength(){
        return getInteger(PROPERTITY_MAX_LENGHT);
    }
    public void setMaxLength(Integer ml){
        putInt(PROPERTITY_MAX_LENGHT, ml);
    }
    
    public String getRenderer(){
        return getString(PROPERTITY_RENDERER);
    }
    public void setRenderer(String renderer){
        putString(PROPERTITY_RENDERER, renderer);
    }
    
    public boolean isTransformCharacter(){
        return getBoolean(PROPERTITY_CHARA_TRANSFORM, true);
    }
    public void setTransformCharacter(boolean tc){
        putBoolean(PROPERTITY_CHARA_TRANSFORM, tc);
    }
}
