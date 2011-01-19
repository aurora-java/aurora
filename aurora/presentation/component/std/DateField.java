package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;

/**
 * 日历组件.
 * 
 * @version $Id: DateField.java v 1.0 2011-1-17 下午01:06:13 znjqolf Exp $
 * @author <a href="mailto:hugh.hz.wu@gmail.com">Hugh</a>
 */
public class DateField extends Component {

	private static final String PROPERTITY_ENABLE_MONTH_BTN = "enablemonthbtn";
	private static final String PROPERTITY_ENABLE_BESIDE_DAYS = "enablebesidedays";
	private static final String DEFAULT_CLASS = "item-dateField";

	protected String getDefaultClass(BuildSession session, ViewContext context) {
		return DEFAULT_CLASS;
	}

	public void onCreateViewContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onCreateViewContent(session, context);
		Map map = context.getMap();
		CompositeMap view = context.getView();
		addConfig(PROPERTITY_ENABLE_MONTH_BTN, view.getString(
				PROPERTITY_ENABLE_MONTH_BTN, "both"));
		addConfig(PROPERTITY_ENABLE_BESIDE_DAYS, view.getString(
				PROPERTITY_ENABLE_BESIDE_DAYS, "both"));
		map.remove(ComponentConfig.PROPERTITY_HEIGHT);
		map.put(CONFIG, getConfigString());
	}
}
