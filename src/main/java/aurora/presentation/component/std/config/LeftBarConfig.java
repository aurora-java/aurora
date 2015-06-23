package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class LeftBarConfig extends SideBarConfig {
	
	public static final String VERSION = "$Revision$";
	
	public static final String TAG_NAME = "leftBar";
	
	public static final String LEFT_BAR = "left";
	
	public static LeftBarConfig getInstance(){
		LeftBarConfig model = new LeftBarConfig();
        model.initialize(LeftBarConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static LeftBarConfig getInstance(CompositeMap context){
		LeftBarConfig model = new LeftBarConfig();
        model.initialize(LeftBarConfig.createContext(context,TAG_NAME));
        return model;
    }
}
