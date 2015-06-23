package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.MenuBarConfig;

public class MenuBar extends Component {

	public MenuBar(IObjectRegistry registry) {
		super(registry);
	}

	public static final String VERSION = "$Revision$";
	
	
	public static final String CONFIG_CONTEXT = "context";
	private static final String DEFAULT_CLASS = "item-menu-bar";
	
	protected String getDefaultClass(BuildSession session, ViewContext context) {
		return DEFAULT_CLASS;
	}
	
	protected int getDefaultWidth(){
		return -1;
	}
	protected int getDefaultHeight(){
		return -1;
	}
	public void onPreparePageContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onPreparePageContent(session, context);
		addStyleSheet(session, context, "menu/Menu-min.css");
		addJavaScript(session, context, "menu/Menu-min.js");
	}

	public void onCreateViewContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onCreateViewContent(session, context);
		Map map = context.getMap();
		CompositeMap view = context.getView();
		MenuBarConfig mbc = MenuBarConfig.getInstance(view);
		map.put(ComponentConfig.PROPERTITY_BINDTARGET, mbc.getBindTarget());
		addConfig(MenuBarConfig.PROPERTITY_FIELD_DISPLAY, mbc.getDisplayField());
		if (null != mbc.getRenderer())
			addConfig(MenuBarConfig.PROPERTITY_RENDERER, mbc.getRenderer());
		addConfig(MenuBarConfig.PROPERTITY_FIELD_ID, mbc.getIdField());
		addConfig(MenuBarConfig.PROPERTITY_FIELD_PARENT, mbc.getParentField());
		addConfig(MenuBarConfig.PROPERTITY_ROOT_ID,
				new Integer(mbc.getRootId()));
		if (session.getContextPath() != null)
			addConfig(CONFIG_CONTEXT, session.getContextPath());
		if (null != mbc.getFocus())
			addConfig(MenuBarConfig.PROPERTITY_FOCUS, mbc.getFocus());
		addConfig(MenuBarConfig.PROPERTITY_FIELD_ICON, mbc.getIconField());
		addConfig(MenuBarConfig.PROPERTITY_FIELD_SEQUENCE, mbc
				.getSequenceField());
		if (null != mbc.getMenuType())
			addConfig(MenuBarConfig.PROPERTITY_MENU_TYPE, mbc.getMenuType());
		if (null != mbc.getURLTarget())
			addConfig(MenuBarConfig.PROPERTITY_URL_TARGET, mbc.getURLTarget());
		addConfig(MenuBarConfig.PROPERTITY_FIELD_URL, mbc.getURLField());
		map.put(CONFIG, getConfigString());
	}
}
