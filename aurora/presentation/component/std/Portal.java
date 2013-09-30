package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.PortalConfig;

public class Portal extends Component {

	public Portal(IObjectRegistry registry) {
		super(registry);
	}

	private static final String DEFAULT_CLASS = "layout-portal";
	private static final String REF = "ref";
	private int cellSpacing;
	private int blockHeight;
	private int blockWidth;

	protected String getDefaultClass(BuildSession session, ViewContext context) {
		return DEFAULT_CLASS;
	}

	public void onPreparePageContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onPreparePageContent(session, context);
		addStyleSheet(session, context, "portal/Portal-min.css");
		addJavaScript(session, context, "portal/Portal-min.js");
	}

	public void onCreateViewContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		Map map = context.getMap();
		PortalConfig pc = PortalConfig.getInstance(view);
		cellSpacing = pc.getCellSpacing();
		blockHeight = pc.getBlockHeight();
		blockWidth = pc.getBlockWidth();
		map.put(PortalConfig.PROPERTITY_CELLSPACING, cellSpacing);
		map.put(PortalConfig.PROPERTITY_PORTALS,
				createPortals(session, context));
		map.put(CONFIG, getConfigString());
	}

	private String createPortals(BuildSession session, ViewContext context)
			throws IOException {
		CompositeMap view = context.getView();
		PortalConfig pc = PortalConfig.getInstance(view);
		Map map = context.getMap();
		CompositeMap portals = pc.getPortals();
		StringBuffer sb = new StringBuffer();
		sb.append("<div style='padding:0 0 "+cellSpacing+"px "+cellSpacing+"px;display:table;table-layout:fixed;width:"+(((Integer)map.get(ComponentConfig.PROPERTITY_WIDTH)).intValue()-cellSpacing)+"px'>");
		JSONArray jsons = new JSONArray();
		if (null != portals) {
			List childs = portals.getChilds();
			if (null != childs) {
				Iterator it = childs.iterator();
				while (it.hasNext()) {
					CompositeMap portal = (CompositeMap) it.next();
					sb.append(createPortal(session, context, portal));
					jsons.put(new JSONObject(portal));
				}
			}
		}
		addConfig("items", jsons);
		sb.append("<div class='portal-proxy' style='width:" + (blockWidth - 8)
				+ "px;height:" + (blockHeight - 8) + "px;margin:"
				+ cellSpacing + "px " + cellSpacing + "px 0 0;display:none'></div>");
		sb.append("</div>");
		return sb.toString();
	}

	private StringBuffer createPortal(BuildSession session,
			ViewContext context, CompositeMap portal) throws IOException {
		CompositeMap model = context.getModel();
		StringBuffer sb = new StringBuffer();
		String prompt = uncertain.composite.TextParser.parse(session
				.getLocalizedPrompt(portal
						.getString(ComponentConfig.PROPERTITY_PROMPT)), model);
		sb.append("<table class='portal-item-wrap' style='width:"
				+ blockWidth
				+ "px;height:"
				+ blockHeight
				+ "px;margin:"
				+ cellSpacing
				+ "px "
				+ cellSpacing
				+ "px 0 0;outline:none' cellSpacing='0' cellPadding='0' hideFocus tabIndex='-1' border='0'>");
		sb.append("<thead>");
		sb.append("<tr style='height:23px;' >");
		sb.append("<td class='portal-item-caption'>");
		sb.append("<table style='height:23px;' cellspacing='0' class='portal-item-cap' unselectable='on'  onselectstart='return false;' cellpadding='0' width='100%' border='0' unselectable='on'>");
		sb.append("<tr>");
		sb.append("<td unselectable='on' class='portal-item-caption-label' width='99%'>");
		sb.append("<div unselectable='on' unselectable='on'>");
		if (null != prompt)
			sb.append(prompt);
		sb.append("</div>");
		sb.append("</td>");
		sb.append("<td unselectable='on' class='portal-item-caption-button' nowrap>");
		sb.append("<div class='portal-item-close' unselectable='on'></div>");
		sb.append("</td>");
		sb.append("<td><div style='width:5px;'/></td>");
		sb.append("</tr>");
		sb.append("</table>");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("</thead>");
		sb.append("<tbody>");
		sb.append("<tr>");
		sb.append("<td class='portal-item-body' valign='top' unselectable='on'>");
		sb.append("<div class='portal-item-content' style='height:" + (blockHeight - 26) + "px'>");
		String ref = portal.getString(REF, "");
		if ("".equals(ref)) {
			sb.append("<script>(function(){var a = window.__host = Ext.get(Ext.fly('"+this.id+"').select('.portal-item-content:last').elements[0]);if(!a.cmps)a.cmps={};})()</script>");
			List portalChilds = portal.getChilds();
			if (portalChilds != null) {
				Iterator it = portalChilds.iterator();
				while (it.hasNext()) {
					CompositeMap portalChild = (CompositeMap) it.next();
					try {
						sb.append(session.buildViewAsString(model, portalChild));
					} catch (Exception e) {
						throw new IOException(e);
					}
				}
			} else if (null != portal.getText() && !"".equals(portal.getText())) {
				sb.append(portal.getText());
			}
			sb.append("<script>window.__host = null</script>");
		}
		portal.putString(REF, uncertain.composite.TextParser.parse(ref, model));
		sb.append("</div>");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("</tbody>");
		sb.append("</table>");
		return sb;
	}
}
