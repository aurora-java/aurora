package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;


public class ComboBoxConfig extends InputFieldConfig {
	
	public static final String TAG_NAME = "comboBox";

	
	public static ComboBoxConfig getInstance(){
		ComboBoxConfig model = new ComboBoxConfig();
        model.initialize(ComboBoxConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static ComboBoxConfig getInstance(CompositeMap context){
		ComboBoxConfig model = new ComboBoxConfig();
        model.initialize(ComboBoxConfig.createContext(context,TAG_NAME));
        return model;
    }
}
