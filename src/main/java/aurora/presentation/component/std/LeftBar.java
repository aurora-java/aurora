package aurora.presentation.component.std;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import uncertain.ocm.IObjectRegistry;


public class LeftBar extends SideBar {

	public LeftBar(IObjectRegistry registry) {
		super(registry);
	}
	
	protected String getDefaultClass(BuildSession session, ViewContext context){
		return DEFAULT_CLASS + " item-leftbar";
	}

	@Override
	protected TYPES getBarType() {
		return TYPES.LEFT;
	}

}
