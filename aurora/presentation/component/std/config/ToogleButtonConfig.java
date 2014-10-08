package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class ToogleButtonConfig extends FieldConfig {
	
	public static final String TAG_NAME = "toolgeButton";
	public static final String PROPERTITY_TOOGLED = "toogled";
	public static final String PROPERTITY_CLICK = "click";
	public static final String PROPERTITY_TOOGLE_ID = "toogleid";
	
	public static ToogleButtonConfig getInstance(){
		ToogleButtonConfig model = new ToogleButtonConfig();
        model.initialize(ToogleButtonConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static ToogleButtonConfig getInstance(CompositeMap context){
		ToogleButtonConfig model = new ToogleButtonConfig();
        model.initialize(ToogleButtonConfig.createContext(context,TAG_NAME));
        return model;
    }
	
    public Boolean isToolged(){
        return getBoolean(PROPERTITY_TOOGLED, false);
    }
    public void setToolged(Boolean toogled){
        putBoolean(PROPERTITY_TOOGLED, toogled);
    }
    
    public String getClick(){
        return getString(PROPERTITY_CLICK, "");
    }
    public void setClick(String click){
        putString(PROPERTITY_CLICK, click);
    }
    
    public String getToogleId(){
        return getString(PROPERTITY_TOOGLE_ID);
    }
    public void setToogleId(String id){
        putString(PROPERTITY_TOOGLE_ID, id);
    }
}
