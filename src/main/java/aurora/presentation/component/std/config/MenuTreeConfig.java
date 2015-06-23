package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class MenuTreeConfig extends ComponentConfig {

	public static final String VERSION = "$Revision$";
	
	public static final String TAG_NAME = "menuTree";

	public static final String PROPERTITY_RENDERER = "renderer";
	public static final String PROPERTITY_FIELD_ID = "idfield";
	public static final String PROPERTITY_FIELD_PARENT = "parentfield";
	public static final String PROPERTITY_FIELD_DISPLAY = "displayfield";
	public static final String PROPERTITY_FIELD_SEQUENCE = "sequencefield";
	public static final String PROPERTITY_SHOW_ROOT = "showroot";
	public static final String PROPERTITY_ROOT_ICON = "rooticon";
	public static final String PROPERTITY_ICON_WIDTH = "iconwidth";
	public static final String PROPERTITY_ICON_HEIGHT = "iconheight";
	public static final String PROPERTITY_ICON_MAP = "iconmap";
	public static final String PROPERTITY_ROOT_LINE_OFFSET = "rootlineoffset";

	private static final String DEFAULT_ID_FIELD = "id";
	private static final String DEFAULT_PARENT_FIELD = "pid";
	private static final String DEFAULT_DISPLAY_FIELD = "name";
	private static final String DEFAULT_SEQUENCE_FIELD = "sequence";

	public static MenuTreeConfig getInstance() {
		MenuTreeConfig model = new MenuTreeConfig();
		CompositeMap context = TextFieldConfig.createContext(null, TAG_NAME);
		model.initialize(context);
		return model;
	}

	public static MenuTreeConfig getInstance(CompositeMap context) {
		MenuTreeConfig model = new MenuTreeConfig();
		CompositeMap map = TextFieldConfig.createContext(context, TAG_NAME);
		model.initialize(map);
		return model;
	}

	public String getRenderer() {
		return getString(PROPERTITY_RENDERER);
	}

	public void setRenderer(String renderer) {
		putString(PROPERTITY_RENDERER, renderer);
	}

	public String getIdField() {
		return getString(PROPERTITY_FIELD_ID, DEFAULT_ID_FIELD);
	}

	public void setIdField(String idf) {
		putString(PROPERTITY_FIELD_ID, idf);
	}

	public String getParentField() {
		return getString(PROPERTITY_FIELD_PARENT, DEFAULT_PARENT_FIELD);
	}

	public void setParentField(String pf) {
		putString(PROPERTITY_FIELD_PARENT, pf);
	}

	public String getDisplayField() {
		return getString(PROPERTITY_FIELD_DISPLAY, DEFAULT_DISPLAY_FIELD);
	}

	public void setDisplayField(String df) {
		putString(PROPERTITY_FIELD_DISPLAY, df);
	}

	public String getSequenceField() {
		return getString(PROPERTITY_FIELD_SEQUENCE, DEFAULT_SEQUENCE_FIELD);
	}

	public void setSequenceField(String sf) {
		putString(PROPERTITY_FIELD_SEQUENCE, sf);
	}
	
	public boolean isShowRoot() {
		return getBoolean(PROPERTITY_SHOW_ROOT, true);
	}
	
	public void setShowRoot(boolean showRoot) {
		putBoolean(PROPERTITY_SHOW_ROOT, showRoot);
	}

	public String getRootIcon() {
		return getString(PROPERTITY_ROOT_ICON);
	}

	public void setRootIcon(String rootIcon) {
		putString(PROPERTITY_ROOT_ICON, rootIcon);
	}
	
	public String getIconWidth() {
		return getString(PROPERTITY_ICON_WIDTH);
	}
	
	public void setIconWidth(String iconWidth) {
		putString(PROPERTITY_ICON_WIDTH, iconWidth);
	}
	
	public String getIconHeight() {
		return getString(PROPERTITY_ICON_HEIGHT);
	}
	
	public void setIconHeight(String iconHeight) {
		putString(PROPERTITY_ICON_HEIGHT, iconHeight);
	}
	
	public String getIconMap() {
		return getString(PROPERTITY_ICON_MAP);
	}
	
	public void setIconMap(String iconHeight) {
		putString(PROPERTITY_ICON_MAP, iconHeight);
	}
	
	public int getRootLineOffset() {
		return getInt(PROPERTITY_ROOT_LINE_OFFSET,120);
	}
	
	public void setRootLineOffset(int offset) {
		putInt(PROPERTITY_ROOT_LINE_OFFSET, offset);
	}
}
