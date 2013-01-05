package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class GridConfig extends ComponentConfig {

	public static final String VERSION = "$Revision$";
	
	public static final String TAG_NAME = "grid";
	
	public static final String PROPERTITY_COLUMNS = "columns";
	public static final String PROPERTITY_EDITOR = "editor";
	public static final String PROPERTITY_EDITORS = "editors";
	public static final String PROPERTITY_TOOLBAR = "toolBar";
	public static final String PROPERTITY_DATASET = "dataset";
	public static final String PROPERTITY_NAVBAR = "navbar";
	public static final String PROPERTITY_AUTO_FOCUS = "autofocus";
	public static final String PROPERTITY_AUTO_APPEND = "autoappend";
	public static final String PROPERTITY_ROW_RENDERER = "rowrenderer";
	public static final String PROPERTITY_CAN_PASTE = "canpaste";
	public static final String PROPERTITY_CAN_WHEEL = "canwheel";
	public static final String PROPERTITY_SHOW_ROWNUMBER = "showrownumber";
	public static final String PROPERTITY_AUTO_ADJUST = "autoadjust";
	
//	public static final String COLUMN_DATAINDEX = "dataindex";
//	public static final String COLUMN_LOCK = "lock";
//	public static final String COLUMN_HIDDEN = "hidden";
//	public static final String COLUMN_RESIZABLE = "resizable";
//	public static final String COLUMN_PROMPT = "prompt";
//	
//	private static final int DEFALUT_HEAD_HEIGHT = 25;
//	private static final int COLUMN_WIDTH = 100;
//	
//	private static final String DEFAULT_CLASS = "item-grid-wrap";
//	private static final String MAX_ROWS = "maxRow";
//	private static final String ROW_SPAN = "rowspan";
//	private static final String COL_SPAN = "colspan";
//	private static final String ROW_HEIGHT = "rowHeight";
//	private static final String HEAD_HEIGHT = "headHeight";
//	private static final String LOCK_WIDTH = "lockwidth";
//	
//	private static final String COLUMN_TYPE = "type";
//	private static final String TYPE_CELL_CHECKBOX = "cellcheck";
//	private static final String TYPE_CELL_RADIO = "cellradio";
//	private static final String TYPE_ROW_CHECKBOX = "rowcheck";
//	private static final String TYPE_ROW_RADIO = "rowradio";
	
	
	public static GridConfig getInstance(){
		GridConfig model = new GridConfig();
        model.initialize(GridConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static GridConfig getInstance(CompositeMap context){
		GridConfig model = new GridConfig();
        model.initialize(GridConfig.createContext(context,TAG_NAME));
        return model;
    }
	
	public String getRowRenderer(){
		return getString(PROPERTITY_ROW_RENDERER);
	}
	public void setRowRenderer(String renderer){
		putString(PROPERTITY_ROW_RENDERER,renderer);
	}
	
	public String getDataSet(){
		return getString(PROPERTITY_DATASET);	
	}
	public void setDataSet(String ds){
		putString(PROPERTITY_DATASET, ds);
	}
	
	public boolean isAutoFocus(){
		return getBoolean(PROPERTITY_AUTO_FOCUS, false);//默认改成false，防止滚动		
	}
	public void setAutoFocus(boolean focus){
		putBoolean(PROPERTITY_AUTO_FOCUS, focus);
	}
	
	public Boolean isAutoAdjust(){
		return getBoolean(PROPERTITY_AUTO_ADJUST);		
	}
	public void setAutoAdjust(Boolean autoAdjust){
		putBoolean(PROPERTITY_AUTO_ADJUST, autoAdjust);
	}
	
	public Boolean isAutoAppend(){
		return getBoolean(PROPERTITY_AUTO_APPEND);		
	}
	public void setAutoAppend(Boolean append){
		putBoolean(PROPERTITY_AUTO_APPEND, append);
	}
	public boolean isCanPaste(){
		return getBoolean(PROPERTITY_CAN_PASTE, false);		
	}
	public void setCanPaste(boolean canPaste){
		putBoolean(PROPERTITY_CAN_PASTE, canPaste);
	}
	public boolean isCanWheel(){
		return getBoolean(PROPERTITY_CAN_WHEEL, true);		
	}
	public void setCanWheel(boolean canPaste){
		putBoolean(PROPERTITY_CAN_WHEEL, canPaste);
	}
	
	public boolean hasNavBar(){
		return getBoolean(PROPERTITY_NAVBAR, false);
	}
	public void setNavBar(boolean nb){
		putBoolean(PROPERTITY_NAVBAR, nb);
	}
	public CompositeMap getColumns(){
		CompositeMap context = getObjectContext();
    	CompositeMap columns = context.getChild(PROPERTITY_COLUMNS);
    	if(columns == null){
    		columns = new CompositeMap(PROPERTITY_COLUMNS);
    		context.addChild(columns);
    	}
    	return columns;  
	}
	
	public void addColumn(GridColumnConfig column){
		CompositeMap columns = getColumns();
		columns.addChild(column.getObjectContext());
	}
	
	public CompositeMap getEditors(){
		CompositeMap context = getObjectContext();
    	CompositeMap editors = context.getChild(PROPERTITY_EDITORS);
    	if(editors == null){
    		editors = new CompositeMap(PROPERTITY_EDITORS);
    		context.addChild(editors);
    	}
    	return editors;  
	}
	
	public void addEditor(ComponentConfig editor){
		CompositeMap editors = getEditors();
		editors.addChild(editor.getObjectContext());
	}
}
