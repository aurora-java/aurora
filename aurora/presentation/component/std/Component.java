package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

public class Component {
	
	private static int idIndex = 1;
	
	protected static final String PROPERTITY_ID = "id";
	protected static final String PROPERTITY_NAME = "name";
	protected static final String PROPERTITY_STYLE = "style";
	protected static final String PROPERTITY_VALUE = "value";
	protected static final String PROPERTITY_CONFIG = "config";
	protected static final String PROPERTITY_EVENTS = "events";
	protected static final String PROPERTITY_CLASSNAME = "className";
	
	protected static final String WRAP_CSS = "wrapClass";
	
	
	protected static final String ID_INDEX = "_id_index";
	
	private StringBuffer esb = new StringBuffer();
	
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		Map map = context.getMap();
//		map.put(ID_INDEX, new Integer(1));//TODO:不管用
	}
	
	public void onCreateViewContent(BuildSession session, ViewContext context){
		CompositeMap view = context.getView();
		Map map = context.getMap();
		
//		int idIndex = ((Integer)map.get(ID_INDEX)).intValue();
		
		/** ID属性 **/
		String id = view.getString(PROPERTITY_ID);
		if("".equals(id)) {
			id= "aid-"+(idIndex++);
//			map.put(ID_INDEX, new Integer(idIndex));
		}
		map.put(PROPERTITY_ID, id);
		
		/** NAME属性 **/
		String name = view.getString(PROPERTITY_NAME);
		if("".equals(name)) {
			name= "aname-"+(idIndex++);
//			map.put(ID_INDEX, new Integer(idIndex));
		}
		map.put(PROPERTITY_NAME, name);

		
		/** 值 **/
		String value = view.getString(PROPERTITY_VALUE);
		if(value != null) {			
			map.put(PROPERTITY_VALUE, value);
		}
		
		/** 组件注册事件 **/
		CompositeMap events = view.getChild(PROPERTITY_EVENTS);
		if(events != null){
			List list = events.getChilds();
			if(list != null){
				Iterator it = list.iterator();
				while(it.hasNext()){
					CompositeMap event = (CompositeMap)it.next();
					String eventName = event.getString("type", "");
					String handler = event.getString("handler", "");
					if(!"".equals(eventName) && !"".equals(handler));
					addEvent(id, eventName,handler);
				}
				
			}
		}
		map.put(PROPERTITY_EVENTS, esb.toString());
	}
	
	
	/**
	 * 增加ClassName
	 */
	public void addClassName(CompositeMap view, Map map){
		String className = view.getString(PROPERTITY_CLASSNAME, "");
		if(!"".equals(className)) {
			map.put(PROPERTITY_CLASSNAME, className);
		}		
	}
	
	/**
	 * 增加Style
	 */
	public void addStyle(CompositeMap view, Map map){
		String style = view.getString(PROPERTITY_STYLE, "");
		if(!"".equals(style)) {
			map.put(PROPERTITY_STYLE, "style='"+style+"'");
		}		
	}
	
	protected void addEvent(String id, String eventName, String handler){
		esb.append(id+".on('" + eventName + "'," + handler + ");\n");
	}
}
