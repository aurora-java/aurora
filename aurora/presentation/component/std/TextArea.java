package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.TextAreaConfig;

public class TextArea extends Component {
	
	public static final String VERSION = "$Revision$";
	private static final String DEFAULT_CLASS = "item-textarea";
	protected static final String CLASSNAME_READONLY = "item-readOnly";
	protected int getDefaultWidth(){
		return 150;
	}
	
	protected int getDefaultHeight(){
		return 50;
	}
	
	protected String getDefaultClass(BuildSession session, ViewContext context){
		CompositeMap view = context.getView();
		TextAreaConfig tac = TextAreaConfig.getInstance(view);
		String wrapClass = DEFAULT_CLASS;
		if(tac.isReadOnly()) {
			wrapClass += " "+CLASSNAME_READONLY;
		}
		return wrapClass;
	}
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		Map map = context.getMap();
		TextAreaConfig tac = TextAreaConfig.getInstance(view);
		boolean readOnly = tac.isReadOnly();
		if(readOnly) {
			map.put(TextAreaConfig.PROPERTITY_READONLY, "readonly");
		}
		addConfig(TextAreaConfig.PROPERTITY_READONLY, Boolean.valueOf(readOnly));
		map.put(ComponentConfig.PROPERTITY_TAB_INDEX, new Integer(tac.getTabIndex()));
		map.put(CONFIG, getConfigString());
	}
}
