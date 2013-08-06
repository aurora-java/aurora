package aurora.presentation.component.std;

import uncertain.ocm.IObjectRegistry;

public class NextButton extends ArrowButton {
	
	public NextButton(IObjectRegistry registry) {
		super(registry);
	}

	public static final String VERSION = "$Revision$";
	
	protected String getType(){
		return 	TYPE_RIGHT;	
	}
}
