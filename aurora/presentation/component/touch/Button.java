package aurora.presentation.component.touch;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.IDGenerator;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.EventConfig;

@SuppressWarnings("unchecked")
public class Button extends Component {
	
	public static final String PROPERTITY_TEXT = "text";
	public static final String PROPERTITY_TYPE = "type";
	private static final String DEFAULT_CLASS = "btn";
	private static final String DEFAULT_TYPE = "button";
	
	protected String getDefaultClass(BuildSession session, ViewContext context){
		return DEFAULT_CLASS;
	}
	
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		
		
		Map map = context.getMap();
		map.put(PROPERTITY_TYPE, view.getString(PROPERTITY_TYPE, DEFAULT_TYPE));
		map.put(PROPERTITY_TEXT, view.getString(PROPERTITY_TEXT,"button"));
	}
}
