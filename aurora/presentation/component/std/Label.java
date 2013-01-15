package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.LabelConfig;

@SuppressWarnings("unchecked")
public class Label extends Component {
	
	public static final String VERSION = "$Revision$";
	
	private static final String DEFAULT_CLASS = "item-label";
	
	protected int getDefaultWidth(){
		return 120;
	}
	
	protected String getDefaultClass(BuildSession session, ViewContext context){
		return DEFAULT_CLASS;
	}
	
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException {
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();	
		LabelConfig lc = LabelConfig.getInstance(view);
		Map map = context.getMap();
		String renderer = lc.getRenderer();
		if(!"".equals(renderer)) addConfig(LabelConfig.PROPERTITY_RENDERER, renderer);
		map.put(CONFIG, getConfigString());
	}
}
