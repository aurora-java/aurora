package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.markup.HtmlPageContext;

public class Component {
		
	protected static final String PROPERTITY_ID = "id";
	protected static final String PROPERTITY_NAME = "name";
	protected static final String PROPERTITY_STYLE = "style";
	protected static final String PROPERTITY_VALUE = "value";
	protected static final String PROPERTITY_CONFIG = "config";
	protected static final String PROPERTITY_EVENTS = "events";
	protected static final String PROPERTITY_BINDING = "binding";
	protected static final String PROPERTITY_CLASSNAME = "className";
	protected static final String PROPERTITY_WIDTH = "width";
	protected static final String PROPERTITY_BINDTARGET = "bindTarget";
	protected static final String PROPERTITY_BINDNAME = "bindName";
	
	protected static final String WRAP_CSS = "wrapClass";
	
	protected static final String ID_INDEX = "_id_index";
	
	private StringBuffer esb = new StringBuffer();
	private StringBuffer bsb = new StringBuffer();
	private JSONObject config = new JSONObject();
	
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		session.getSessionContext().put(ID_INDEX, new Integer(1));
		addStyleSheet(session, context, "core/Aurora.css");
		addJavaScript(session, context, "core/ext-core.js");
		addJavaScript(session, context, "core/Aurora.js");
		addJavaScript(session, context, "core/Component.js");
	}
	
	public void onCreateViewContent(BuildSession session, ViewContext context){
		CompositeMap view = context.getView();
		Map map = context.getMap();
		
		int idIndex = ((Integer)session.getSessionContext().get(ID_INDEX)).intValue();
		
		/** ID属性 **/
		String id = view.getString(PROPERTITY_ID, "");
		if("".equals(id)) {
			id= "aid-"+(idIndex++);
			session.getSessionContext().put(ID_INDEX, new Integer(idIndex));
		}
		map.put(PROPERTITY_ID, id);
		addConfig(PROPERTITY_ID, id);
		
		/** Width属性**/
		Integer width = Integer.valueOf(view.getString(PROPERTITY_WIDTH, "150"));//TODO:默认值??
		map.put(PROPERTITY_WIDTH, width);
		
		/** NAME属性 **/
		String name = view.getString(PROPERTITY_NAME, "");
		if("".equals(name)) {
			name= "aname-"+(idIndex++);
			session.getSessionContext().put(ID_INDEX, new Integer(idIndex));
		}
		map.put(PROPERTITY_NAME, name);
		
		String style = view.getString(PROPERTITY_STYLE, "");
		if(!"".equals(style)) {
			map.put(PROPERTITY_STYLE, style);
		}

		
		/** 值 **/
		String value = view.getString(PROPERTITY_VALUE, "");		
		map.put(PROPERTITY_VALUE, value);
		
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
		
		/** 绑定DataSet **/
		String bindTarget = view.getString(PROPERTITY_BINDTARGET, "");
		String bindName = "";
		if(!bindTarget.equals("")){			
			bindName = view.getString(PROPERTITY_BINDNAME, "");
			if(bindName.equals("")){
				bindName = name;				
			}
			bsb.append(id+".bind(" + bindTarget + ",'" + bindName + "');\n");
			map.put(PROPERTITY_BINDING, bsb.toString());
		}
	}
	
	
	/**
	 * 加入JavaScript
	 * 
	 * @param session
	 * @param context
	 * @param javascript
	 * @return String
	 */
	protected void addJavaScript(BuildSession session, ViewContext context, String javascript) {
		HtmlPageContext page = HtmlPageContext.getInstance(context);
        String js = session.getResourceUrl(javascript);
        page.addScript(js);
	}
	
	/**
	 * 加入StyleSheet
	 * 
	 * @param session
	 * @param context
	 * @param style
	 * @return String
	 */
	protected void addStyleSheet(BuildSession session, ViewContext context,String style) {
		HtmlPageContext page = HtmlPageContext.getInstance(context);
        String styleSheet = session.getResourceUrl(style);
        page.addStyleSheet(styleSheet);
	}
	
	
	/**
	 * 增加ClassName
	 */
	protected void addClassName(CompositeMap view, Map map){
		String className = view.getString(PROPERTITY_CLASSNAME, "");
		if(!"".equals(className)) {
			map.put(PROPERTITY_CLASSNAME, className);
		}		
	}
	
	/**
	 * 增加事件
	 * 
	 * @param id 组件ID
	 * @param eventName 事件名
	 * @param handler 事件函数
	 */
	protected void addEvent(String id, String eventName, String handler){
		esb.append(id+".on('" + eventName + "'," + handler + ");\n");
	}
	
	/**
	 * 增加配置信息.
	 * 
	 * @param key 名称
	 * @param value 值
	 */
	protected void addConfig(String key, Object value) {
		try {
			config.put(key, value);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 配置信息.
	 * @return
	 */
	protected String getConfigString(){
		return config.toString();
	}
}
