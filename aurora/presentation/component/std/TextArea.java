package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.InputFieldConfig;

public class TextArea extends Component {
	
	private static final String DEFAULT_CLASS = "item-textarea";
	protected static final String PROPERTITY_READONLY = "readonly";
	protected static final String CLASSNAME_READONLY = "item-readOnly";
	protected int getDefaultWidth(){
		return 150;
	}
	
	protected int getDefaultHeight(){
		return 50;
	}
	
	protected String getDefaultClass(BuildSession session, ViewContext context){
		CompositeMap view = context.getView();
		String wrapClass = DEFAULT_CLASS;
		boolean readOnly = view.getBoolean(PROPERTITY_READONLY, false);
		if(readOnly) {
			wrapClass += " "+CLASSNAME_READONLY;
		}
		return wrapClass;
	}
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		Map map = context.getMap();
		boolean readOnly = view.getBoolean(PROPERTITY_READONLY, false);
		if(readOnly) {
			map.put(PROPERTITY_READONLY, "readonly");
		}
		addConfig(PROPERTITY_READONLY, Boolean.valueOf(readOnly));
		map.put(ComponentConfig.PROPERTITY_TAB_INDEX, new Integer(view.getInt(ComponentConfig.PROPERTITY_TAB_INDEX, 0)));
		map.put(CONFIG, getConfigString());		
	}
}
