package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class ListViewConfig extends ComponentConfig {
	
	public static final String TAG_NAME = "list";
	
	private static final String PROPERTITY_COLUMNS = "columns";
	private static final String PROPERTITY_DATA_MODEL = "datamodel";
	
	public static ListViewConfig getInstance(){
		ListViewConfig model = new ListViewConfig();
        model.initialize(ListViewConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static ListViewConfig getInstance(CompositeMap context){
		ListViewConfig model = new ListViewConfig();
        model.initialize(ListViewConfig.createContext(context,TAG_NAME));
        return model;
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
	
	public void addColumn(ListViewColumnConfig column){
		CompositeMap columns = getColumns();
		columns.addChild(column.getObjectContext());
	}
	
	public String getDataModel(){
        return getString(PROPERTITY_DATA_MODEL);
    }
    public void setDataModel(String dm){
        putString(PROPERTITY_DATA_MODEL, dm);
    }
}
