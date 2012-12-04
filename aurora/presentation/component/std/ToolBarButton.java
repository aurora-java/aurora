package aurora.presentation.component.std;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;

public class ToolBarButton extends Button {
	
	
	public static final String TAG_NAME = "toolbarButton";
	private static final String DEFAULT_CLASS = " item-rbtn ";
	private static final String DEFAULT_THEME = "item-rbtn-blue";
	
	protected String getDefaultClass(BuildSession session, ViewContext context){
		CompositeMap view = context.getView();
		String className = view.getString(ComponentConfig.PROPERTITY_CLASSNAME, "");
		if("".equals(className)){
			view.putString(ComponentConfig.PROPERTITY_CLASSNAME, DEFAULT_THEME);
		}
		String style = super.getDefaultClass(session, context);
		return style + DEFAULT_CLASS;
	}
	
	protected int getDefaultHeight(){
		return 22;
	}
}
