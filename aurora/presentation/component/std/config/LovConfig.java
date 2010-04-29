package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;


public class LovConfig extends InputFieldConfig {
	
	public static final String TAG_NAME = "lov";

	
	public static LovConfig getInstance(){
		LovConfig model = new LovConfig();
        model.initialize(LovConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static LovConfig getInstance(CompositeMap context){
		LovConfig model = new LovConfig();
        model.initialize(LovConfig.createContext(context,TAG_NAME));
        return model;
    }
}
