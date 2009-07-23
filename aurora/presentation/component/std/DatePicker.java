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
public class DatePicker extends TextField {
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		super.onPreparePageContent(session, context);		
		addStyleSheet(session, context, "datefield/DateField.css");
		addJavaScript(session, context, "core/TriggerField.js");
		addJavaScript(session, context, "datefield/DateField.js");
		addJavaScript(session, context, "datefield/DatePicker.js");
	}

	public void onCreateViewContent(BuildSession session, ViewContext context)  {
		super.onCreateViewContent(session, context);
//		CompositeMap view = context.getView();
		Map map = context.getMap();		
		map.put(PROPERTITY_CONFIG, getConfigString());
	}
}
