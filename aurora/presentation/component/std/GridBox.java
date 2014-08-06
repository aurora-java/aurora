package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import aurora.application.ApplicationViewConfig;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.GridBoxConfig;

@SuppressWarnings("unchecked")
public class GridBox extends Component {
	
	public static final String VERSION = "$Revision$";
	private static final String DEFAULT_CLASS = "item-gridbox-wrap";
	
	public GridBox(IObjectRegistry registry) {
		super(registry);
    }
	
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		super.onPreparePageContent(session, context);
		addStyleSheet(session, context, "gridbox/GridBox-min.css");
		addJavaScript(session, context, "gridbox/GridBox-min.js");
	}
	
	protected int getDefaultWidth() {
		return -1;
	}
	
	
	protected String getDefaultClass(BuildSession session, ViewContext context){
		return DEFAULT_CLASS;
	}
	
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{	
		super.onCreateViewContent(session, context);
		ApplicationViewConfig view_config = mApplicationConfig.getApplicationViewConfig();
		CompositeMap view = context.getView();
		CompositeMap model = context.getModel();
		Map map = context.getMap();
		GridBoxConfig gbc = GridBoxConfig.getInstance(view);
		String labelSeparator = gbc.getLabelSeparator()==null?view_config.getDefaultLabelSeparator():gbc.getLabelSeparator();
		processColumns(gbc.getColumns().getChilds());
		boolean underBox = gbc.getUnderBox();
		if(underBox)
			addConfig(GridBoxConfig.PROPERTITY_UNDERBOX, Boolean.TRUE);
		addConfig(GridBoxConfig.PROPERTITY_COLUMN,new Integer(gbc.getColumn()));
		int padding = gbc.getPadding(model,3);
		addConfig(GridBoxConfig.PROPERTITY_PADDING, new Integer(padding));
		addConfig(GridBoxConfig.PROPERTITY_TAB_INDEX, gbc.getTabIndex());
		addConfig(GridBoxConfig.PROPERTITY_LABEL_SEPARATOR, labelSeparator);
		createEditors(session, context);
		map.put(CONFIG, getConfigString());
	}
	
	private void processColumns(List children) {
		Iterator it = children.iterator();
		JSONArray jsons = new JSONArray();
		while (it.hasNext()) {
			CompositeMap column = (CompositeMap) it.next();
			jsons.put(new JSONObject(column));
		}
		addConfig(GridBoxConfig.PROPERTITY_COLUMNS, jsons);
	}
	
	private void createEditors(BuildSession session, ViewContext context)
	throws IOException {
		CompositeMap view = context.getView();
		Map map = context.getMap();
		CompositeMap model = context.getModel();
		CompositeMap editors = view.getChild(GridBoxConfig.PROPERTITY_EDITORS);
		StringBuffer sb = new StringBuffer();
		if (editors != null && editors.getChilds() != null) {
			Iterator it = editors.getChildIterator();
			while (it.hasNext()) {
				CompositeMap editor = (CompositeMap) it.next();
				editor.put(GridBoxConfig.PROPERTITY_TAB_INDEX, new Integer(-1));
				editor.put(GridBoxConfig.PROPERTITY_STYLE,
						"position:absolute;left:-1000px;top:-1000px;");
				try {
					sb.append(session.buildViewAsString(model, editor));
				} catch (Exception e) {
					throw new IOException(e);
				}
			}
		}
		map.put(GridBoxConfig.PROPERTITY_EDITORS, sb.toString());
		}
}
