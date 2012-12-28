package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.DateTimePickerConfig;

/**
 * 日历组件.
 * 
 * @version $Id$
 * @author <a href="mailto:znjqolf@126.com">vincent</a>
 */
public class DateTimePicker extends DatePicker {
	
	public static final String VERSION = "$Revision$";

	public void onCreateViewContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onCreateViewContent(session, context);
		Map map = context.getMap();
		DateTimePickerConfig dtpc = DateTimePickerConfig.getInstance(context
				.getView());
		addConfig(DateTimePickerConfig.PROPERTITY_HOUR, new Integer(dtpc
				.getHour()));
		addConfig(DateTimePickerConfig.PROPERTITY_MINUTE, new Integer(dtpc
				.getMinute()));
		addConfig(DateTimePickerConfig.PROPERTITY_SECOND, new Integer(dtpc
				.getSecond()));
		map.put(CONFIG, getConfigString());
	}
}
