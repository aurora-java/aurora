package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

public class Label extends Component {
	
	public static final String VERSION = "$Revision$";
	
	private static final String DEFAULT_CLASS = "item-label";
	public static final String PROPERTITY_RENDERER = "renderer";
	
	protected int getDefaultWidth(){
		return 120;
	}
	
	protected String getDefaultClass(BuildSession session, ViewContext context){
		return DEFAULT_CLASS;
	}
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException {
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();	
		Map map = context.getMap();
		
		String renderer = view.getString(PROPERTITY_RENDERER,"");
		if(!"".equals(renderer)) addConfig(PROPERTITY_RENDERER, renderer);
		map.put(CONFIG, getConfigString());
	}
}
