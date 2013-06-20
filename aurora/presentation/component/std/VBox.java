package aurora.presentation.component.std;

import uncertain.composite.CompositeMap;

public class VBox extends Box {
	
	public static final String VERSION = "$Revision$";
	
	protected int getRows(CompositeMap view,CompositeMap model){
		return UNLIMITED;
	}
	
	protected int getColumns(CompositeMap view,CompositeMap model){
		return 1;
	}
}
