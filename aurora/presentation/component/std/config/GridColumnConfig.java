package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class GridColumnConfig extends ComponentConfig {
	
	public static final String TAG_NAME = "column";
	
	public static final String PROPERTITY_EDITOR = "editor";
	public static final String PROPERTITY_DATAINDEX = "dataindex";
	public static final String PROPERTITY_LOCK = "lock";
	public static final String PROPERTITY_HIDDEN = "hidden";
	public static final String PROPERTITY_RESIZABLE = "resizable";
	public static final String PROPERTITY_PROMPT = "prompt";
	
	
	public static GridColumnConfig getInstance(){
		GridColumnConfig model = new GridColumnConfig();
        model.initialize(GridColumnConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static GridColumnConfig getInstance(CompositeMap context){
		GridColumnConfig model = new GridColumnConfig();
        model.initialize(GridColumnConfig.createContext(context,TAG_NAME));
        return model;
    }
	
	public String getDataIndex(){
		return getString(PROPERTITY_DATAINDEX);
	}
	public void setDataIndex(String index){
		putString(PROPERTITY_DATAINDEX, index);
	}
	
	public boolean isLock(){
		return getBoolean(PROPERTITY_LOCK, false);
	}
	public void setLock(boolean lock){
		putBoolean(PROPERTITY_LOCK, lock);
	}
	
	public boolean isHidden(){
		return getBoolean(PROPERTITY_HIDDEN, false);
	}
	public void setHidden(boolean hidden){
		putBoolean(PROPERTITY_HIDDEN, hidden);
	}
	
	public boolean isResizable(){
		return getBoolean(PROPERTITY_RESIZABLE, true);
	}
	public void setResizable(boolean resiable){
		putBoolean(PROPERTITY_RESIZABLE, resiable);
	}
	
	public String getPrompt(){
		return getString(PROPERTITY_PROMPT);		
	}
	public void setPrompt(String prompt){
		putString(PROPERTITY_PROMPT, prompt);
	}
	
	public String getEditor(){
		return getString(PROPERTITY_EDITOR);		
	}
	public void setEditor(String editor){
		putString(PROPERTITY_EDITOR, editor);
	}
}
