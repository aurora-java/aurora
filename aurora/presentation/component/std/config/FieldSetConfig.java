package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class FieldSetConfig extends GridLayouConfig {
	
	public static final String VERSION = "$Revision$";
	public static final String TAG_NAME = "fieldSet";
	public static final String PROPERTITY_TITLE="title";
	
	public static FieldSetConfig getInstance() {
		FieldSetConfig model = new FieldSetConfig();
		model.initialize(FieldSetConfig.createContext(null, TAG_NAME));
		return model;
	}

	public static FieldSetConfig getInstance(CompositeMap context) {
		FieldSetConfig model = new FieldSetConfig();
		model.initialize(FieldSetConfig.createContext(context, TAG_NAME));
		return model;
	}
	
	public String getTitle(){
		return getString(PROPERTITY_TITLE,"");
	}
	public void setTitle(String title){
		putString(PROPERTITY_TITLE,title);
	}
}
