package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class CurrencyLabelConfig extends LabelConfig {
	
	public static final String VERSION = "$Revision$";
	
	public static final String TAG_NAME = "currencyLabel";
	
	public static CurrencyLabelConfig getInstance(){
		CurrencyLabelConfig model = new CurrencyLabelConfig();
        model.initialize(CurrencyLabelConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static CurrencyLabelConfig getInstance(CompositeMap context){
		CurrencyLabelConfig model = new CurrencyLabelConfig();
        model.initialize(CurrencyLabelConfig.createContext(context,TAG_NAME));
        return model;
    }

}
