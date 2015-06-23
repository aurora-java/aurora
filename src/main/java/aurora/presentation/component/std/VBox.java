package aurora.presentation.component.std;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;

public class VBox extends Box {
	
	public static final String VERSION = "$Revision$";
	
	public VBox(IObjectRegistry registry) {
		super(registry);
	}
	
	protected int getRows(CompositeMap view,CompositeMap model){
		return UNLIMITED;
	}
	
	protected int getColumns(CompositeMap view,CompositeMap model){
		return 1;
	}
}
