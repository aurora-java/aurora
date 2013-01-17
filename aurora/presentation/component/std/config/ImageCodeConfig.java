package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class ImageCodeConfig extends ComponentConfig {
	
	public static final String VERSION = "$Revision";
	public static final String TAG_NAME = "imageCode";
	
	public static final String PROPERTITY_ENABLE = "enable";
	public static final String PROPERTITY_SRC = "src";
	
	
	public static ImageCodeConfig getInstance(){
		ImageCodeConfig model = new ImageCodeConfig();
        model.initialize(ImageCodeConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static ImageCodeConfig getInstance(CompositeMap context){
		ImageCodeConfig model = new ImageCodeConfig();
        model.initialize(ImageCodeConfig.createContext(context,TAG_NAME));
        return model;
    }
	
	public boolean isEnable(){
		return getBoolean(PROPERTITY_ENABLE,true);
	}
	
	public void setEnable(boolean enable){
		putBoolean(PROPERTITY_ENABLE, enable);
	}
	
	public String getSrc(){
		return getString(PROPERTITY_SRC);
	}
	
	public void setSrc(String src){
		putString(PROPERTITY_SRC, src);
	}
}
