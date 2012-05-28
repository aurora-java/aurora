package aurora.presentation.component.std.config;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;

public class DashboardConfig extends ComponentConfig {

	public static final String TAG_NAME = "dashboard";

	public static final String PROPERTITY_ALIGN = "align";
	public static final String PROPERTITY_VERTICAL_ALIGN = "verticalalign";
	public static final String PROPERTITY_PADDING = "padding";
	public static final String PROPERTITY_MAX = "max";
	public static final String PROPERTITY_MIN = "min";

	public static final String PROPERTITY_BOARD = "board";
	private static final String PROPERTITY_BOARD_START_ANGLE = "startangle";
	private static final String PROPERTITY_BOARD_END_ANGLE = "endangle";
	private static final String PROPERTITY_BOARD_DASH_WIDTH = "dashwidth";
	private static final String PROPERTITY_BOARD_FILL_COLOR = "fillcolor";
	private static final String PROPERTITY_BOARD_FILL_OPACITY = "fillopacity";
	private static final String PROPERTITY_BOARD_BORDER_COLOR = "bordercolor";
	private static final String PROPERTITY_BOARD_BORDER_WIDTH = "borderwidth";

	public static final String PROPERTITY_POINTER = "pointer";
	private static final String PROPERTITY_POINTER_WIDTH = "width";
	private static final String PROPERTITY_POINTER_FILLCOLOR = "fillcolor";
	private static final String PROPERTITY_POINTER_FILL_OPACITY = "fillopacity";
	private static final String PROPERTITY_POINTER_BORDER_COLOR = "bordercolor";
	private static final String PROPERTITY_POINTER_BORDER_WIDTH = "borderwidth";

	public static final String PROPERTITY_TITLE = "title";
	private static final String PROPERTITY_TITLE_TEXT = "text";
	private static final String PROPERTITY_TITLE_ALIGN = "align";
	private static final String PROPERTITY_TITLE_VERTICAL_ALIGN = "verticalalign";
	private static final String PROPERTITY_TITLE_MARGIN = "margin";
	private static final String PROPERTITY_TITLE_X = "x";
	private static final String PROPERTITY_TITLE_Y = "y";
	private static final String PROPERTITY_TITLE_STYLE = "style";

	public static DashboardConfig getInstance() {
		DashboardConfig model = new DashboardConfig();
		model.initialize(DashboardConfig.createContext(null, TAG_NAME));
		return model;
	}

	public static DashboardConfig getInstance(CompositeMap context) {
		DashboardConfig model = new DashboardConfig();
		model.initialize(DashboardConfig.createContext(context, TAG_NAME));
		return model;
	}

	private void putStringCfg(CompositeMap view, String key, Map map) {
		String value = view.getString(key.toLowerCase());
		if (value != null)
			map.put(key, value);
	}

	private void putIntCfg(CompositeMap view, String key, Map map) {
		Integer value = view.getInt(key.toLowerCase());
		if (value != null)
			map.put(key, value);
	}

	private void putFloatCfg(CompositeMap view, String key, Map map) {
		Float value = view.getFloat(key.toLowerCase());
		if (value != null)
			map.put(key, value);
	}

	private void putLongCfg(CompositeMap view, String key, Map map) {
		Long value = view.getLong(key.toLowerCase());
		if (value != null)
			map.put(key, value);
	}

	private void putBooleanCfg(CompositeMap view, String key, Map map) {
		Boolean value = view.getBoolean(key.toLowerCase());
		if (value != null)
			map.put(key, value);
	}

	private String hump(String str){
		String [] strs = str.split("-");
		int length = strs.length;
		if(length == 1){
			return strs[0];
		}
		StringBuffer sb = new StringBuffer(strs[0]);
		for (int i = 1; i < length; i++) {
			sb.append(strs[i].replaceAll("^.", new String(new char[]{strs[i].charAt(0)}).toUpperCase()));
		}
		return sb.toString();
	}
	
	private void putStyleCfg(CompositeMap view, String key, Map map) {
		String value = view.getString(key.toLowerCase());
		if (value != null) {
			JSONObject smap = new JSONObject();
			String[] sts = value.split(";");
			for (int i = 0; i < sts.length; i++) {
				String style = sts[i];
				if (!"".equals(style) && style.indexOf(":") != -1) {
					String[] vs = style.split(":");
					String k = hump(vs[0]);
					String v = vs[1];
					v = v.replaceAll("'", "");

					Integer iv = null;
					try {
						iv = Integer.valueOf(v);
					} catch (Exception e) {
					}
					try {
						if (iv == null) {
							smap.put(k, v);
						} else {
							smap.put(k, iv);
						}
					} catch (JSONException e) {
						throw new RuntimeException(e);
					}
				}
			}
			map.put(key, smap);
		}
	}

	public String getAlign() {
		return getString(PROPERTITY_ALIGN);
	}

	public void setAlign(String align) {
		putString(PROPERTITY_ALIGN, align);
	}

	public String getVerticalAlign() {
		return getString(PROPERTITY_VERTICAL_ALIGN);
	}

	public void setVerticalAlign(String verticalAlign) {
		putString(PROPERTITY_VERTICAL_ALIGN, verticalAlign);
	}

	public int getPadding() {
		return getInt(PROPERTITY_PADDING, 50);
	}

	public void setPadding(int padding) {
		putInt(PROPERTITY_PADDING, padding);
	}

	public Integer getMax() {
		return getInteger(PROPERTITY_MAX);
	}

	public void setMax(Integer max) {
		putInt(PROPERTITY_MAX, max);
	}

	public Integer getMin() {
		return getInteger(PROPERTITY_MIN);
	}

	public void setMin(Integer min) {
		putInt(PROPERTITY_MIN, min);
	}

	public JSONObject getBoard() {
		CompositeMap view = getObjectContext().getChild(PROPERTITY_BOARD);
		Map cfg = new HashMap();
		if (null != view) {
			putIntCfg(view, PROPERTITY_BOARD_START_ANGLE, cfg);
			putIntCfg(view, PROPERTITY_BOARD_END_ANGLE, cfg);
			putFloatCfg(view, PROPERTITY_BOARD_DASH_WIDTH, cfg);
			putStringCfg(view, PROPERTITY_BOARD_FILL_COLOR, cfg);
			putFloatCfg(view, PROPERTITY_BOARD_FILL_OPACITY, cfg);
			putStringCfg(view, PROPERTITY_BOARD_BORDER_COLOR, cfg);
			putIntCfg(view, PROPERTITY_BOARD_BORDER_WIDTH, cfg);
		}
		if (cfg.isEmpty())
			return null;
		else
			return new JSONObject(cfg);
	}

	public JSONObject getPointer() {
		CompositeMap view = getObjectContext().getChild(PROPERTITY_POINTER);
		Map cfg = new HashMap();
		if (null != view) {
			putIntCfg(view, PROPERTITY_POINTER_WIDTH, cfg);
			putStringCfg(view, PROPERTITY_POINTER_FILLCOLOR, cfg);
			putFloatCfg(view, PROPERTITY_POINTER_FILL_OPACITY, cfg);
			putStringCfg(view, PROPERTITY_POINTER_BORDER_COLOR, cfg);
			putIntCfg(view, PROPERTITY_POINTER_BORDER_WIDTH, cfg);
		}
		if (cfg.isEmpty())
			return null;
		else
			return new JSONObject(cfg);
	}

	public JSONObject getTitle() {
		CompositeMap view = getObjectContext().getChild(PROPERTITY_TITLE);
		Map cfg = new HashMap();
		if (null != view) {
			putStringCfg(view, PROPERTITY_TITLE_TEXT, cfg);
			putStringCfg(view, PROPERTITY_TITLE_ALIGN, cfg);
			putStringCfg(view, PROPERTITY_TITLE_VERTICAL_ALIGN, cfg);
			putIntCfg(view, PROPERTITY_TITLE_MARGIN, cfg);
			putFloatCfg(view, PROPERTITY_TITLE_X, cfg);
			putFloatCfg(view, PROPERTITY_TITLE_Y, cfg);
			putStringCfg(view, PROPERTITY_POINTER_BORDER_COLOR, cfg);
			putStyleCfg(view, PROPERTITY_TITLE_STYLE, cfg);
		}
		if (cfg.isEmpty())
			return null;
		else
			return new JSONObject(cfg);
	}
}
