package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class LabelConfig extends ComponentConfig {
	
	public static final String VERSION = "$Revision$";
	public static final String TAG_NAME = "label";
	public static final String PROPERTITY_RENDERER = "renderer";
	
	public static LabelConfig getInstance(){
		LabelConfig model = new LabelConfig();
        model.initialize(LabelConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static LabelConfig getInstance(CompositeMap context){
		LabelConfig model = new LabelConfig();
        model.initialize(LabelConfig.createContext(context,TAG_NAME));
        return model;
    }
	
	public String getRenderer(){
        return getString(PROPERTITY_RENDERER, "");
    }
    public void setRenderer(String renderer){
        putString(PROPERTITY_RENDERER, renderer);
    }
}
