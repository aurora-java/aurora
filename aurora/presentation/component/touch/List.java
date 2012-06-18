package aurora.presentation.component.touch;

import java.io.IOException;
import java.util.Map;


import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.touch.Component;

@SuppressWarnings("unchecked")
public class List extends Component {
	
	private static final String PROPERTITY_BIND = "bind";
	private static final String PROPERTITY_PAGE_SIZE = "size";
	private static final String PROPERTITY_RENDERER = "renderer";
	private static final String PROPERTITY_CALLBACK = "callback";
	private static final String PROPERTITY_SHOW_PAGEBAR = "showpagebar";
	private static final String PROPERTITY_AUTO_QUERY = "autoquery";
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException {
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		Map map = context.getMap();
		
		addConfig(PROPERTITY_BIND, view.getString(PROPERTITY_BIND));
		addConfig(PROPERTITY_PAGE_SIZE, view.getInt(PROPERTITY_PAGE_SIZE,10));
		addConfig(PROPERTITY_RENDERER, view.getString(PROPERTITY_RENDERER,""));
		addConfig(PROPERTITY_CALLBACK, view.getString(PROPERTITY_CALLBACK,""));
		addConfig(PROPERTITY_SHOW_PAGEBAR, view.getBoolean(PROPERTITY_SHOW_PAGEBAR,true));
		addConfig(PROPERTITY_AUTO_QUERY, view.getBoolean(PROPERTITY_AUTO_QUERY,true));
		map.put(CONFIG, getConfigString());
	}
}
