package aurora.presentation.component.std;

import java.io.IOException;

import uncertain.ocm.IObjectRegistry;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

@SuppressWarnings("unchecked")
public class DynamicTree extends Tree {
	
	public DynamicTree(IObjectRegistry registry) {
		super(registry);
	}

	public static final String VERSION = "$Revision$";
	
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		super.onPreparePageContent(session, context);
		addJavaScript(session, context, "tree/DynamicTree-min.js");
	}
	
}
