package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;


public class TextFieldConfig extends InputFieldConfig {
	
	public static final String TAG_NAME = "textField";
	public static final String INPUT_TYPE = "inputtype";
	public static final String PROPERTITY_TYPE_CASE = "typecase";
	public static final String PROPERTITY_RESTRICT = "restrict";
	public static final String PROPERTITY_RESTRICT_INFO = "restrictinfo";

	
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
    
    public String getTypeCase(){
    	return getString(PROPERTITY_TYPE_CASE);
    }
    public void setTypeCase(String typeCase){
        putString(PROPERTITY_TYPE_CASE, typeCase);
    }
    public String getRestrict(){
    	return getString(PROPERTITY_RESTRICT);
    }
    public void setRestrict(String restrict){
    	putString(PROPERTITY_RESTRICT, restrict);
    }
    public String getRestrictInfo(){
    	return getString(PROPERTITY_RESTRICT_INFO);
    }
    public void setRestrictInfo(String restrictInfo){
    	putString(PROPERTITY_RESTRICT_INFO, restrictInfo);
    }
    
}
