package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class SandBoxConfig extends ComponentConfig {
	
	public static final String VERSION = "$Revision$";
	public static final String TAG_NAME = "sandBox";
	
	public static final String PROPERTITY_FILE_NAME = "filename";
	public static final String PROPERTITY_TAG = "tag";

	
	public static SandBoxConfig getInstance(){
		SandBoxConfig model = new SandBoxConfig();
        model.initialize(SandBoxConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static SandBoxConfig getInstance(CompositeMap context){
		SandBoxConfig model = new SandBoxConfig();
        model.initialize(SandBoxConfig.createContext(context,TAG_NAME));
        return model;
    }
	
	public String getFileName(){
		return getString(PROPERTITY_FILE_NAME);
	}
	
	public void setFileName(String fileName){
		putString(PROPERTITY_FILE_NAME, fileName);
	}
	
	public String getTag(){
		return getString(PROPERTITY_TAG);
	}
	
	public void setTag(String tag){
		putString(PROPERTITY_TAG, tag);
	}
}
