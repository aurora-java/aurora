package aurora.presentation.component.std;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import uncertain.ocm.IObjectRegistry;


public class RightBar extends SideBar {

	public RightBar(IObjectRegistry registry) {
		super(registry);
	}
	
	protected String getDefaultClass(BuildSession session, ViewContext context){
		return DEFAULT_CLASS + " item-rightbar";
	}

	@Override
	protected TYPES getBarType() {
		return TYPES.RIGHT;
	}

}
