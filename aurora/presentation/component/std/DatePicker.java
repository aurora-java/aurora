package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.DatePickerConfig;
import aurora.presentation.component.std.config.InputFieldConfig;

/**
 * 日历组件.
 * 
 * @version $Id: DatePicker.java v 1.0 2009-7-21 下午04:06:13 znjqolf Exp $
 * @author <a href="mailto:znjqolf@126.com">vincent</a>
 */
public class DatePicker extends TextField {

	private static final String BODY = "body";

	public void onCreateViewContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onCreateViewContent(session, context);
		Map map = context.getMap();
		DatePickerConfig dpc = DatePickerConfig.getInstance(context.getView());
		int viewSize = dpc.getViewSize();
		if (viewSize > 4)
			viewSize = 4;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < viewSize; i++) {
			sb.append("<TD valign='top' id='" + id + "_df" + i + "'></TD>");
		}
		map.put(BODY, sb.toString());
		addConfig(DatePickerConfig.PROPERTITY_VIEW_SIZE, new Integer(viewSize));
		Integer width = (Integer) map.get(ComponentConfig.PROPERTITY_WIDTH);
		map.put(InputFieldConfig.PROPERTITY_INPUTWIDTH, new Integer(width
				.intValue() - 23));
		if (null != dpc.getDayRenderer())
			addConfig(DatePickerConfig.PROPERTITY_DAY_RENDERER, dpc
					.getDayRenderer());
		addConfig(DatePickerConfig.PROPERTITY_ENABLE_MONTH_BTN, dpc
				.getEnableMonthBtn());
		addConfig(DatePickerConfig.PROPERTITY_ENABLE_YEAR_BTN, dpc
				.getEnableYearBtn());
		addConfig(DatePickerConfig.PROPERTITY_ENABLE_BESIDE_DAYS, dpc
				.getEnableBesideDays());
		map.put(CONFIG, getConfigString());
	}
}
