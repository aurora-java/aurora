package aurora.presentation.component.std;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;

public class HBox extends Box {
	
	public static final String VERSION = "$Revision$";
	
	public HBox(IObjectRegistry registry) {
		super(registry);
	}
	
	protected int getRows(CompositeMap view,CompositeMap model){
		return 1;
	}
	
	protected int getColumns(CompositeMap view,CompositeMap model){
		return UNLIMITED;
	}
}
