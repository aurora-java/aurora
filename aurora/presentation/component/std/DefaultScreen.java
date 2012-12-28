package aurora.presentation.component.std;

import java.io.IOException;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;

public class DefaultScreen  extends VBox{
	
	public static final String VERSION = "$Revision$";
	
	
	public void buildView(BuildSession session, ViewContext view_context) throws IOException, ViewCreationException {
		CompositeMap view = view_context.getView();
		view.putString("style", "width:100%");
		super.buildView(session, view_context);
	}
	
}
