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
import aurora.presentation.component.std.config.AccordionConfig;
import aurora.presentation.component.std.config.ComponentConfig;

public class Accordion extends Component {
	
	public Accordion(IObjectRegistry registry) {
		super(registry);
	}

	public static final String VERSION = "$Revision$";
	
	private static final String DEFAULT_CLASS = "layout-accordion";
	private static final String SELECTED = "selected";
	private static final String REF = "ref";
	
	private Integer bodyHeight;
	private int stripHeight = 25;

	protected int getDefaultHeight() {
		return -1;
	}
	
	protected String getDefaultClass(BuildSession session, ViewContext context) {
		return DEFAULT_CLASS;
	}

	public void onPreparePageContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onPreparePageContent(session, context);
		addStyleSheet(session, context, "accordion/Accordion-min.css");
		addJavaScript(session, context, "accordion/Accordion-min.js");
	}

	public void onCreateViewContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onCreateViewContent(session, context);
		//CompositeMap view = context.getView();
		Map map = context.getMap();
		addConfig("stripheight", new Integer(stripHeight));
		map.put(AccordionConfig.PROPERTITY_ACCORDIONS, createAccordions(session, context));
		map.put(CONFIG, getConfigString());
	}

	private String createAccordions(BuildSession session, ViewContext context)
			throws IOException {
		CompositeMap view = context.getView();
		AccordionConfig ac = AccordionConfig.getInstance(view);
		Map map = context.getMap();
		CompositeMap accordions = ac.getAccordions();
		StringBuffer sb = new StringBuffer();
		JSONArray jsons = new JSONArray();
		if (null != accordions) {
			List childs = accordions.getChilds();
			if (null != childs) {
				int numAccordions = childs.size();
				int i = 0;
				boolean isSelected = false, singleMode = ac.isSingleMode(),showIcon = ac.isShowIcon();
				int height = ((Integer) map
						.get(ComponentConfig.PROPERTITY_HEIGHT)).intValue();
				if(height>0){
					bodyHeight = new Integer((height - numAccordions
							* stripHeight)
							/ (singleMode ? 1 : numAccordions));
				}
				Iterator it = childs.iterator();
				addConfig(AccordionConfig.PROPERTITY_SINGLE_MODE, new Boolean(singleMode));
				addConfig(AccordionConfig.PROPERTITY_SHOW_ICON, new Boolean(showIcon));
				while (it.hasNext()) {
					CompositeMap accordion = (CompositeMap) it.next();
					if ((isSelected == false || singleMode == false)
							&& "true".equals(accordion.getString(SELECTED, ""))) {
						sb.append(createAccordion(session, context, accordion,
								isSelected = true,showIcon));
					} else
						sb.append(createAccordion(session, context, accordion,
								false,showIcon));
					jsons.put(new JSONObject(accordion));
					i++;
				}
			}
		}
		addConfig("items", jsons);
		return sb.toString();
	}

	private String createAccordion(BuildSession session, ViewContext context,
			CompositeMap accordion, boolean isSelected,boolean showIcon) throws IOException {
		CompositeMap model = context.getModel();
		StringBuffer sb = new StringBuffer();
		sb.append("<DIV class='" + (isSelected ? "item-accordion selected" : "item-accordion") + "' style='height:"
				+  stripHeight + "px'><DIV class='accordion-strip'>");
		if(showIcon)sb.append("<div class='item-accordion-btn'></div>");
		String prompt = uncertain.composite.TextParser.parse(session.getLocalizedPrompt(accordion
				.getString(ComponentConfig.PROPERTITY_PROMPT)),model);
		if(null!=prompt)sb.append(prompt);
		sb.append("</DIV><DIV class='item-accordion-body' hideFocus tabIndex='-1' style='");
		if(bodyHeight!=null)sb.append("height:"+ bodyHeight + "px;");
		sb.append((isSelected?"":"visibility:hidden")+"'>");
		String ref = accordion.getString(REF, "");
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
						throw new IOException(e);
					}
				}
			} else if (null!=accordion.getText() && !"".equals(accordion.getText())) {
				sb.append(accordion.getText());
			}
		}
		accordion.putString(REF, uncertain.composite.TextParser
				.parse(ref, model));
		sb.append("</DIV></DIV>");
		return sb.toString();
	}
}
