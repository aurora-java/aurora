package aurora.presentation.component.std;

import uncertain.composite.CompositeMap;

public class HBox extends Box {
	
	protected int getRows(CompositeMap view){
		return 1;
	}
	
	protected int getColumns(CompositeMap view){
		return UNLIMITED;
	}
}
