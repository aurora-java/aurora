package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class DateTimePickerConfig extends DatePickerConfig {

	public static final String VERSION = "$Revision$";
	
	public static final String TAG_NAME = "dateTimePicker";

	public static final String PROPERTITY_HOUR = "hour";
	public static final String PROPERTITY_MINUTE = "minute";
	public static final String PROPERTITY_SECOND = "second";

	public static DateTimePickerConfig getInstance() {
		DateTimePickerConfig model = new DateTimePickerConfig();
		model.initialize(DateTimePickerConfig.createContext(null, TAG_NAME));
		return model;
	}

	public static DateTimePickerConfig getInstance(CompositeMap context) {
		DateTimePickerConfig model = new DateTimePickerConfig();
		model.initialize(DateTimePickerConfig.createContext(context, TAG_NAME));
		return model;
	}

	public void setHour(int hour) {
		putInt(PROPERTITY_HOUR, hour);
	}

	public int getHour() {
		return getInt(PROPERTITY_HOUR, 0);
	}

	public void setMinute(int minute) {
		putInt(PROPERTITY_MINUTE, minute);
	}

	public int getMinute() {
		return getInt(PROPERTITY_MINUTE, 0);
	}

	public void setSecond(int second) {
		putInt(PROPERTITY_SECOND, second);
	}

	public int getSecond() {
		return getInt(PROPERTITY_SECOND, 0);
	}
}
