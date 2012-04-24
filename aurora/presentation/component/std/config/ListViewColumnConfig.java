package aurora.presentation.component.std.config;

import java.util.List;

import uncertain.composite.CompositeMap;

public class ListViewColumnConfig extends ComponentConfig {
	
	private static final String TAG_NAME = "column";
	
	private static final String PROPERTITY_HEAD_ALIGN = "headAlign";
	
	private static final String PROPERTITY_ALIGN = "align";
	
	private static final String PROPERTITY_PROMPT = "prompt";
	
	private static final String DEFAUTL_HEAD_ALIGN = "center";
	
	private static final String DEFAUTL_ALIGN = "left";
	
	public static ListViewColumnConfig getInstance(){
		ListViewColumnConfig model = new ListViewColumnConfig();
        model.initialize(ListViewColumnConfig.createContext(null,TAG_NAME));
        model.removeMapping();
        return model;
    }
	
	public static ListViewColumnConfig getInstance(CompositeMap context){
		ListViewColumnConfig model = new ListViewColumnConfig();
        model.initialize(ListViewColumnConfig.createContext(context,TAG_NAME));
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
    
    
    public String getHeadAlign(){
        return getString(PROPERTITY_HEAD_ALIGN, DEFAUTL_HEAD_ALIGN);
    }
    public void setHeadAlign(String headAlign){
        putString(PROPERTITY_HEAD_ALIGN, headAlign);
    }
    
    public int getWidth(){
        return getInt(PROPERTITY_WIDTH,10);
    }
    public void setWidth(int width){
        putInt(PROPERTITY_WIDTH, width);
    }
    
}
