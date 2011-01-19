package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

/**
 * 日历组件.
 * 
 * @version $Id: DatePicker.java v 1.0 2009-7-21 下午04:06:13 znjqolf Exp $
 * @author <a href="mailto:znjqolf@126.com">vincent</a>
 */
public class DateTimePicker extends DatePicker {
	

	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException {
		super.onCreateViewContent(session, context);
		Map map = context.getMap();
		map.put(CONFIG, getConfigString());
	}
}
