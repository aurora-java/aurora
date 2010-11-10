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
	public static final String CONFIG_CONTEXT = "context";

	public static void main(String[] args) {
		JFrame jf=new JFrame();
		JMenuBar jbm=new JMenuBar();
		JMenu menu1=new JMenu("AAAAA");
		JMenu menu2=new JMenu("BBBBB");
		JRadioButtonMenuItem menu11=new JRadioButtonMenuItem("AAAAB");
		JRadioButtonMenuItem menu12=new JRadioButtonMenuItem("AAAAC");
		ButtonGroup group=new ButtonGroup();
		group.add(menu11);group.add(menu12);
		JCheckBoxMenuItem menu13=new JCheckBoxMenuItem("AAAAD");
		JMenuItem menu21=new JMenuItem("BBBBA");
		JMenuItem menu22=new JMenuItem("BBBBB");
		jbm.add(menu1).add(menu11);
		menu1.add(menu12);
		menu1.addSeparator();
		menu1.add(menu13);
		menu1.add(menu2).add(menu21);
		menu2.add(menu22);
		jf.setJMenuBar(jbm);
		jf.setSize(300, 300);
		jf.setBackground(Color.GRAY);
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
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
		addConfig(PROPERTITY_ROOT_ID, view.getInt(PROPERTITY_ROOT_ID, -1));
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

		map.put(CONFIG, getConfigString());
	}
}
