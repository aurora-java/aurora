package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

/**
 * 
 * @version $Id$
 * @author <a href="mailto:njq.niu@hand-china.com">vincent</a>
 */
public class NumberFieldConfig extends InputFieldConfig {

	public static final String VERSION = "$Revision$";
	
	public static final String TAG_NAME = "numberField";
	public static final String PROPERTITY_ALLOWDECIMALS = "allowdecimals";
	public static final String PROPERTITY_DECIMALPRECISION = "decimalprecision";	
	public static final String PROPERTITY_ALLOWNEGATIVE = "allownegative";
	public static final String PROPERTITY_ALLOWFORMAT = "allowformat";
	
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
	
    public boolean isAllowFormat(){
    	return getBoolean(PROPERTITY_ALLOWFORMAT, false);
    }
    public void setAllowFormat(boolean format){
    	putBoolean(PROPERTITY_ALLOWFORMAT, format);
    }
	
	public boolean isAllowDecimals(){
		return getBoolean(PROPERTITY_ALLOWDECIMALS, true);
	}
	public void setAllowDecimals(boolean allowed){
		putBoolean(PROPERTITY_ALLOWDECIMALS, allowed);
	}
	
	public boolean isAllowNegative(){
		return getBoolean(PROPERTITY_ALLOWNEGATIVE, true);
	}
	public void setAllowNegative(boolean allowed){
		putBoolean(PROPERTITY_ALLOWNEGATIVE, allowed);
	}
	
	public int getDecimalPrecision(CompositeMap model){
		String pre = getString(PROPERTITY_DECIMALPRECISION,"2");
		pre = uncertain.composite.TextParser.parse(pre, model);
		return Integer.valueOf(pre);
	}
	public void setDecimalPrecision(int precision){
		putInt(PROPERTITY_DECIMALPRECISION,precision);
	}
}
