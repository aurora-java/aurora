package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

public class Lov extends TextField {
	
	private static final String PROPERTITY_TITLE = "title";
	private static final String PROPERTITY_REF = "ref";
	private static final String PROPERTITY_VALUE_FIELD = "valuefield";
	private static final String PROPERTITY_DISPLAY_FIELD = "displayfield";
	private static final String PROPERTITY_WIN_WIDTH = "winwidth";
	private static final String PROPERTITY_WIN_HEIGHT = "winheight";

	
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		Map map = context.getMap();
		
		Integer width = (Integer)map.get(PROPERTITY_WIDTH);
		map.put(PROPERTITY_INPUTWIDTH, new Integer(width.intValue()-23));
		
		addConfig(PROPERTITY_REF, view.getString(PROPERTITY_REF,""));
		addConfig(PROPERTITY_TITLE, view.getString(PROPERTITY_TITLE,""));
		addConfig(PROPERTITY_VALUE_FIELD, view.getString(PROPERTITY_VALUE_FIELD,""));
		addConfig(PROPERTITY_DISPLAY_FIELD, view.getString(PROPERTITY_DISPLAY_FIELD,""));
		addConfig(PROPERTITY_WIN_WIDTH, new Integer(view.getInt(PROPERTITY_WIN_WIDTH,400)));
		addConfig(PROPERTITY_WIN_HEIGHT, new Integer(view.getInt(PROPERTITY_WIN_HEIGHT,400)));
		map.put(PROPERTITY_CONFIG, getConfigString());
	}
}
