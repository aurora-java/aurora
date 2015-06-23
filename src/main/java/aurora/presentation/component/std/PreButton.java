package aurora.presentation.component.std;

import uncertain.ocm.IObjectRegistry;

public class PreButton extends ArrowButton {
	
	public PreButton(IObjectRegistry registry) {
		super(registry);
	}

	public static final String VERSION = "$Revision$";

	protected String getType(){
		return 	TYPE_LEFT;	
	}
	
}
