package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class LinkConfig extends ComponentConfig {
	public static final String VERSION = "$Revision$";
	public static final String TAG_NAME = "link";
	public static final String PROPERTITY_URL = "url";
	public static final String PROPERTITY_MODEL = "model";
	public static final String PROPERTITY_MODEL_ACTION = "modelaction";
	
	public static LinkConfig getInstance(){
		LinkConfig model = new LinkConfig();
        model.initialize(LinkConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static LinkConfig getInstance(CompositeMap context){
		LinkConfig model = new LinkConfig();
        model.initialize(LinkConfig.createContext(context,TAG_NAME));
        return model;
    }
	
	public String getUrl(){
        return getString(PROPERTITY_URL);
    }
    public void setUrl(String url){
        putString(PROPERTITY_URL, url);
    }
    
    public String getModel(){
        return getString(PROPERTITY_MODEL);
    }
    public void setModel(String m){
        putString(PROPERTITY_MODEL, m);
    }
    
    public String getModelAction(){
        return getString(PROPERTITY_MODEL_ACTION);
    }
    public void setModelAction(String ma){
        putString(PROPERTITY_MODEL_ACTION, ma);
    }
}
