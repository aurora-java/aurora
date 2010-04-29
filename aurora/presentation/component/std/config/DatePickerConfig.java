package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class DatePickerConfig extends InputFieldConfig {
	
	public static final String TAG_NAME = "datePicker";

	
	public static DatePickerConfig getInstance(){
		DatePickerConfig model = new DatePickerConfig();
        model.initialize(DatePickerConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static DatePickerConfig getInstance(CompositeMap context){
		DatePickerConfig model = new DatePickerConfig();
        model.initialize(DatePickerConfig.createContext(context,TAG_NAME));
        return model;
    }
}
