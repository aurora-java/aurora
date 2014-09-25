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
import aurora.presentation.component.std.config.SwitchCardConfig;

public class SwitchCard extends Component {

	public SwitchCard(IObjectRegistry registry) {
		super(registry);
	}

	public static final String VERSION = "$Revision$";
	
	private static final String DEFAULT_CLASS = "layout-switchcard";
	private static final String REF = "ref";
	private static final String VALUE = "value";
	private static final String HIDDEN = "hidden";

	protected String getDefaultClass(BuildSession session, ViewContext context) {
		return DEFAULT_CLASS;
	}

	public void onPreparePageContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onPreparePageContent(session, context);
		addJavaScript(session, context, "switchcard/SwitchCard-min.js");
	}

	public void onCreateViewContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onCreateViewContent(session, context);
		Map map = context.getMap();
		map.put(SwitchCardConfig.PROPERTITY_CARDS,
				createCards(session, context));
		map.put(CONFIG, getConfigString());
	}

	private String createCards(BuildSession session, ViewContext context)
			throws IOException {
		CompositeMap view = context.getView();
		SwitchCardConfig pc = SwitchCardConfig.getInstance(view);
		Map map = context.getMap();
		CompositeMap portals = pc.getCards();
		StringBuffer sb = new StringBuffer();
		JSONArray jsons = new JSONArray();
		if (null != portals) {
			List childs = portals.getChilds();
			if (null != childs) {
				Iterator it = childs.iterator();
				while (it.hasNext()) {
					CompositeMap card = (CompositeMap) it.next();
					sb.append(createCard(session, context, card));
					jsons.put(new JSONObject(card));
				}
			}
		}
		addConfig("items", jsons);
		return sb.toString();
	}

	private StringBuffer createCard(BuildSession session, ViewContext context,
			CompositeMap card) throws IOException {
		CompositeMap model = context.getModel();
		StringBuffer sb = new StringBuffer();
		String ref = card.getString(REF, "");
		String value = card.getString(VALUE, "");
		boolean hidden = card.getBoolean(HIDDEN, true);
		sb.append("<div class='switchcard-body' style='display:"+(hidden?"none":"")+"' case='"+value+"' url='"+ref+"'");
		if ("".equals(ref)) {
			String hostid =  IDGenerator.getInstance().generate();
			sb.append(" id='"+hostid+"'>");
			List cardChilds = card.getChilds();
			if (cardChilds != null) {
				Iterator it = cardChilds.iterator();
				while (it.hasNext()) {
					CompositeMap cardChild = (CompositeMap) it.next();
					cardChild.putString(SwitchCardConfig.PROPERTITY_HOST_ID, hostid);
					try {
						sb.append(session.buildViewAsString(model, cardChild));
					} catch (Exception e) {
						throw new IOException(e);
					}
				}
			} else if (null != card.getText() && !"".equals(card.getText())) {
				sb.append(card.getText());
			}
		}else{
			sb.append(">");
		}
		card.putString(REF, uncertain.composite.TextParser.parse(ref, model));
		sb.append("</div>");
		return sb;
	}
}
