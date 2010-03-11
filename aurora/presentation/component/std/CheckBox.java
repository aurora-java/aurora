package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

public class CheckBox extends Component {
	
//	private static final String PROPERTITY_CHECKEDVALUE = "checkedvalue";
//	private static final String PROPERTITY_UNCHECKEDVALUE = "uncheckedvalue";
	private static final String PROPERTITY_LABEL = "label";
	
	public void onCreateViewContent(BuildSession session, ViewContext view_context) throws IOException{
		super.onCreateViewContent(session, view_context);
		Map map = view_context.getMap();
		CompositeMap view = view_context.getView();	
			
//		addConfig(PROPERTITY_CHECKEDVALUE, view.getString(PROPERTITY_CHECKEDVALUE,"Y"));
//		addConfig(PROPERTITY_UNCHECKEDVALUE, view.getString(PROPERTITY_UNCHECKEDVALUE,"N"));
		String label = view.getString(PROPERTITY_LABEL, "");
		if(!"".equals(label)){
			label = ":"+label;
		}
		map.put(PROPERTITY_LABEL, label);
		map.put(PROPERTITY_CONFIG, getConfigString());
	}

}
