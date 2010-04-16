package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;


public class FormConfig extends BoxConfig {
	
	public static final String TAG_NAME = "form";
	
	public static final String PROPERTITY_TITLE="title";	
	public static final String PROPERTITY_SHOWMARGIN = "showmargin";
	
	public static FormConfig getInstance(){
		FormConfig model = new FormConfig();
        model.initialize(FormConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static FormConfig getInstance(CompositeMap context){
		FormConfig model = new FormConfig();
        model.initialize(FormConfig.createContext(context,TAG_NAME));
        return model;
    }
	
	public String getTitle() {
		return getString(PROPERTITY_TITLE);
	}
	public void setTitlte(String title){
		putString(PROPERTITY_TITLE, title);	
	}
	
	public boolean isShowMargin() {
		return getBoolean(PROPERTITY_SHOWMARGIN, false);		
	}
	public void setShowMargin(boolean showmargin){
		putBoolean(PROPERTITY_SHOWMARGIN, showmargin);
	}
	
	
}
