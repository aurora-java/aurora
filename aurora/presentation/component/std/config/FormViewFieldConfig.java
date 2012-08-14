package aurora.presentation.component.std.config;

import java.util.List;

import uncertain.composite.CompositeMap;

public class FormViewFieldConfig extends ComponentConfig {
	
	private static final String TAG_NAME = "field";
	
	private static final String PROPERTITY_PROMPT_ALIGN = "promptalign";
	
	private static final String PROPERTITY_ALIGN = "align";
	
	private static final String PROPERTITY_PROMPT = "prompt";
	
	private static final String DEFAUTL_PROMPT_ALIGN = "left";
	
	private static final String DEFAUTL_ALIGN = "center";
	
	public static FormViewFieldConfig getInstance(){
		FormViewFieldConfig model = new FormViewFieldConfig();
        model.initialize(FormViewFieldConfig.createContext(null,TAG_NAME));
        model.removeMapping();
        return model;
    }
	
	public static FormViewFieldConfig getInstance(CompositeMap context){
		FormViewFieldConfig model = new FormViewFieldConfig();
        model.initialize(FormViewFieldConfig.createContext(context,TAG_NAME));
        model.removeMapping();
        return model;
    }
	
	private void removeMapping(){
		List childs = object_context.getChilds();
		if(childs!=null){
			Object[] array = childs.toArray();
			for(int i=0;i<array.length;i++){
				CompositeMap map = (CompositeMap)array[i];
				object_context.removeChild(map);
			}
		}
	}
	
	public String getPromptAlign(){
        return getString(PROPERTITY_PROMPT_ALIGN, DEFAUTL_PROMPT_ALIGN);
    }
	
    public void setPromptAlign(String align){
        putString(PROPERTITY_PROMPT_ALIGN, align);
    }
    
    public String getAlign(){
    	return getString(PROPERTITY_ALIGN, DEFAUTL_ALIGN);
    }
    
    public void setAlign(String align){
    	putString(PROPERTITY_ALIGN, align);
    }
    
    
    public String getPrompt(){
        return getString(PROPERTITY_PROMPT, "");
    }
    
    public void setPrompt(String prompt){
        putString(PROPERTITY_PROMPT, prompt);
    }
    
    public Integer getPromptWidth(){
    	return getInteger(FormViewConfig.PROPERTITY_PROMPT_WIDTH);
    }
    
    public int getWidth(){
        return getInt(PROPERTITY_WIDTH,0);
    }
    public void setWidth(int width){
        putInt(PROPERTITY_WIDTH, width);
    }
}
