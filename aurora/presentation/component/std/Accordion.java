package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;

public class Accordion extends Component {

	private static final String DEFAULT_CLASS = "layout-accordion";

	private static final String ACCORDIONS = "accordions";
	private static final String SINGLE_MODE = "singlemode";
	private static final String PROPERTITY_REF = "ref";
	private static final String PROPERTITY_SELECTED = "selected";

	private int bodyHeight;
	private int bodyWidth;
	private int stripHeight = 25;

	protected String getDefaultClass(BuildSession session, ViewContext context) {
		return DEFAULT_CLASS;
	}

	public void onPreparePageContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onPreparePageContent(session, context);
		addStyleSheet(session, context, "accordion/Accordion.css");
		addJavaScript(session, context, "accordion/Accordion.js");
	}

	public void onCreateViewContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		Map map = context.getMap();
		addConfig("stripheight", new Integer(stripHeight));
		map.put(ACCORDIONS, createAccordions(session, context));
		map.put(CONFIG, getConfigString());
	}

	private String createAccordions(BuildSession session, ViewContext context)
			throws IOException {
		CompositeMap view = context.getView();
		Map map = context.getMap();
		CompositeMap accordions = view.getChild(ACCORDIONS);
		StringBuffer sb = new StringBuffer();
		JSONArray jsons = new JSONArray();
		if (null != accordions) {
			List childs = accordions.getChilds();
			if (null != childs) {
				int numAccordions = childs.size();
				int i = 0;
				boolean isSelected = false, singleMode = view.getBoolean(
						SINGLE_MODE, true);
				bodyHeight = (((Integer) map
						.get(ComponentConfig.PROPERTITY_HEIGHT)).intValue() - numAccordions
						* stripHeight)
						/ (singleMode ? 1 : numAccordions);
				bodyWidth = ((Integer) map
						.get(ComponentConfig.PROPERTITY_WIDTH)).intValue();
				Iterator it = childs.iterator();
				addConfig(SINGLE_MODE, new Boolean(singleMode));
				while (it.hasNext()) {
					CompositeMap accordion = (CompositeMap) it.next();
					if ((isSelected == false || singleMode == false)
							&& "true".equals(accordion.getString(
									PROPERTITY_SELECTED, ""))) {
						sb.append(createAccordion(session, context, accordion,
								isSelected = true));
					} else
						sb.append(createAccordion(session, context, accordion,
								false));
					jsons.put(new JSONObject(accordion));
					i++;
				}
			}
		}
		addConfig("items", jsons);
		return sb.toString();
	}

	private String createAccordion(BuildSession session, ViewContext context,
			CompositeMap accordion, boolean isSelected) throws IOException {
		CompositeMap model = context.getModel();
		int accordionHeight = isSelected ? bodyHeight + stripHeight
				: stripHeight;
		String stripClass = isSelected ? "item-accordion selected"
				: "item-accordion";
		StringBuffer sb = new StringBuffer();
		sb.append("<TR><TD><DIV class='" + stripClass + "' style='height:"
				+ accordionHeight + "px'><DIV class='strip' style='height:"
				+ stripHeight + "px;width:" + bodyWidth + "px;line-height:"
				+ stripHeight + "px'>");
		sb.append(session.getLocalizedPrompt(accordion
				.getString(ComponentConfig.PROPERTITY_PROMPT)));
		sb.append("</DIV><DIV class='item-accordion-body' style='height:"
				+ bodyHeight + "px;width:" + bodyWidth + "px'>");
		String ref = accordion.getString(PROPERTITY_REF, "");
		if ("".equals(ref)) {
			List accordionChilds = accordion.getChilds();
			if (accordionChilds != null) {
				Iterator it = accordionChilds.iterator();
				while (it.hasNext()) {
					CompositeMap accordionChild = (CompositeMap) it.next();
					try {
						sb.append(session.buildViewAsString(model,
								accordionChild));
					} catch (Exception e) {
						throw new IOException(e.getMessage());
					}
				}
			} else if (!"".equals(accordion.getText())) {
				sb.append(accordion.getText());
			}
		}
		accordion.putString(PROPERTITY_REF, uncertain.composite.TextParser
				.parse(ref, model));
		sb.append("</DIV></DIV></TD></TR>");
		return sb.toString();
	}
}
