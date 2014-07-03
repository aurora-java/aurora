package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class SwitchCardConfig extends ComponentConfig {

	public static final String VERSION = "$Revision$";

	public static final String TAG_NAME = "switchCard";

	public static final String PROPERTITY_CARDS = "cards";

	public static SwitchCardConfig getInstance() {
		SwitchCardConfig model = new SwitchCardConfig();
		model.initialize(GridConfig.createContext(null, TAG_NAME));
		return model;
	}

	public static SwitchCardConfig getInstance(CompositeMap context) {
		SwitchCardConfig model = new SwitchCardConfig();
		model.initialize(GridConfig.createContext(context, TAG_NAME));
		return model;
	}

	public CompositeMap getCards() {
		return getObjectContext().getChild(PROPERTITY_CARDS);
	}

}
