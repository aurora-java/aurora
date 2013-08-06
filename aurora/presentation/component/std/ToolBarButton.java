package aurora.presentation.component.std;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.ToolBarButtonConfig;

public class ToolBarButton extends Button {
	
	public ToolBarButton(IObjectRegistry registry) {
		super(registry);
	}

	public static final String VERSION = "$Revision$";
	private static final String DEFAULT_CLASS = " item-rbtn ";
	private static final String DEFAULT_THEME = "item-rbtn-blue";
	
	protected String getDefaultClass(BuildSession session, ViewContext context){
		CompositeMap view = context.getView();
		ToolBarButtonConfig tbc = ToolBarButtonConfig.getInstance(view);
		String className = tbc.getClassName("");
		if("".equals(className)){
			view.putString(ComponentConfig.PROPERTITY_CLASSNAME, DEFAULT_THEME);
		}
		return super.getDefaultClass(session, context) + DEFAULT_CLASS;
	}
	
	protected int getDefaultHeight(){
		return 22;
	}
}
