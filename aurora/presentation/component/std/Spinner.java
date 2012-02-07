package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.InputFieldConfig;
import aurora.presentation.component.std.config.SpinnerConfig;

public class Spinner extends TextField {
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		super.onPreparePageContent(session, context);
		addStyleSheet(session, context, "spinner/Spinner.css");
		addJavaScript(session, context, "spinner/Spinner.js");
	}
	public void onCreateViewContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onCreateViewContent(session, context);
		Map map = context.getMap();
		Integer width = (Integer)map.get(ComponentConfig.PROPERTITY_WIDTH);
		map.put(InputFieldConfig.PROPERTITY_INPUTWIDTH, new Integer(width.intValue()-23));
		
		SpinnerConfig sc = SpinnerConfig.getInstance(context.getView());

		if (null != sc.getMin())
			addConfig(SpinnerConfig.PROPERTITY_MIN, sc.getMin());
		if (null != sc.getMax())
			addConfig(SpinnerConfig.PROPERTITY_MAX, sc.getMax());
		addConfig(SpinnerConfig.PROPERTITY_STEP, sc.getStep());

		map.put(INPUT_TYPE, DEFAULT_INPUT_TYPE);
		map.put(CONFIG, getConfigString());
	}
}
