package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class ViewItemConfig extends ComponentConfig {
	
	public static final String VERSION = "$Revision$";
	
	public static final String TAG_NAME = "viewItem";

	public static final String PROPERTITY_FORMAT = "format";

	public static ViewItemConfig getInstance() {
		ViewItemConfig model = new ViewItemConfig();
		model.initialize(GridConfig.createContext(null, TAG_NAME));
		return model;
	}

	public static ViewItemConfig getInstance(CompositeMap context) {
		ViewItemConfig model = new ViewItemConfig();
		model.initialize(GridConfig.createContext(context, TAG_NAME));
		return model;
	}

	public String getFormat(){
		return getString(PROPERTITY_FORMAT, "");
	}
	
	public void setFormat(String format){
		putString(PROPERTITY_FORMAT, format);
	}
	
}
