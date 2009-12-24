package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

public class InputField extends Field {
	
	protected static final String PROPERTITY_EMPTYTEXT = "emptytext";
	protected static final String PROPERTITY_INPUTWIDTH = "inputwidth";	

	protected static final String CLASSNAME_EMPTYTEXT = "item-emptyText";
	
	
	@SuppressWarnings("unchecked")
	public void onCreateViewContent(BuildSession session, ViewContext context)throws IOException{
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		Map map = context.getMap();
		
		/** 输入框宽度**/
		Integer width = (Integer)map.get(PROPERTITY_WIDTH);
		map.put(PROPERTITY_INPUTWIDTH, new Integer(width.intValue()-3));
		
		/** 值 **/
		String value = (String)map.get(PROPERTITY_VALUE);
		
		/** 文本提示 **/
		String emptyText = view.getString(PROPERTITY_EMPTYTEXT,"");
		if(!"".equals(emptyText) && "".equals(value)) {
			String wrapClass = (String)map.get(WRAP_CSS);
			wrapClass += " " + CLASSNAME_EMPTYTEXT;
			map.put(WRAP_CSS, wrapClass);
			map.put(PROPERTITY_VALUE, emptyText);
			addConfig(PROPERTITY_EMPTYTEXT, emptyText);
		}
	
	}

}
