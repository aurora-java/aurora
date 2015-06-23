package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.CheckBoxConfig;

@SuppressWarnings("unchecked")
public class CheckBox extends Component {
	
	public CheckBox(IObjectRegistry registry) {
		super(registry);
	}
	protected int getDefaultWidth() {
		return -1;
	}
	public static final String VERSION = "$Revision$";
	
	public void onCreateViewContent(BuildSession session, ViewContext view_context) throws IOException{
		super.onCreateViewContent(session, view_context);
		Map map = view_context.getMap();
		CompositeMap view = view_context.getView();	
		CheckBoxConfig cbc = CheckBoxConfig.getInstance(view);
		String label = session.getLocalizedPrompt(cbc.getLabel());
		map.put(CheckBoxConfig.PROPERTITY_LABEL, label);
		map.put(CheckBoxConfig.PROPERTITY_TAB_INDEX, cbc.getTabIndex());
		int height = cbc.getHeight();
		map.put("margin-top", new Integer(Math.round((height-13)/2)));
		map.put(CONFIG, getConfigString());
	}
}
