package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class AccordionConfig extends ComponentConfig {
	
	public static final String VERSION = "$Revision$";
	
	public static final String TAG_NAME = "accordionPanel";

	public static final String PROPERTITY_ACCORDIONS = "accordions";

	public static final String PROPERTITY_SINGLE_MODE = "singlemode";
	public static final String PROPERTITY_SHOW_ICON = "showicon";

	public static AccordionConfig getInstance() {
		AccordionConfig model = new AccordionConfig();
		model.initialize(GridConfig.createContext(null, TAG_NAME));
		return model;
	}

	public static AccordionConfig getInstance(CompositeMap context) {
		AccordionConfig model = new AccordionConfig();
		model.initialize(GridConfig.createContext(context, TAG_NAME));
		return model;
	}

	public CompositeMap getAccordions() {
		return getObjectContext().getChild(PROPERTITY_ACCORDIONS);
	}

	public boolean isSingleMode() {
		return getBoolean(PROPERTITY_SINGLE_MODE, true);
	}

	public void setSingleMode(boolean singleMode) {
		putBoolean(PROPERTITY_SINGLE_MODE, singleMode);
	}
	
	public boolean isShowIcon() {
		return getBoolean(PROPERTITY_SHOW_ICON, true);
	}
	
	public void setShowIcon(boolean showIcon) {
		putBoolean(PROPERTITY_SHOW_ICON, showIcon);
	}

}
