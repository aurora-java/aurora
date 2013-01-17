package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class HTMLIncludeConfig extends ComponentConfig {
	
	public static final String VERSION = "$Revision";
	public static final String TAG_NAME = "imageCode";
	
	public static final String PROPERTITY_PATH_FIELD = "pathfield";
	public static final String PROPERTITY_MODEL = "model";
	public static final String PROPERTITY_PARAMS = "params";
	public static final String PROPERTITY_PATH = "path";
	public static final String PROPERTITY_VERSION = "version";
	
	
	public static HTMLIncludeConfig getInstance(){
		HTMLIncludeConfig model = new HTMLIncludeConfig();
        model.initialize(HTMLIncludeConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static HTMLIncludeConfig getInstance(CompositeMap context){
		HTMLIncludeConfig model = new HTMLIncludeConfig();
        model.initialize(HTMLIncludeConfig.createContext(context,TAG_NAME));
        return model;
    }
	
	public String getPath(){
		return getString(PROPERTITY_PATH);
	}
	
	public void setPath(String path){
		putString(PROPERTITY_PATH, path);
	}
	
	public String getVersion(){
		return getString(PROPERTITY_VERSION);
	}
	
	public void setVersion(String version){
		putString(PROPERTITY_VERSION, version);
	}
	
	public String getPathField(){
		return getString(PROPERTITY_PATH_FIELD);
	}
	
	public void setPathField(String pathField){
		putString(PROPERTITY_PATH_FIELD, pathField);
	}
	
	public String getModel(){
		return getString(PROPERTITY_MODEL);
	}
	
	public void setModel(String model){
		putString(PROPERTITY_MODEL, model);
	}
	
	public CompositeMap getParams(){
    	return getObjectContext().getChild(PROPERTITY_PARAMS);  
	}
}
