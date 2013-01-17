package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;


public class TextAreaConfig extends ComponentConfig {

	public static final String VERSION = "$Revision$";
	
	public static final String TAG_NAME = "textArea";
	public static final String PROPERTITY_READONLY = "readonly";

	
	public static TextAreaConfig getInstance(){
		TextAreaConfig model = new TextAreaConfig();
		CompositeMap context = TextAreaConfig.createContext(null,TAG_NAME);
        model.initialize(context);
        return model;
    }
	
	public static TextAreaConfig getInstance(CompositeMap context){
		TextAreaConfig model = new TextAreaConfig();
		CompositeMap map = TextAreaConfig.createContext(context,TAG_NAME);
        model.initialize(map);
        return model;
    }
	
	public boolean isReadOnly(){
        return getBoolean(PROPERTITY_READONLY,false);
    }
    public void setReadOnly(boolean readonly){
        putBoolean(PROPERTITY_READONLY, readonly);
    }
}
