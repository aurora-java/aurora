package aurora.presentation.component.std.config;

import java.util.List;

import uncertain.composite.CompositeMap;

public class FormViewConfig extends ComponentConfig {
	
	public static final String TAG_NAME = "formView";
	
	private static final String PROPERTITY_TITLE = "title";
	private static final String PROPERTITY_FIELDS = "formSection";
	private static final String PROPERTITY_DATA_MODEL = "datamodel";
	
	public static final String PROPERTITY_PROMPT_ALIGN = "promptalign";
	public static final String PROPERTITY_PROMPT_WIDTH = "promptwidth";
	public static final String PROPERTITY_TABLE_STYLE = "tablestyle";
	
	private static final String DEFAULT_PROMPT_ALIGN = "left";
	private static final int DEFAULT_PROMPT_WIDTH = 15;
	
	public static FormViewConfig getInstance(){
		FormViewConfig model = new FormViewConfig();
        model.initialize(FormViewConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static FormViewConfig getInstance(CompositeMap context){
		FormViewConfig model = new FormViewConfig();
        model.initialize(FormViewConfig.createContext(context,TAG_NAME));
        return model;
    }
	
	public List getSections(){
		CompositeMap context = getObjectContext();
    	List sections = context.getChildsNotNull();
    	return sections;  
	}
	
	
	public String getDataModel(){
        return getString(PROPERTITY_DATA_MODEL);
    }
    public void setDataModel(String dm){
        putString(PROPERTITY_DATA_MODEL, dm);
    }
    
    
    public String getTitle(){
    	return getString(PROPERTITY_TITLE);
    }
    public void setTitle(String title){
    	putString(PROPERTITY_TITLE, title);
    }
    
    public String getTableStyle(){
    	return getString(PROPERTITY_TABLE_STYLE);
    }
    public void setTableStyle(String style){
    	putString(PROPERTITY_TABLE_STYLE, style);
    }
    
    public String getPromptAlign(){
    	return getString(PROPERTITY_PROMPT_ALIGN,DEFAULT_PROMPT_ALIGN);
    }
    public void setPromptAlign(String align){
    	putString(PROPERTITY_PROMPT_ALIGN, align);
    }
    
    public int getPromptWidth(){
    	return getInt(PROPERTITY_PROMPT_WIDTH,DEFAULT_PROMPT_WIDTH);
    }
    public void setPromptWidth(int width){
    	putInt(PROPERTITY_PROMPT_WIDTH, width);
    }
    
    public String getTitleText(){
    	CompositeMap context = getObjectContext();
    	CompositeMap title = context.getChild(PROPERTITY_TITLE);
    	if(title!=null){
    		return title.getText();
    	}
    	return null;
    }
}
