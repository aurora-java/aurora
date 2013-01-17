package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class NavBarConfig extends ComponentConfig {
	
	public static final String VERSION = "$Revision";
	public static final String TAG_NAME = "navBar";
	
	public static final String PROPERTITY_DATASET = "dataset";
	public static final String PROPERTITY_NAVBAR_TYPE = "navbartype";
	public static final String PROPERTITY_PAGE_SIZE_EDITABLE = "enablepagesize";
	public static final String PROPERTITY_MAX_PAGE_COUNT = "maxpagecount";
	
	
	public static NavBarConfig getInstance(){
		NavBarConfig model = new NavBarConfig();
        model.initialize(NavBarConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static NavBarConfig getInstance(CompositeMap context){
		NavBarConfig model = new NavBarConfig();
        model.initialize(NavBarConfig.createContext(context,TAG_NAME));
        return model;
    }
	
	public String getDataset(){
		return getString(PROPERTITY_DATASET);
	}
	
	public void setDataset(String dataset){
		putString(PROPERTITY_DATASET, dataset);
	}
	
	public String getNavBarType(){
		return getString(PROPERTITY_NAVBAR_TYPE,"complex");
	}
	
	public void setNavBarType(String navBarType){
		putString(PROPERTITY_NAVBAR_TYPE, navBarType);
	}
	
	public int getMaxPageCount(){
		return getInt(PROPERTITY_MAX_PAGE_COUNT,10);
	}
	
	public void setMaxPageCount(int defaultQueryField){
		putInt(PROPERTITY_MAX_PAGE_COUNT, defaultQueryField);
	}
	
	public boolean isPageSizeEditable(){
		return getBoolean(PROPERTITY_PAGE_SIZE_EDITABLE,true);
	}
	
	public void setPageSizeEditable(boolean pageSizeEditable){
		putBoolean(PROPERTITY_PAGE_SIZE_EDITABLE, pageSizeEditable);
	}
	
}
