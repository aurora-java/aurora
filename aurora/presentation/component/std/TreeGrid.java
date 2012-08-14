package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import aurora.bm.IModelFactory;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.TreeConfig;

public class TreeGrid extends Grid {

	public TreeGrid(IObjectRegistry registry, IModelFactory factory) {
		super(registry, factory);
	}


	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		super.onPreparePageContent(session, context);
		addStyleSheet(session, context, "grid/Grid-min.css");
		addStyleSheet(session, context, "tree/Tree-min.css");
		addStyleSheet(session, context, "treegrid/TreeGrid-min.css");
		addJavaScript(session, context, "tree/Tree-min.js");
		addJavaScript(session, context, "treegrid/TreeGrid-min.js");
	}
	
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
		super.onCreateViewContent(session, context);
		Map map = context.getMap();
		CompositeMap view = context.getView();
		TreeConfig tc = TreeConfig.getInstance(view);
		
		
		if(session.getContextPath()!=null) addConfig(Tree.CONFIG_CONTEXT,session.getContextPath()+"/");
		addConfig(TreeConfig.PROPERTITY_FIELD_DISPLAY, tc.getDisplayField());
		if(tc.getRenderer()!=null)addConfig(TreeConfig.PROPERTITY_RENDERER, tc.getRenderer());
		addConfig(TreeConfig.PROPERTITY_FIELD_ID, tc.getIdField());
		addConfig(TreeConfig.PROPERTITY_FIELD_PARENT, tc.getParentField());
		addConfig(TreeConfig.PROPERTITY_SHOWCHECKBOX,  new Boolean(tc.isShowCheckBox()));
		addConfig(TreeConfig.PROPERTITY_FIELD_CHECKED, tc.getCheckField());
		addConfig(TreeConfig.PROPERTITY_FIELD_EXPAND, tc.getExpandField());
		addConfig(TreeConfig.PROPERTITY_FIELD_SEQUENCE, tc.getSequenceField());
		map.put(CONFIG, getConfigString());
	}
}
