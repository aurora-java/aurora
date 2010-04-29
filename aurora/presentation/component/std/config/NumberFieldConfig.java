package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class NumberFieldConfig extends InputFieldConfig {
	
	public static final String TAG_NAME = "numberField";
	
	public static NumberFieldConfig getInstance(){
		NumberFieldConfig model = new NumberFieldConfig();
        model.initialize(NumberFieldConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static NumberFieldConfig getInstance(CompositeMap context){
		NumberFieldConfig model = new NumberFieldConfig();
        model.initialize(NumberFieldConfig.createContext(context,TAG_NAME));
        return model;
    }
}
