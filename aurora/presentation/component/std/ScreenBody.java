package aurora.presentation.component.std;

import java.io.IOException;

import uncertain.composite.CompositeMap;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;
import aurora.presentation.component.std.config.ComponentConfig;

public class ScreenBody extends VBox {
	public static final String VERSION = "$Revision$";
	
	public void buildView(BuildSession session, ViewContext view_context) throws IOException, ViewCreationException {
		CompositeMap view = view_context.getView();
		view.put(ComponentConfig.PROPERTITY_STYLE, "margin:5px;");
		super.buildView(session, view_context);
	}
}
