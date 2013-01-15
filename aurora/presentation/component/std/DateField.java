package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.DateFieldConfig;

/**
 * 日历组件.
 * 
 * @version $Id$
 * @author <a href="mailto:hugh.hz.wu@gmail.com">Hugh</a>
 */
public class DateField extends Component {
	
	public static final String VERSION = "$Revision$";

	private static final String DEFAULT_CLASS = "item-dateField";

	protected String getDefaultClass(BuildSession session, ViewContext context) {
		return DEFAULT_CLASS;
	}
	protected int getDefaultWidth(){
		return 150;
	}
	
	protected int getDefaultHeight(){
		return 150;
	}
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException {
		super.onCreateViewContent(session, context);
		Map map = context.getMap();
		CompositeMap view = context.getView();
		DateFieldConfig dfc = DateFieldConfig.getInstance(view);
		
		String dayRenderer = dfc.getDayRenderer();
		if(null!=dayRenderer) addConfig(DateFieldConfig.PROPERTITY_DAY_RENDERER, dayRenderer);
		addConfig(DateFieldConfig.PROPERTITY_ENABLE_MONTH_BTN, dfc.getEnableMonthBtn());
		addConfig(DateFieldConfig.PROPERTITY_ENABLE_YEAR_BTN, dfc.getEnableYearBtn());
		addConfig(DateFieldConfig.PROPERTITY_ENABLE_BESIDE_DAYS, dfc.getEnablebeSideDays());
		map.remove(ComponentConfig.PROPERTITY_HEIGHT);
		map.put(CONFIG, getConfigString());
	}
}
