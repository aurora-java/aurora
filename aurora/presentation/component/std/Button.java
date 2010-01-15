package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

public class Button extends Field {
	
	private static final String CLASSNAME_WRAP = "item-btn";
	private static final String PROPERTITY_TEXT = "text";
	
	private static final String PROPERTITY_CLICK = "click";
	
	protected int getDefaultWidth(){
		return 60;
	}
	
	protected int getDefaultHeight(){
		return 15;
	}
	
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		Map map = context.getMap();
		String clickEvent = view.getString(PROPERTITY_CLICK, "");
		if(!"".equals(clickEvent)){
			addEvent(id, "click", clickEvent);
		}
		map.put(PROPERTITY_EVENTS, esb.toString());
		
		/** 包装样式 **/
		map.put(WRAP_CSS, CLASSNAME_WRAP);
		map.put(PROPERTITY_TEXT, view.getString(PROPERTITY_TEXT, "button"));
		
		map.put(PROPERTITY_CONFIG, getConfigString());
	}
}
