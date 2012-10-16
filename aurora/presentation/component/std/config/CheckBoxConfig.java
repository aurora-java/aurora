package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class CheckBoxConfig extends ComponentConfig {
	
	public static final String TAG_NAME = "checkBox";
	public static final String PROPERTITY_CHECKEDVALUE = "checkedvalue";
	public static final String PROPERTITY_UNCHECKEDVALUE = "uncheckedvalue";
	public static final String PROPERTITY_LABEL = "label";
	
	
	public static CheckBoxConfig getInstance(){
		CheckBoxConfig model = new CheckBoxConfig();
        model.initialize(CheckBoxConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static CheckBoxConfig getInstance(CompositeMap context){
		CheckBoxConfig model = new CheckBoxConfig();
        model.initialize(CheckBoxConfig.createContext(context,TAG_NAME));
        return model;
    }
	
	public String getLabel(){
        return getString(PROPERTITY_LABEL,"");
    }
    public void setLabel(String label){
        putString(PROPERTITY_LABEL, label);
    }
}
