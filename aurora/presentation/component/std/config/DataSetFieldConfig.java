package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;

public class DataSetFieldConfig extends DynamicObject  {
	
	public static final String TAG_NAME = "field";
	
	public static final String PROPERTITY_REQUIRED = "required";
	public static final String PROPERTITY_READONLY = "readonly";
    public static final String PROPERTITY_PROMPT = "prompt";
    
    
    public static CompositeMap createContext(CompositeMap map,String tagName) {
		CompositeMap context = new CompositeMap(tagName);
		if(map != null){
			context.copy(map);
		}
		return context;		
	}
	
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
    
    public boolean getReadOnly(){
    	return getBoolean(PROPERTITY_READONLY, false);
    }
    public void setReadOnly(boolean readonly){
    	putBoolean(PROPERTITY_READONLY, readonly);
    }
    
    
    public String getPrompt(){
    	return getString(PROPERTITY_PROMPT);
    }
    public void setPrompt(String prompt){
    	putString(PROPERTITY_PROMPT, prompt);
    }
}
