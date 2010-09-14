package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;

public class TreeGrid extends Grid {

	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		super.onPreparePageContent(session, context);
		addStyleSheet(session, context, "grid/Grid-min.css");
		addStyleSheet(session, context, "tree/Tree.css");
		addJavaScript(session, context, "tree/Tree.js");
		addJavaScript(session, context, "treegrid/TreeGrid.js");
	}
	
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
		super.onCreateViewContent(session, context);
		Map map = context.getMap();
		CompositeMap view = context.getView();
		
		if(session.getContextPath()!=null) addConfig(Tree.CONFIG_CONTEXT,session.getContextPath()+"/");
		addConfig(Tree.PROPERTITY_FIELD_DISPLAY, view.getString(Tree.PROPERTITY_FIELD_DISPLAY,"name"));
		addConfig(Tree.PROPERTITY_RENDERER, view.getString(Tree.PROPERTITY_RENDERER,""));
		addConfig(Tree.PROPERTITY_FIELD_ID, view.getString(Tree.PROPERTITY_FIELD_ID,"id"));
		addConfig(Tree.PROPERTITY_FIELD_PARENT, view.getString(Tree.PROPERTITY_FIELD_PARENT,"pid"));
		addConfig(Tree.PROPERTITY_SHOWCHECKBOX, new Boolean(view.getBoolean(Tree.PROPERTITY_SHOWCHECKBOX, false)));
		map.put(CONFIG, getConfigString());
	}
}
