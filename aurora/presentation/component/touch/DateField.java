package aurora.presentation.component.touch;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.IDGenerator;
import aurora.presentation.component.std.config.ComponentConfig;

public class DateField extends Component {
	private static final String DEFAULT_CLASS = "datefield";
	private static final String PROPERTITY_DAY_RENDERER = "dayrenderer";
	private static final String PROPERTITY_V_ALIGN = "valign";
	
	public void onPreparePageContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onPreparePageContent(session, context);
		addJavaScript(session, context, "base/iscroll-min.js");
	}

	protected String getDefaultClass(BuildSession session, ViewContext context) {
		return DEFAULT_CLASS;
	}

	public void onCreateViewContent(BuildSession session, ViewContext context)
			throws IOException {
		Map map = context.getMap();
		CompositeMap view = context.getView();
		CompositeMap model = context.getModel();
		id = view.getString(ComponentConfig.PROPERTITY_ID, "");
		if ("".equals(id))
			id = IDGenerator.getInstance().generate();
		super.onCreateViewContent(session, context);
		addConfig(ComponentConfig.PROPERTITY_ID, id);
		String renderer = view.getString(PROPERTITY_DAY_RENDERER,"");
		if(!"".equals(renderer)){
			addConfig(PROPERTITY_DAY_RENDERER, new JSONFunction(renderer));
		}
		addConfig(PROPERTITY_V_ALIGN, view.getString(PROPERTITY_V_ALIGN));
		addEvents(view, model);
		map.put("_id", id);
		map.put(CONFIG, getConfigString());
	}
}
