package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

/**
 * ComboBox组件.
 * 
 * @version $Id: ComboBox.java v 1.0 2009-8-27 下午01:03:10 znjqolf Exp $
 * @author <a href="mailto:znjqolf@126.com">vincent</a>
 */
public class ComboBox extends TextField {

	private static final String PROPERTITY_POPWIDTH = "popwidth";
	private static final String PROPERTITY_VALUE_FIELD = "valuefield";
	private static final String PROPERTITY_DISPLAY_FIELD = "displayfield";
	private static final String PROPERTITY_OPTIONS = "options";

	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		super.onPreparePageContent(session, context);
		addStyleSheet(session, context, "combobox/ComboBox.css");
		addJavaScript(session, context, "core/TriggerField.js");
		addJavaScript(session, context, "combobox/ComboBox.js");
	}

	public void onCreateViewContent(BuildSession session, ViewContext view_context) throws IOException{
		super.onCreateViewContent(session, view_context);
		Map map = view_context.getMap();
		CompositeMap view = view_context.getView();	
		
		Integer width = (Integer)map.get(PROPERTITY_WIDTH);
		map.put(PROPERTITY_INPUTWIDTH, new Integer(width.intValue()-23));
		map.put(PROPERTITY_POPWIDTH, new Integer(width.intValue()-2));
		
		
		String options = view.getString(PROPERTITY_OPTIONS, "");
		if(!options.equals(""))addConfig(PROPERTITY_OPTIONS, options);		
		addConfig(PROPERTITY_VALUE_FIELD, view.getString(PROPERTITY_VALUE_FIELD, "code"));
		addConfig(PROPERTITY_DISPLAY_FIELD, view.getString(PROPERTITY_DISPLAY_FIELD, "name"));
		
		map.put(PROPERTITY_CONFIG, getConfigString());
	}

	
}
