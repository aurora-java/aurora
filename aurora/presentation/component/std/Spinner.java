package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.ocm.IObjectRegistry;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.InputFieldConfig;
import aurora.presentation.component.std.config.SpinnerConfig;

public class Spinner extends NumberField {
	
	public Spinner(IObjectRegistry registry) {
		super(registry);
	}
	public static final String VERSION = "$Revision$";
	public void onCreateViewContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onCreateViewContent(session, context);
		Map map = context.getMap();
		CompositeMap model = context.getModel();
		Integer width = (Integer) map.get(ComponentConfig.PROPERTITY_WIDTH);
		map.put(InputFieldConfig.PROPERTITY_INPUTWIDTH, new Integer(width
				.intValue() - 23));

		SpinnerConfig sc = SpinnerConfig.getInstance(context.getView());

		addConfig(SpinnerConfig.PROPERTITY_STEP, TextParser.parse(sc.getStep(),model));

		map.put(INPUT_TYPE, DEFAULT_INPUT_TYPE);
		map.put(CONFIG, getConfigString());
	}
}
