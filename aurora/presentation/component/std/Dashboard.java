package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.EventConfig;
import aurora.presentation.component.std.config.DashboardConfig;

/**
 * Panel
 * 
 * @version $Id: Panel.java v 1.0 2011-4-21 上午10:37:19 hugh Exp $
 * @author <a href="mailto:hugh.hz.wu@gmail.com">Hugh</a>
 */
public class Dashboard extends Component {

	protected int getDefaultWidth() {
		return 300;
	}

	protected int getDefaultHeight() {
		return 300;
	}

	public void onPreparePageContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onPreparePageContent(session, context);
		addJavaScript(session, context, "graphic/Graphics-min.js");
		addJavaScript(session, context, "dashboard/Dashboard-min.js");
	}

	public void onCreateViewContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onCreateViewContent(session, context);
		Map map = context.getMap();
		CompositeMap view = context.getView();
		DashboardConfig dc = DashboardConfig.getInstance(view);
		addConfig(DashboardConfig.PROPERTITY_PADDING,
				new Integer(dc.getPadding()));
		if (null != dc.getMax())
			addConfig(DashboardConfig.PROPERTITY_MAX, dc.getMax());
		if (null != dc.getMin())
			addConfig(DashboardConfig.PROPERTITY_MIN, dc.getMin());
		if (null != dc.getMarginTop())
			addConfig(DashboardConfig.PROPERTITY_MARGIN_TOP, dc.getMarginTop());
		if (null != dc.getMarginLeft())
			addConfig(DashboardConfig.PROPERTITY_MARGIN_LEFT,
					dc.getMarginLeft());
		if (null != dc.getMarginBottom())
			addConfig(DashboardConfig.PROPERTITY_MARGIN_BOTTOM,
					dc.getMarginBottom());
		if (null != dc.getMarginRight())
			addConfig(DashboardConfig.PROPERTITY_MARGIN_RIGHT,
					dc.getMarginRight());
		if (null != dc.getBorderColor())
			addConfig(DashboardConfig.PROPERTITY_BORDER_COLOR,
					dc.getBorderColor());
		if (null != dc.getBorderWidth())
			addConfig(DashboardConfig.PROPERTITY_BORDER_WIDTH,
					dc.getBorderWidth());
		if (null != dc.getBorderRadius())
			addConfig(DashboardConfig.PROPERTITY_BORDER_RADIUS,
					dc.getBorderRadius());
		JSONObject board = dc.getBoard();
		if (null != board)
			addConfig(DashboardConfig.PROPERTITY_BOARD, board);
		JSONObject pointer = dc.getBoard();
		if (null != pointer)
			addConfig(DashboardConfig.PROPERTITY_POINTER, pointer);
		JSONObject title = dc.getTitle();
		if (null != title)
			addConfig(DashboardConfig.PROPERTITY_TITLE, title);
		map.put(CONFIG, getConfigString());
	}
}
