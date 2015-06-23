package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.ocm.IObjectRegistry;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.MenuTreeConfig;

public class MenuTree extends Component {

	public MenuTree(IObjectRegistry registry) {
		super(registry);
	}

	public static final String VERSION = "$Revision$";
	
	
	public static final String CONFIG_CONTEXT = "context";
	private static final String DEFAULT_CLASS = "item-menu-tree";
	
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
		addStyleSheet(session, context, "menutree/MenuTree-min.css");
		addJavaScript(session, context, "menutree/MenuTree-min.js");
	}

	public void onCreateViewContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onCreateViewContent(session, context);
		Map map = context.getMap();
		CompositeMap view = context.getView();
		CompositeMap model = context.getModel();
		MenuTreeConfig mtc = MenuTreeConfig.getInstance(view);
//		String contextPath = session.getContextPath();
//		if (null != contextPath)
//			addConfig(CONFIG_CONTEXT, contextPath);
		if(mtc.isShowRoot()){
			createRootIcon(mtc,map,model);
		}
		map.put(ComponentConfig.PROPERTITY_BINDTARGET, mtc.getBindTarget());
		addConfig(MenuTreeConfig.PROPERTITY_FIELD_DISPLAY, mtc.getDisplayField());
		if (null != mtc.getRenderer())
			addConfig(MenuTreeConfig.PROPERTITY_RENDERER, mtc.getRenderer());
		addConfig(MenuTreeConfig.PROPERTITY_FIELD_ID, mtc.getIdField());
		addConfig(MenuTreeConfig.PROPERTITY_FIELD_PARENT, mtc.getParentField());
		addConfig(MenuTreeConfig.PROPERTITY_FIELD_SEQUENCE, mtc
				.getSequenceField());
		map.put(CONFIG, getConfigString());
	}
	
	private void createRootIcon(MenuTreeConfig mtc,Map map,CompositeMap model){
		String icon = TextParser.parse(mtc.getRootIcon(), model);
		String iconwidth = mtc.getIconWidth();
		String iconheight = mtc.getIconHeight();
		StringBuffer sb = new StringBuffer("<thead>");
		sb.append("<tr>");
		sb.append("<td width='"+iconwidth+"'>");
		sb.append("<img border='0' width='"+iconwidth+"' height='"+iconheight+"' src='"+icon+"' class='menu-tree-icon' usemap='"+mtc.getIconMap()+"'></img>");
		sb.append("</td>");
		sb.append("<td></td>");
		sb.append("</tr>");
		sb.append("</thead>");
		map.put("rooticon", sb.toString());
		sb = new StringBuffer("<td valign='top'>");
		sb.append("<div style='margin-left:"+mtc.getRootLineOffset()+"px' class='menu-tree-line-left-bottom'>");
		sb.append("</td>");
		map.put("rootline", sb.toString());
	}
}
