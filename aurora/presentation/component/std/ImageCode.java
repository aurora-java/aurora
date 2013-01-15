package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

@SuppressWarnings("unchecked")
public class ImageCode extends Component {
	
	public static final String VERSION = "$Revision$";	
	
	public static final String PROPERTITY_ENABLE = "enable";
	public static final String PROPERTITY_SRC = "src";
	private String PROPERTITY_SRC_VALUE = "imagecode";
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException {
		super.onCreateViewContent(session, context);
		Map map = context.getMap();
		CompositeMap view = context.getView();
		boolean enable = view.getBoolean(PROPERTITY_ENABLE, true);
		if(enable==true){
			map.put(PROPERTITY_SRC, PROPERTITY_SRC_VALUE);			
		}else{
			addConfig(PROPERTITY_ENABLE, new Boolean(false));
			map.put(PROPERTITY_SRC, "");
		}
		map.put(CONFIG, getConfigString());
	}
}
