package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class TableConfig extends ComponentConfig {
	
	public static final String TAG_NAME = "grid";
	
	public static final String PROPERTITY_COLUMNS = "columns";
	public static final String PROPERTITY_EDITORS = "editors";
	public static final String PROPERTITY_DATASET = "dataset";
	public static final String PROPERTITY_ROW_RENDERER = "rowrenderer";
	public static final String PROPERTITY_PERCENT_WIDTH = "percentwidth";
	
	public static TableConfig getInstance(){
		TableConfig model = new TableConfig();
        model.initialize(TableConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static TableConfig getInstance(CompositeMap context){
		TableConfig model = new TableConfig();
        model.initialize(TableConfig.createContext(context,TAG_NAME));
        return model;
    }
	
	public String getRowRenderer(){
		return getString(PROPERTITY_ROW_RENDERER);
	}
	public void setRowRenderer(String renderer){
		putString(PROPERTITY_ROW_RENDERER,renderer);
	}
	public String getPercentWidth(){
		return getString(PROPERTITY_PERCENT_WIDTH);
	}
	public void setPercentWidth(String percentWidth){
		putString(PROPERTITY_PERCENT_WIDTH,percentWidth);
	}
	public String getDataSet(){
		return getString(PROPERTITY_DATASET);	
	}
	public void setDataSet(String ds){
		putString(PROPERTITY_DATASET, ds);
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
