package aurora.presentation.component.std;

import uncertain.ocm.IObjectRegistry;

@SuppressWarnings("unchecked")
public class CheckButton extends CheckBox {
	
	public CheckButton(IObjectRegistry registry) {
		super(registry);
	}
	private static final int DEFAULT_HEIGHT = 25;
	private static final int DEFAULT_WIDTH = 60;
	

	protected int getDefaultWidth(){
		return DEFAULT_WIDTH;
	}
	
	protected int getDefaultHeight(){
		return DEFAULT_HEIGHT;
	}
}
