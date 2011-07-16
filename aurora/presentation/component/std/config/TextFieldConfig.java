package aurora.presentation.component.std.config;

import aurora.application.AuroraApplication;
import uncertain.composite.CompositeMap;


public class TextFieldConfig extends InputFieldConfig {
	
	public static final String TAG_NAME = "textField";
	public static final String INPUT_TYPE = "inputtype";
	public static final String PROPERTITY_TYPE_CASE = "typecase";

	
	public static TextFieldConfig getInstance(){
		TextFieldConfig model = new TextFieldConfig();
		CompositeMap context = TextFieldConfig.createContext(null,TAG_NAME);
//		context.setNameSpaceURI(Namespace.AURORA_FRAMEWORK_NAMESPACE);
        model.initialize(context);
        return model;
    }
	
	public static TextFieldConfig getInstance(CompositeMap context){
		TextFieldConfig model = new TextFieldConfig();
		CompositeMap map = TextFieldConfig.createContext(context,TAG_NAME);
//		map.setNameSpaceURI(Namespace.AURORA_FRAMEWORK_NAMESPACE);
        model.initialize(map);
        return model;
    }
	
	public String getInputType(){
        return getString(INPUT_TYPE);
    }
    public void setInputType(String type){
        putString(INPUT_TYPE, type);
    }
}
