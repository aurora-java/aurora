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
		return DashboardConfig.DEFAULT_WIDTH;
	}

	protected int getDefaultHeight() {
		return DashboardConfig.DEFAULT_HEIGHT;
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
		JSONObject chart = dc.getChart();
		JSONObject board = dc.getBoard();
		JSONObject pointer = dc.getPointer();
		JSONObject title = dc.getTitle();
		addConfig(DashboardConfig.PROPERTITY_CHART, chart);
		if (null != board)
			addConfig(DashboardConfig.PROPERTITY_BOARD, board);
		if (null != pointer)
			addConfig(DashboardConfig.PROPERTITY_POINTER, pointer);
		if (null != title)
			addConfig(DashboardConfig.PROPERTITY_TITLE, title);
		map.put(CONFIG, getConfigString());
	}
}
