package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.InputFieldConfig;

public class Lov extends TextField {
	//TODO:需要改造
	public static final String PROPERTITY_TITLE = "title";
	public static final String PROPERTITY_VALUE_FIELD = "valuefield";
	public static final String PROPERTITY_DISPLAY_FIELD = "displayfield";
	public static final String PROPERTITY_LOV_URL = "lovurl";
	public static final String PROPERTITY_LOV_MODEL = "lovmodel";
	public static final String PROPERTITY_LOV_SERVICE = "lovservice";
	public static final String PROPERTITY_LOV_WIDTH = "lovwidth";
	public static final String PROPERTITY_LOV_AUTO_QUERY = "lovautoquery";
	public static final String PROPERTITY_LOV_LABEL_WIDTH = "lovlabelwidth";
	public static final String PROPERTITY_LOV_HEIGHT = "lovheight";
	public static final String PROPERTITY_LOV_GRID_HEIGHT = "lovgridheight";
	public static final String PROPERTITY_LOV_FETCH_REMOTE = "fetchremote";
	public static final String PROPERTITY_LOV_AUTOCOMPLETE_RENDERER = "autocompleterenderer";
	
	private static final String CONFIG_CONTEXT = "context";
	
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		Map map = context.getMap();
		
		Integer width = (Integer)map.get(ComponentConfig.PROPERTITY_WIDTH);
		map.put(InputFieldConfig.PROPERTITY_INPUTWIDTH, new Integer(width.intValue()-23));
		
		if(session.getContextPath()!=null) addConfig(CONFIG_CONTEXT,session.getContextPath()+"/");
		addConfig(PROPERTITY_LOV_URL, view.getString(PROPERTITY_LOV_URL,""));
		addConfig(PROPERTITY_TITLE, view.getString(PROPERTITY_TITLE,""));
		addConfig(PROPERTITY_VALUE_FIELD, view.getString(PROPERTITY_VALUE_FIELD,""));
		addConfig(PROPERTITY_DISPLAY_FIELD, view.getString(PROPERTITY_DISPLAY_FIELD,""));
		addConfig(PROPERTITY_LOV_MODEL, view.getString(PROPERTITY_LOV_MODEL,""));
		addConfig(PROPERTITY_LOV_SERVICE, view.getString(PROPERTITY_LOV_SERVICE,""));
		addConfig(PROPERTITY_LOV_WIDTH, new Integer(view.getInt(PROPERTITY_LOV_WIDTH,400)));
		addConfig(PROPERTITY_LOV_AUTO_QUERY, new Boolean(view.getBoolean(PROPERTITY_LOV_AUTO_QUERY,true)));
		addConfig(PROPERTITY_LOV_LABEL_WIDTH, new Integer(view.getInt(PROPERTITY_LOV_LABEL_WIDTH,75)));
		addConfig(PROPERTITY_LOV_HEIGHT, new Integer(view.getInt(PROPERTITY_LOV_HEIGHT,400)));
		addConfig(PROPERTITY_LOV_GRID_HEIGHT, new Integer(view.getInt(PROPERTITY_LOV_GRID_HEIGHT,350)));
		addConfig(PROPERTITY_LOV_FETCH_REMOTE, new Boolean(view.getBoolean(PROPERTITY_LOV_FETCH_REMOTE, true)));
		String renderer = view.getString(PROPERTITY_LOV_AUTOCOMPLETE_RENDERER);
		if(renderer != null)addConfig(PROPERTITY_LOV_AUTOCOMPLETE_RENDERER, renderer);
		map.put(CONFIG, getConfigString());
	}
}
