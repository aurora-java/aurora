package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class CurrencyFieldConfig extends NumberFieldConfig {
	public static final String VERSION = "$Revision$";
	
	public static final String TAG_NAME = "currencyField";
	
	public static CurrencyFieldConfig getInstance(){
		CurrencyFieldConfig model = new CurrencyFieldConfig();
        model.initialize(CurrencyFieldConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static CurrencyFieldConfig getInstance(CompositeMap context){
		CurrencyFieldConfig model = new CurrencyFieldConfig();
        model.initialize(CurrencyFieldConfig.createContext(context,TAG_NAME));
        return model;
    }
}
