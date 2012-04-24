package aurora.presentation.component.std.config;

import java.util.List;

import uncertain.composite.CompositeMap;

public class GridColumnConfig extends ComponentConfig {
	
	public static final String TAG_NAME = "column";
	
	public static final String PROPERTITY_EDITOR = "editor";
	public static final String PROPERTITY_ALIGN = "align";
	public static final String PROPERTITY_LOCK = "lock";
	public static final String PROPERTITY_HIDDEN = "hidden";
	public static final String PROPERTITY_RESIZABLE = "resizable";
	public static final String PROPERTITY_RENDERER = "renderer";
	public static final String PROPERTITY_FOOTER_RENDERER = "footerrenderer";
	public static final String PROPERTITY_SORTABLE = "sortable";
	public static final String PROPERTITY_SORTABLE_FIELD = "sortfield";
	public static final String PROPERTITY_FOR_EXPORT = "forexport";
	public static final String PROPERTITY_EXPORT_FIELD = "exportfield";
	public static final String PROPERTITY_EDITOR_FUNCTION = "editorfunction";
	public static final String PROPERTITY_AUTO_ADJUST = "autoadjust";
	
	
	private static final String DEFAULT_ALIGN = "left";
	
	
	public static GridColumnConfig getInstance(){
		GridColumnConfig model = new GridColumnConfig();
        model.initialize(GridColumnConfig.createContext(null,TAG_NAME));
        model.removeMapping();
        return model;
    }
	
	public static GridColumnConfig getInstance(CompositeMap context){
		GridColumnConfig model = new GridColumnConfig();
        model.initialize(GridColumnConfig.createContext(context,TAG_NAME));
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
	
//	public String getDataIndex(){
//		return getString(PROPERTITY_DATAINDEX);
//	}
//	public void setDataIndex(String index){
//		putString(PROPERTITY_DATAINDEX, index);
//	}
	
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
	
	public String getAlign(){
		return getString(PROPERTITY_ALIGN,DEFAULT_ALIGN);		
	}
	public void setAlign(String align){
		putString(PROPERTITY_ALIGN, align);
	}
	
	public boolean isSortable(){
		return getBoolean(PROPERTITY_SORTABLE, true);
	}
	public void setSortable(boolean sortable){
		putBoolean(PROPERTITY_SORTABLE, sortable);
	}
	
	public String getSortField(){
		return getString(PROPERTITY_SORTABLE_FIELD);		
	}
	
	public void setSortField(String field){
		putString(PROPERTITY_SORTABLE_FIELD, field);
	}
	
	public String getExportField(){
		return getString(PROPERTITY_EXPORT_FIELD);		
	}
	
	public void setExportField(String field){
		putString(PROPERTITY_EXPORT_FIELD, field);
	}	
	public boolean isForExport(){
		return getBoolean(PROPERTITY_FOR_EXPORT,true);		
	}
	
	public void setForExport(boolean forExport){
		putBoolean(PROPERTITY_FOR_EXPORT, forExport);
	}
	
	public boolean isResizable(){
		return getBoolean(PROPERTITY_RESIZABLE, true);
	}
	public void setResizable(boolean resiable){
		putBoolean(PROPERTITY_RESIZABLE, resiable);
	}
	public boolean isAutoAdjust(){
		return getBoolean(PROPERTITY_AUTO_ADJUST, true);
	}
	public void setAutoAdjust(boolean autoAdjust){
		putBoolean(PROPERTITY_AUTO_ADJUST, autoAdjust);
	}
	public String getPrompt(){
		return getString(PROPERTITY_PROMPT);		
	}
	public void setPrompt(String prompt){
		putString(PROPERTITY_PROMPT, prompt);
	}
	
	public String getFooterRenderer(){
		return getString(PROPERTITY_FOOTER_RENDERER);
	}
	public void setFooterRenderer(String renderer){
		putString(PROPERTITY_FOOTER_RENDERER, renderer);
	}
	
	public String getRenderer(){
		return getString(PROPERTITY_RENDERER, "");		
	}
	public void setRenderer(String renderer){
		putString(PROPERTITY_RENDERER, renderer);
	}
	
	public String getEditor(){
		return getString(PROPERTITY_EDITOR);		
	}
	public void setEditor(String editor){
		putString(PROPERTITY_EDITOR, editor);
	}
	
	public String getEditorFunction(){
		return getString(PROPERTITY_EDITOR_FUNCTION);
	}
	public void setEditorFunction(String func){
		putString(PROPERTITY_EDITOR_FUNCTION, func);
	}
}
