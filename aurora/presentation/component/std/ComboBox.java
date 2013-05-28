package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComboBoxConfig;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.InputFieldConfig;

/**
 * ComboBox组件.
 * 
 * @version $Id$
 * @author <a href="mailto:znjqolf@126.com">vincent</a>
 */
@SuppressWarnings("unchecked")
public class ComboBox extends TextField {
	
	public static final String VERSION = "$Revision$";
	
	public void onCreateViewContent(BuildSession session, ViewContext view_context) throws IOException{
		super.onCreateViewContent(session, view_context);
		Map map = view_context.getMap();
		CompositeMap model = view_context.getModel();
		CompositeMap view = view_context.getView();	
		
		Integer width = (Integer)map.get(ComponentConfig.PROPERTITY_WIDTH);
		map.put(InputFieldConfig.PROPERTITY_INPUTWIDTH, new Integer(width.intValue()-23));
		map.put(ComboBoxConfig.PROPERTITY_POPWIDTH, new Integer(width.intValue()-2));
		
		ComboBoxConfig cbc = ComboBoxConfig.getInstance(view);
//		if(cbc.getOptions()!=null)addConfig(ComboBoxConfig.PROPERTITY_OPTIONS, uncertain.composite.TextParser.parse(cbc.getOptions(), model));
		if(cbc.getRenderer()!=null)addConfig(ComboBoxConfig.PROPERTITY_RENDERER, cbc.getRenderer());
//		addConfig(ComboBoxConfig.PROPERTITY_VALUE_FIELD, cbc.getValueField());
//		addConfig(ComboBoxConfig.PROPERTITY_DISPLAY_FIELD, cbc.getDisplayField());
		addConfig(ComboBoxConfig.PROPERTITY_FETCH_RECORD, new Boolean(cbc.isFetchRecord()));
		
		map.put(CONFIG, getConfigString());
	}

	
}
