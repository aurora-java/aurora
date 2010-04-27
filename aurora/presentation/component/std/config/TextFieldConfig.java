package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;


public class TextFieldConfig extends InputFieldConfig {
	
	public static final String TAG_NAME = "textField";
	public static final String INPUT_TYPE = "inputtype";

	
	public static TextFieldConfig getInstance(){
		TextFieldConfig model = new TextFieldConfig();
        model.initialize(TextFieldConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static TextFieldConfig getInstance(CompositeMap context){
		TextFieldConfig model = new TextFieldConfig();
        model.initialize(TextFieldConfig.createContext(context,TAG_NAME));
        return model;
    }
	
	public String getInputType(){
        return getString(INPUT_TYPE);
    }
    public void setInputType(String type){
        putString(INPUT_TYPE, type);
    }
}
