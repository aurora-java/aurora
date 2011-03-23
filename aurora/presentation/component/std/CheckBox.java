package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.CheckBoxConfig;

public class CheckBox extends Component {
	
	public void onCreateViewContent(BuildSession session, ViewContext view_context) throws IOException{
		super.onCreateViewContent(session, view_context);
		Map map = view_context.getMap();
		CompositeMap view = view_context.getView();	
			
//		addConfig(PROPERTITY_CHECKEDVALUE, view.getString(PROPERTITY_CHECKEDVALUE,"Y"));
//		addConfig(PROPERTITY_UNCHECKEDVALUE, view.getString(PROPERTITY_UNCHECKEDVALUE,"N"));
		String label = view.getString(CheckBoxConfig.PROPERTITY_LABEL, "");
		label = session.getLocalizedPrompt(label);
		/*
		if(!"".equals(label)){
			label = label;
		}
		*/
		map.put(CheckBoxConfig.PROPERTITY_LABEL, label);
		map.put(CONFIG, getConfigString());
	}
	
//	protected int getDefaultWidth(){
//		return 13;
//	}

}
