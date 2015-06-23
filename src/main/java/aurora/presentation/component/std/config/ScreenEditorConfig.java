package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;


public class ScreenEditorConfig extends ComponentConfig {

	public static final String VERSION = "$Revision:$";
	
	public static final String TAG_NAME = "screenEditor";
	public static final String PROPERTITY_SCREEN_RESOLUTION = "screenresolution";
	
	public static final String DEFAULT_SCREEN_RESOLUTION = "1024*768";
	
	public static ScreenEditorConfig getInstance(){
		ScreenEditorConfig model = new ScreenEditorConfig();
        model.initialize(ScreenEditorConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static ScreenEditorConfig getInstance(CompositeMap context){
		ScreenEditorConfig model = new ScreenEditorConfig();
        model.initialize(ScreenEditorConfig.createContext(context,TAG_NAME));
        return model;
    }
	
	public String getScreenResolution(){
		return getString(PROPERTITY_SCREEN_RESOLUTION,DEFAULT_SCREEN_RESOLUTION);
	}
	public void setScreenResolution(String screenResolution){
		putString(PROPERTITY_SCREEN_RESOLUTION, screenResolution);
	}
}
