package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

public class AccordionMenu extends Component {
	
	public AccordionMenu(IObjectRegistry registry) {
		super(registry);
	}

	public static final String VERSION = "$Revision$";
	
	private static final String PROPERTY_DISPLAY_FIELD = "displayfield";
	private static final String PROPERTY_MIN_HEIGHT = "minheight";
	private static final String PROPERTY_PARENT_FIELD = "parentfield";
	private static final String PROPERTY_ID_FIELD = "idfield";
	private static final String PROPERTY_SEQUENCE_FIELD = "sequencefield";
	private static final String PROPERTY_ICON = "icon";
	private static final String PROPERTY_INFO = "information";

	private static final String MENU_ID = "menu_id";
	private static final String SUBMENU_ID = "submenu_id";

	public static final String CONFIG_CONTEXT = "context";

	public void onPreparePageContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onPreparePageContent(session, context);
		addStyleSheet(session, context, "accordionmenu/AccordionMenu-min.css");
		addJavaScript(session, context, "accordionmenu/AccordionMenu-min.js");
	}

	public void onCreateViewContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onCreateViewContent(session, context);
		Map map = context.getMap();
		CompositeMap view = context.getView();
		CompositeMap model = context.getModel();
		String displayField = view.getString(PROPERTY_DISPLAY_FIELD);
		String parentField = view.getString(PROPERTY_PARENT_FIELD);
		String idField = view.getString(PROPERTY_ID_FIELD);
		String sequence = view.getString(PROPERTY_SEQUENCE_FIELD);
		String icon = uncertain.composite.TextParser.parse(
				view.getString(PROPERTY_ICON), model);
		if (null == icon)
			icon = "";
		String info = uncertain.composite.TextParser.parse(
				view.getString(PROPERTY_INFO), model);
		if (null != info)
			map.put(PROPERTY_INFO, info);
		String minHeight = view.getString(PROPERTY_MIN_HEIGHT);
		if (null != minHeight && !minHeight.isEmpty())
			map.put(PROPERTY_MIN_HEIGHT, "min-height:" + minHeight + "px");
		CompositeMap params = model.getRoot();
		String menuID = (String) params.getObject("/parameter/@menu_id");
		if (menuID == null)
			menuID = "";
		String subMenuID = (String) params.getObject("/parameter/@submenu_id");
		if (subMenuID == null)
			subMenuID = "";
		addConfig(MENU_ID, menuID);
		addConfig(SUBMENU_ID, subMenuID);
		addConfig(PROPERTY_PARENT_FIELD, parentField);
		addConfig(PROPERTY_ID_FIELD, idField);
		addConfig(PROPERTY_DISPLAY_FIELD, displayField);
		addConfig(PROPERTY_ICON, icon);
		addConfig(PROPERTY_SEQUENCE_FIELD, sequence);
		map.put(CONFIG, getConfigString());
	}
}
