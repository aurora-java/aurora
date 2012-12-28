package aurora.presentation.component.std.config;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;

public class DashboardConfig extends ComponentConfig {

	public static final String VERSION = "$Revision$";
	
	public static final String TAG_NAME = "dashboard";

	public static final String PROPERTITY_CHART = "chart";
	private static final String PROPERTITY_ALIGN = "align";
	private static final String PROPERTITY_VERTICAL_ALIGN = "verticalAlign";
	private static final String PROPERTITY_PADDING = "padding";
	private static final String PROPERTITY_MAX = "max";
	private static final String PROPERTITY_MIN = "min";
	private static final String PROPERTITY_MARGIN_TOP = "marginTop";
	private static final String PROPERTITY_MARGIN_LEFT = "marginLeft";
	private static final String PROPERTITY_MARGIN_BOTTOM = "marginBottom";
	private static final String PROPERTITY_MARGIN_RIGHT = "marginRight";
	private static final String PROPERTITY_BORDER_COLOR = "borderColor";
	private static final String PROPERTITY_BORDER_WIDTH = "borderWidth";
	private static final String PROPERTITY_BORDER_RADIUS = "borderRadius";
	private static final String PROPERTITY_BACKGROUND_COLOR = "backgroundColor";

	public static final String PROPERTITY_BOARD = "board";
	private static final String PROPERTITY_BOARD_START_ANGLE = "startAngle";
	private static final String PROPERTITY_BOARD_END_ANGLE = "endAngle";
	private static final String PROPERTITY_BOARD_WIDTH = "width";
	private static final String PROPERTITY_BOARD_FILL_COLOR = "fillColor";
	private static final String PROPERTITY_BOARD_FILL_OPACITY = "fillOpacity";
	private static final String PROPERTITY_BOARD_BORDER_COLOR = "borderColor";
	private static final String PROPERTITY_BOARD_BORDER_WIDTH = "borderWidth";
	private static final String PROPERTITY_BOARD_ALLOW_DECIMALS = "allowDecimals";
	private static final String PROPERTITY_BOARD_TICK_COLOR = "tickColor";
	private static final String PROPERTITY_BOARD_TICK_LENGTH = "tickLength";
	private static final String PROPERTITY_BOARD_TICK_WIDTH = "tickWidth";
	private static final String PROPERTITY_BOARD_TICK_POSITION = "tickPosition";
	private static final String PROPERTITY_BOARD_TICK_INTERVAL = "tickInterval";
	private static final String PROPERTITY_BOARD_TICK_ANGLE_INTERVAL = "tickAngleInterval";
	private static final String PROPERTITY_BOARD_TICK_START_ANGLE = "tickStartAngle";
	private static final String PROPERTITY_BOARD_TICK_END_ANGLE = "tickEndAngle";
	private static final String PROPERTITY_BOARD_MINOR_TICK_COLOR = "minorTickColor";
	private static final String PROPERTITY_BOARD_MINOR_TICK_WIDTH = "minorTickWidth";
	private static final String PROPERTITY_BOARD_MINOR_TICK_LENGTH = "minorTickLength";
	private static final String PROPERTITY_BOARD_MINOR_TICK_POSITION = "minorTickPosition";
	private static final String PROPERTITY_BOARD_MARGINAL_TICK_COLOR = "marginalTickColor";
	private static final String PROPERTITY_BOARD_MARGINAL_TICK_WIDTH = "marginalTickWidth";
	private static final String PROPERTITY_BOARD_MARGINAL_TICK_LENGTH = "marginalTickLength";
	private static final String PROPERTITY_BOARD_START_ON_TICK = "startOntick";
	private static final String PROPERTITY_BOARD_END_ON_TICK = "endOntick";
	private static final String PROPERTITY_BOARD_SHOW_FIRST_LABEL = "showFirstLabel";
	private static final String PROPERTITY_BOARD_SHOW_LAST_LABEL = "showLastLabel";

	private static final String PROPERTITY_LABELS = "labels";
	private static final String PROPERTITY_LABELS_ENABLED = "enabled";
	private static final String PROPERTITY_LABELS_ROTATION = "rotation";
	private static final String PROPERTITY_LABELS_X = "x";
	private static final String PROPERTITY_LABELS_Y = "y";
	private static final String PROPERTITY_LABELS_STYLE = "style";

	public static final String PROPERTITY_POINTER = "pointer";
	private static final String PROPERTITY_POINTER_WIDTH = "width";
	private static final String PROPERTITY_POINTER_DIST = "dist";
	private static final String PROPERTITY_POINTER_FILLCOLOR = "fillColor";
	private static final String PROPERTITY_POINTER_FILL_OPACITY = "fillOpacity";
	private static final String PROPERTITY_POINTER_BORDER_COLOR = "borderColor";
	private static final String PROPERTITY_POINTER_BORDER_WIDTH = "borderWidth";

	public static final String PROPERTITY_TITLE = "title";
	private static final String PROPERTITY_TITLE_TEXT = "text";
	private static final String PROPERTITY_TITLE_ALIGN = "align";
	private static final String PROPERTITY_TITLE_VERTICAL_ALIGN = "verticalAlign";
	private static final String PROPERTITY_TITLE_MARGIN = "margin";
	private static final String PROPERTITY_TITLE_X = "x";
	private static final String PROPERTITY_TITLE_Y = "y";
	private static final String PROPERTITY_TITLE_STYLE = "style";

	public static final int DEFAULT_WIDTH = 300;
	public static final int DEFAULT_HEIGHT = 300;

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

	private String hump(String str) {
		String[] strs = str.split("-");
		int length = strs.length;
		if (length == 1) {
			return strs[0];
		}
		StringBuffer sb = new StringBuffer(strs[0]);
		for (int i = 1; i < length; i++) {
			sb.append(strs[i].replaceAll("^.",
					new String(new char[] { strs[i].charAt(0) }).toUpperCase()));
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

	public JSONObject getChart() {
		CompositeMap view = getObjectContext();
		Map cfg = new HashMap();
		cfg.put(PROPERTITY_WIDTH,
				new Integer(view.getInt(PROPERTITY_WIDTH, DEFAULT_WIDTH)));
		cfg.put(PROPERTITY_HEIGHT,
				new Integer(view.getInt(PROPERTITY_HEIGHT, DEFAULT_HEIGHT)));
		putStringCfg(view, PROPERTITY_ALIGN, cfg);
		putStringCfg(view, PROPERTITY_VERTICAL_ALIGN, cfg);
		putIntCfg(view, PROPERTITY_PADDING, cfg);
		putIntCfg(view, PROPERTITY_MAX, cfg);
		putIntCfg(view, PROPERTITY_MIN, cfg);
		putIntCfg(view, PROPERTITY_MARGIN_TOP, cfg);
		putIntCfg(view, PROPERTITY_MARGIN_LEFT, cfg);
		putIntCfg(view, PROPERTITY_MARGIN_BOTTOM, cfg);
		putIntCfg(view, PROPERTITY_MARGIN_RIGHT, cfg);
		putStringCfg(view, PROPERTITY_BORDER_COLOR, cfg);
		putIntCfg(view, PROPERTITY_BORDER_WIDTH, cfg);
		putIntCfg(view, PROPERTITY_BORDER_RADIUS, cfg);
		putStringCfg(view, PROPERTITY_BACKGROUND_COLOR, cfg);
		return new JSONObject(cfg);
	}

	public JSONObject getBoard() {
		CompositeMap view = getObjectContext().getChild(PROPERTITY_BOARD);
		Map cfg = new HashMap();
		if (null != view) {
			putIntCfg(view, PROPERTITY_BOARD_START_ANGLE, cfg);
			putIntCfg(view, PROPERTITY_BOARD_END_ANGLE, cfg);
			putStringCfg(view, PROPERTITY_BOARD_WIDTH, cfg);
			putStringCfg(view, PROPERTITY_BOARD_FILL_COLOR, cfg);
			putFloatCfg(view, PROPERTITY_BOARD_FILL_OPACITY, cfg);
			putStringCfg(view, PROPERTITY_BOARD_BORDER_COLOR, cfg);
			putIntCfg(view, PROPERTITY_BOARD_BORDER_WIDTH, cfg);
			putBooleanCfg(view, PROPERTITY_BOARD_ALLOW_DECIMALS, cfg);
			putStringCfg(view, PROPERTITY_BOARD_TICK_COLOR, cfg);
			putIntCfg(view, PROPERTITY_BOARD_TICK_LENGTH, cfg);
			putIntCfg(view, PROPERTITY_BOARD_TICK_WIDTH, cfg);
			putStringCfg(view, PROPERTITY_BOARD_TICK_POSITION, cfg);
			putFloatCfg(view, PROPERTITY_BOARD_TICK_INTERVAL, cfg);
			putFloatCfg(view, PROPERTITY_BOARD_TICK_ANGLE_INTERVAL, cfg);
			putIntCfg(view, PROPERTITY_BOARD_TICK_START_ANGLE, cfg);
			putIntCfg(view, PROPERTITY_BOARD_TICK_END_ANGLE, cfg);
			putStringCfg(view, PROPERTITY_BOARD_MINOR_TICK_COLOR, cfg);
			putIntCfg(view, PROPERTITY_BOARD_MINOR_TICK_WIDTH, cfg);
			putIntCfg(view, PROPERTITY_BOARD_MINOR_TICK_LENGTH, cfg);
			putStringCfg(view, PROPERTITY_BOARD_MINOR_TICK_POSITION, cfg);
			putStringCfg(view, PROPERTITY_BOARD_MARGINAL_TICK_COLOR, cfg);
			putIntCfg(view, PROPERTITY_BOARD_MARGINAL_TICK_WIDTH, cfg);
			putIntCfg(view, PROPERTITY_BOARD_MARGINAL_TICK_LENGTH, cfg);
			putBooleanCfg(view, PROPERTITY_BOARD_START_ON_TICK, cfg);
			putBooleanCfg(view, PROPERTITY_BOARD_END_ON_TICK, cfg);
			putBooleanCfg(view, PROPERTITY_BOARD_SHOW_FIRST_LABEL, cfg);
			putBooleanCfg(view, PROPERTITY_BOARD_SHOW_LAST_LABEL, cfg);
			processLabels(view, cfg);
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
			putIntCfg(view, PROPERTITY_POINTER_DIST, cfg);
			putStringCfg(view, PROPERTITY_POINTER_FILLCOLOR, cfg);
			putFloatCfg(view, PROPERTITY_POINTER_FILL_OPACITY, cfg);
			putStringCfg(view, PROPERTITY_POINTER_BORDER_COLOR, cfg);
			putIntCfg(view, PROPERTITY_POINTER_BORDER_WIDTH, cfg);
			processLabels(view, cfg);
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

	private void processLabels(CompositeMap parent, Map map) {
		CompositeMap view = parent.getChild(PROPERTITY_LABELS);
		Map cfg = new HashMap();
		if (null != view) {
			putBooleanCfg(view, PROPERTITY_LABELS_ENABLED, cfg);
			putIntCfg(view, PROPERTITY_LABELS_ROTATION, cfg);
			putStyleCfg(view, PROPERTITY_LABELS_STYLE, cfg);
			putIntCfg(view, PROPERTITY_LABELS_X, cfg);
			putIntCfg(view, PROPERTITY_LABELS_Y, cfg);
		}
		if (!cfg.isEmpty())
			map.put(PROPERTITY_LABELS, new JSONObject(cfg));
	}
}
