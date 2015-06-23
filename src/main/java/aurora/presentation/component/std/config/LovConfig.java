package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class LovConfig extends InputFieldConfig {

	public static final String VERSION = "$Revision$";
	
	public static final String TAG_NAME = "lov";

//	public static final String PROPERTITY_TITLE = "title";
//	public static final String PROPERTITY_VALUE_FIELD = "valuefield";
//	public static final String PROPERTITY_DISPLAY_FIELD = "displayfield";
//	public static final String PROPERTITY_LOV_URL = "lovurl";
//	public static final String PROPERTITY_LOV_MODEL = "lovmodel";
//	public static final String PROPERTITY_LOV_SERVICE = "lovservice";
//	public static final String PROPERTITY_LOV_WIDTH = "lovwidth";
//	public static final String PROPERTITY_LOV_AUTO_QUERY = "lovautoquery";
//	public static final String PROPERTITY_LOV_LABEL_WIDTH = "lovlabelwidth";
//	public static final String PROPERTITY_LOV_HEIGHT = "lovheight";
//	public static final String PROPERTITY_LOV_GRID_HEIGHT = "lovgridheight";
//	public static final String PROPERTITY_FETCH_REMOTE = "fetchremote";
//	public static final String PROPERTITY_AUTOCOMPLETE_RENDERER = "autocompleterenderer";
//	public static final String PROPERTITY_FETCH_SINGLE = "fetchsingle";

	public static LovConfig getInstance() {
		LovConfig model = new LovConfig();
		model.initialize(LovConfig.createContext(null, TAG_NAME));
		return model;
	}

	public static LovConfig getInstance(CompositeMap context) {
		LovConfig model = new LovConfig();
		model.initialize(LovConfig.createContext(context, TAG_NAME));
		return model;
	}

//	public String getTitle() {
//		return getString(PROPERTITY_TITLE, "");
//	}
//
//	public void setTitle(String title) {
//		putString(PROPERTITY_TITLE, title);
//	}
//
//	public String getValueField() {
//		return getString(PROPERTITY_VALUE_FIELD, "");
//	}
//
//	public void setValueField(String valueField) {
//		putString(PROPERTITY_VALUE_FIELD, valueField);
//	}
//
//	public String getDisplayField() {
//		return getString(PROPERTITY_DISPLAY_FIELD, "");
//	}
//
//	public void setDisplayField(String displayField) {
//		putString(PROPERTITY_DISPLAY_FIELD, displayField);
//	}
//
//	public String getLovUrl() {
//		return getString(PROPERTITY_LOV_URL, "");
//	}
//
//	public void setLovUrl(String lovUrl) {
//		putString(PROPERTITY_LOV_URL, lovUrl);
//	}
//
//	public String getLovModel() {
//		return getString(PROPERTITY_LOV_MODEL, "");
//	}
//
//	public void setLovModel(String lovModel) {
//		putString(PROPERTITY_LOV_MODEL, lovModel);
//	}
//
//	public String getLovService() {
//		return getString(PROPERTITY_LOV_SERVICE, "");
//	}
//
//	public void setLovService(String lovService) {
//		putString(PROPERTITY_LOV_SERVICE, lovService);
//	}
//
//	public int getLovWidth() {
//		return getInt(PROPERTITY_LOV_WIDTH, 400);
//	}
//
//	public void setLovWidth(int width) {
//		putInt(PROPERTITY_LOV_WIDTH, width);
//	}
//
//	public boolean getLovAutoQuery() {
//		return getBoolean(PROPERTITY_LOV_AUTO_QUERY, true);
//	}
//
//	public void setLovAutoQuery(boolean lovAutoQuery) {
//		putBoolean(PROPERTITY_LOV_AUTO_QUERY, lovAutoQuery);
//	}
//
//	public int getLovLabelWidth() {
//		return getInt(PROPERTITY_LOV_LABEL_WIDTH, 75);
//	}
//
//	public void setLovLabelWidth(int lovLabelWidth) {
//		putInt(PROPERTITY_LOV_LABEL_WIDTH, lovLabelWidth);
//	}
//
//	public int getLovHeight() {
//		return getInt(PROPERTITY_LOV_HEIGHT, 400);
//	}
//
//	public void setLovHeight(int height) {
//		putInt(PROPERTITY_LOV_HEIGHT, height);
//	}
//
//	public int getLovGridHeight() {
//		return getInt(PROPERTITY_LOV_GRID_HEIGHT, 350);
//	}
//
//	public void setLovGridHeight(int lovGridHeight) {
//		putInt(PROPERTITY_LOV_GRID_HEIGHT, lovGridHeight);
//	}
//
//	public boolean getFetchRemote() {
//		return getBoolean(PROPERTITY_FETCH_REMOTE, true);
//	}
//
//	public void setFetchRemote(boolean fetchRemote) {
//		putBoolean(PROPERTITY_FETCH_REMOTE, fetchRemote);
//	}
//
//	public String getAutocompleteRenderer() {
//		return getString(PROPERTITY_AUTOCOMPLETE_RENDERER);
//	}
//
//	public void setAutocompleteRenderer(String renderer) {
//		putString(PROPERTITY_AUTOCOMPLETE_RENDERER, renderer);
//	}
//
//	public boolean getFetchSingle() {
//		return getBoolean(PROPERTITY_FETCH_SINGLE, false);
//	}
//	public void setFetchSingle(boolean fetchSingle) {
//		putBoolean(PROPERTITY_FETCH_SINGLE, fetchSingle);
//	}
}
