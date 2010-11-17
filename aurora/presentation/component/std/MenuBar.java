package aurora.presentation.component.std;

import java.awt.Color;
import java.io.IOException;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;

public class MenuBar extends Component {

	public static final String PROPERTITY_RENDERER = "renderer";
	public static final String PROPERTITY_FIELD_ID = "idfield";
	public static final String PROPERTITY_FIELD_PARENT = "parentfield";
	public static final String PROPERTITY_FIELD_DISPLAY = "displayfield";
	public static final String PROPERTITY_FIELD_ICON = "iconfield";
	public static final String PROPERTITY_SEQUENCE = "sequence";
	public static final String PROPERTITY_FOCUS = "focus";
	public static final String PROPERTITY_MENU_TYPE = "menutype";
	public static final String PROPERTITY_ROOT_ID = "rootid";
	public static final String PROPERTITY_TARGET = "targetname";
	public static final String PROPERTITY_URL = "url";
	public static final String CONFIG_CONTEXT = "context";

	public void onPreparePageContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onPreparePageContent(session, context);
		addStyleSheet(session, context, "menu/Menu.css");
		addJavaScript(session, context, "menu/Menu.js");
	}

	public void onCreateViewContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onCreateViewContent(session, context);
		Map map = context.getMap();
		CompositeMap view = context.getView();

		map.put(ComponentConfig.PROPERTITY_BINDTARGET, view
				.getString(ComponentConfig.PROPERTITY_BINDTARGET));
		addConfig(PROPERTITY_FIELD_DISPLAY, view.getString(
				PROPERTITY_FIELD_DISPLAY, "name"));
		addConfig(PROPERTITY_RENDERER, view.getString(PROPERTITY_RENDERER, ""));
		addConfig(PROPERTITY_FIELD_ID, view
				.getString(PROPERTITY_FIELD_ID, "id"));
		addConfig(PROPERTITY_FIELD_PARENT, view.getString(
				PROPERTITY_FIELD_PARENT, "pid"));
		addConfig(PROPERTITY_ROOT_ID, new Integer(view.getInt(PROPERTITY_ROOT_ID, -1)));
		if (session.getContextPath() != null)
			addConfig(CONFIG_CONTEXT, session.getContextPath());
		if (null != view.getString(PROPERTITY_FOCUS))
			addConfig(PROPERTITY_FOCUS, view.getString(PROPERTITY_FOCUS));
		if (null != view.getString(PROPERTITY_FIELD_ICON))
			addConfig(PROPERTITY_FIELD_ICON, view
					.getString(PROPERTITY_FIELD_ICON));
		if (null != view.getString(PROPERTITY_SEQUENCE))
			addConfig(PROPERTITY_SEQUENCE, view.getString(PROPERTITY_SEQUENCE));
		if (null != view.getString(PROPERTITY_MENU_TYPE))
			addConfig(PROPERTITY_MENU_TYPE, view.getString(PROPERTITY_MENU_TYPE));
		if (null != view.getString(PROPERTITY_TARGET))
			addConfig(PROPERTITY_TARGET, view.getString(PROPERTITY_TARGET));
		if (null != view.getString(PROPERTITY_URL))
			addConfig(PROPERTITY_URL, view.getString(PROPERTITY_URL));
		map.put(CONFIG, getConfigString());
	}
}
