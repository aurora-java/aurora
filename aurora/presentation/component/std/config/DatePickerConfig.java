package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class DatePickerConfig extends InputFieldConfig {

	public static final String VERSION = "$Revision$";
	
	public static final String TAG_NAME = "datePicker";

	public static final String PROPERTITY_VIEW_SIZE = "viewsize";
	public static final String PROPERTITY_DAY_RENDERER = "dayrenderer";
	public static final String PROPERTITY_ENABLE_MONTH_BTN = "enablemonthbtn";
	public static final String PROPERTITY_ENABLE_YEAR_BTN = "enableyearbtn";
	public static final String PROPERTITY_ENABLE_BESIDE_DAYS = "enablebesidedays";

	public static DatePickerConfig getInstance() {
		DatePickerConfig model = new DatePickerConfig();
		model.initialize(DatePickerConfig.createContext(null, TAG_NAME));
		return model;
	}

	public static DatePickerConfig getInstance(CompositeMap context) {
		DatePickerConfig model = new DatePickerConfig();
		model.initialize(DatePickerConfig.createContext(context, TAG_NAME));
		return model;
	}

	public void setViewSize(int viewSize) {
		putInt(PROPERTITY_VIEW_SIZE, viewSize);
	}

	public int getViewSize() {
		return getInt(PROPERTITY_VIEW_SIZE, 1);
	}

	public void setDayRenderer(String dayRenderer) {
		putString(PROPERTITY_DAY_RENDERER, dayRenderer);
	}

	public String getDayRenderer() {
		return getString(PROPERTITY_DAY_RENDERER);
	}

	public void setEnableMonthBtn(String enableMonthBtn) {
		putString(PROPERTITY_ENABLE_MONTH_BTN, enableMonthBtn);
	}

	public String getEnableMonthBtn() {
		return getString(PROPERTITY_ENABLE_MONTH_BTN, "both");
	}

	public void setEnableYearBtn(String enableYearBtn) {
		putString(PROPERTITY_ENABLE_YEAR_BTN, enableYearBtn);
	}

	public String getEnableYearBtn() {
		return getString(PROPERTITY_ENABLE_YEAR_BTN, "both");
	}
	
	public void setEnableBesideDays(String enableBesideDays) {
		putString(PROPERTITY_ENABLE_BESIDE_DAYS, enableBesideDays);
	}
	
	public String getEnableBesideDays() {
		return getString(PROPERTITY_ENABLE_BESIDE_DAYS, "both");
	}
}
