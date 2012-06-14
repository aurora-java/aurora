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

	public void onPreparePageContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onPreparePageContent(session, context);
		addJavaScript(session, context, "base/iscroll.js");
	}

	protected String getDefaultClass(BuildSession session, ViewContext context) {
		return DEFAULT_CLASS;
	}

	public void onCreateViewContent(BuildSession session, ViewContext context)
			throws IOException {
		Map map = context.getMap();
		CompositeMap view = context.getView();
		id = view.getString(ComponentConfig.PROPERTITY_ID, "");
		if ("".equals(id))
			id = IDGenerator.getInstance().generate();
		super.onCreateViewContent(session, context);
		addConfig(ComponentConfig.PROPERTITY_ID, id);
		map.put("_id", id);
		map.put(CONFIG, getConfigString());
	}
}
