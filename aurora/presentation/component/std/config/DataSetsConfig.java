package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;

public class DataSetsConfig extends DynamicObject {

	public static final String VERSION = "$Revision$";
	
	public static final String TAG_NAME = "dataSets";
	
	public static CompositeMap createContext(CompositeMap map,String tagName) {
		CompositeMap context = new CompositeMap(tagName);
		if(map != null){
			context.copy(map);
		}
		return context;		
	}
	
	public static DataSetsConfig getInstance(){
		DataSetsConfig model = new DataSetsConfig();
        model.initialize(DataSetsConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static DataSetsConfig getInstance(CompositeMap context){
		DataSetsConfig model = new DataSetsConfig();
        model.initialize(DataSetsConfig.createContext(context,TAG_NAME));
        return model;
    }
	
	public void addDataSet(DataSetConfig dataset){
		CompositeMap context = getObjectContext();
		context.addChild(dataset.getObjectContext());
	}
	
}
