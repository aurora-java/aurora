package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class RightBarConfig extends SideBarConfig {
	
	public static final String VERSION = "$Revision$";
	
	public static final String TAG_NAME = "rightBar";
	
	public static final String RIGHT_BAR = "right";
	
	public static RightBarConfig getInstance(){
		RightBarConfig model = new RightBarConfig();
        model.initialize(RightBarConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static RightBarConfig getInstance(CompositeMap context){
		RightBarConfig model = new RightBarConfig();
        model.initialize(RightBarConfig.createContext(context,TAG_NAME));
        return model;
    }
}
