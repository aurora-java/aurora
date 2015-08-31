package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class RadioConfig extends ComponentConfig {

	public static final String VERSION = "$Revision$";
	
	public static final String TAG_NAME = "radio";
	
	private static final String PROPERTITY_ITEMS = "items";
	private static final String PROPERTITY_LABEL_FIELD = "labelfield";
	private static final String PROPERTITY_VALUE_FIELD = "valuefield";
	private static final String PROPERTITY_LAYOUT = "layout";
	private static final String PROPERTITY_OPTIONS = "options";
	private static final String PROPERTITY_LABEL_EXPRESSION = "labelexpression";
	public static final String PROPERTITY_RADIO_SEPARATOR = "radioseparator";
//	private static final String PROPERTITY_SELECT_IDNEX = "selectindex";
	
	public static final String DEFAULT_LAYOUT_HORIZONTAL = "horizontal";
	public static final String DEFAULT_LAYOUT_VERTICAL = "vertical";
	public static final String DEFAULT_LABEL_FIELD = "label";
	public static final String DEFAULT_VALUE_FIELD = "value";
	
	
	public static RadioConfig getInstance(){
		RadioConfig model = new RadioConfig();
        model.initialize(GridConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static RadioConfig getInstance(CompositeMap context){
		RadioConfig model = new RadioConfig();
        model.initialize(GridConfig.createContext(context,TAG_NAME));
        return model;
    }
	
//	public int getSelectIndex(){
//		return getInt(PROPERTITY_SELECT_IDNEX, 0);
//	}
//	public void setSelectIndex(int index){
//		putInt(PROPERTITY_SELECT_IDNEX, index);
//	}
	
	public String getLayout(){
		return getString(PROPERTITY_LAYOUT, DEFAULT_LAYOUT_HORIZONTAL);
	}
	public void setLayout(String layout){
		putString(PROPERTITY_LAYOUT, layout);
	}
	
	public String getLabelField(){
		return getString(PROPERTITY_LABEL_FIELD, DEFAULT_LABEL_FIELD);
	}
	public void setLabelField(String field){
		putString(PROPERTITY_LABEL_FIELD, field);
	}
	
	public String getValueField(){
		return getString(PROPERTITY_VALUE_FIELD, DEFAULT_VALUE_FIELD);
	}
	public void setValueField(String field){
		putString(PROPERTITY_VALUE_FIELD, field);
	}
	
	public String getLabelExpression(){
		return getString(PROPERTITY_LABEL_EXPRESSION);
	}
	public void setLabelExpression(String expression){
		putString(PROPERTITY_LABEL_EXPRESSION, expression);
	}
	
	public CompositeMap getItems(){
		CompositeMap context = getObjectContext();
    	CompositeMap columns = context.getChild(PROPERTITY_ITEMS);
    	return columns;  
	}
	
	public String getOptions(){
		return getString(PROPERTITY_OPTIONS);
	}
	public void setOptions(String options){
		putString(PROPERTITY_OPTIONS, options);
	}
	public String getRadioSeparator(String defaultvalue){
		return getString(PROPERTITY_RADIO_SEPARATOR,defaultvalue);
	}
	public void setRadioSeparator(String radioseparator){
		putString(PROPERTITY_RADIO_SEPARATOR, radioseparator);
	}
	
	
}
