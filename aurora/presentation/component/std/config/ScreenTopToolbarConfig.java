package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class ScreenTopToolbarConfig extends ComponentConfig {
	
	public static final String VERSION = "$Revision$";
	public static final String TAG_NAME = "screenTopToolbar";
	
	
	public static ScreenTopToolbarConfig getInstance(){
		ScreenTopToolbarConfig model = new ScreenTopToolbarConfig();
        model.initialize(ScreenTopToolbarConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static ScreenTopToolbarConfig getInstance(CompositeMap context){
		ScreenTopToolbarConfig model = new ScreenTopToolbarConfig();
        model.initialize(ScreenTopToolbarConfig.createContext(context,TAG_NAME));
        return model;
    }
}
