package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class DateFieldConfig extends ComponentConfig {
	public static final String VERSION = "$Revision$";
	
	public static final String TAG_NAME = "dateField";
	public static final String PROPERTITY_DAY_RENDERER = "dayrenderer";
	public static final String PROPERTITY_ENABLE_MONTH_BTN = "enablemonthbtn";
	public static final String PROPERTITY_ENABLE_YEAR_BTN = "enableyearbtn";
	public static final String PROPERTITY_ENABLE_BESIDE_DAYS = "enablebesidedays";
	
	public static DateFieldConfig getInstance() {
		DateFieldConfig model = new DateFieldConfig();
		model.initialize(DateFieldConfig.createContext(null, TAG_NAME));
		return model;
	}

	public static DateFieldConfig getInstance(CompositeMap context) {
		DateFieldConfig model = new DateFieldConfig();
		model.initialize(DateFieldConfig.createContext(context, TAG_NAME));
		return model;
	}
	
	public String getDayRenderer(){
		return getString(PROPERTITY_DAY_RENDERER);
	}
	public void setDayRenderer(String renderer){
		putString(PROPERTITY_DAY_RENDERER, renderer);
	}
	
	public String getEnableMonthBtn(){
		return getString(PROPERTITY_ENABLE_MONTH_BTN, "both");
	}
	public void setEnableMonthBtn(String v){
		putString(PROPERTITY_ENABLE_MONTH_BTN, v);
	}
	
	public String getEnableYearBtn(){
		return getString(PROPERTITY_ENABLE_YEAR_BTN, "both");
	}
	public void setEnableYearBtn(String v){
		putString(PROPERTITY_ENABLE_YEAR_BTN, v);
	}
	
	public String getEnablebeSideDays(){
		return getString(PROPERTITY_ENABLE_BESIDE_DAYS, "both");
	}
	public void setEnablebeSideDays(String v){
		putString(PROPERTITY_ENABLE_BESIDE_DAYS, v);
	}
}
