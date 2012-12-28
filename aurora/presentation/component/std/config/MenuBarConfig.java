package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class MenuBarConfig extends ComponentConfig {

	public static final String VERSION = "$Revision$";
	
	public static final String TAG_NAME = "menuBar";

	public static final String PROPERTITY_RENDERER = "renderer";
	public static final String PROPERTITY_FIELD_ID = "idfield";
	public static final String PROPERTITY_FIELD_PARENT = "parentfield";
	public static final String PROPERTITY_FIELD_DISPLAY = "displayfield";
	public static final String PROPERTITY_FIELD_SEQUENCE = "sequencefield";
	public static final String PROPERTITY_FIELD_ICON = "iconfield";
	public static final String PROPERTITY_FIELD_URL = "urlfield";
	public static final String PROPERTITY_FOCUS = "focus";
	public static final String PROPERTITY_ROOT_ID = "rootid";
	public static final String PROPERTITY_MENU_TYPE = "menutype";
	public static final String PROPERTITY_URL_TARGET = "urltarget";

	private static final String DEFAULT_ID_FIELD = "id";
	private static final String DEFAULT_PARENT_FIELD = "pid";
	private static final String DEFAULT_DISPLAY_FIELD = "name";
	private static final String DEFAULT_SEQUENCE_FIELD = "sequence";
	private static final String DEFAULT_ICON_FIELD = "icon";
	private static final String DEFAULT_URL_FIELD = "url";
	public static final int DEFAULT_ROOT_ID = -1;

	public static MenuBarConfig getInstance() {
		MenuBarConfig model = new MenuBarConfig();
		CompositeMap context = TextFieldConfig.createContext(null, TAG_NAME);
		model.initialize(context);
		return model;
	}

	public static MenuBarConfig getInstance(CompositeMap context) {
		MenuBarConfig model = new MenuBarConfig();
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

	public String getIconField() {
		return getString(PROPERTITY_FIELD_ICON, DEFAULT_ICON_FIELD);
	}

	public void setIconField(String icf) {
		putString(PROPERTITY_FIELD_ICON, icf);
	}

	public String getURLField() {
		return getString(PROPERTITY_FIELD_URL, DEFAULT_URL_FIELD);
	}

	public void setURLField(String icf) {
		putString(PROPERTITY_FIELD_URL, icf);
	}

	public String getFocus() {
		return getString(PROPERTITY_FOCUS);
	}

	public void setFocus(String fi) {
		putString(PROPERTITY_FOCUS, fi);
	}

	public int getRootId() {
		return getInt(PROPERTITY_ROOT_ID, DEFAULT_ROOT_ID);
	}

	public void setRootId(int ri) {
		putInt(PROPERTITY_ROOT_ID, ri);
	}

	public String getMenuType() {
		return getString(PROPERTITY_MENU_TYPE);
	}

	public void setMenuType(String mt) {
		putString(PROPERTITY_MENU_TYPE, mt);
	}

	public String getURLTarget() {
		return getString(PROPERTITY_URL_TARGET);
	}

	public void setURLTarget(String ut) {
		putString(PROPERTITY_URL_TARGET, ut);
	}
}
