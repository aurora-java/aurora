package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class ToolBarButtonConfig extends ButtonConfig {
	
	public static final String VERSION = "$Revision$";
	public static final String TAG_NAME = "toolbarButton";
	
	
	public static ToolBarButtonConfig getInstance(){
		ToolBarButtonConfig model = new ToolBarButtonConfig();
        model.initialize(ToolBarButtonConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static ToolBarButtonConfig getInstance(CompositeMap context){
		ToolBarButtonConfig model = new ToolBarButtonConfig();
        model.initialize(ToolBarButtonConfig.createContext(context,TAG_NAME));
        return model;
    }
}
