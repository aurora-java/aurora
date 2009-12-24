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
		
	public static final String PROPERTITY_ID = "id";
	public static final String PROPERTITY_LABEL = "prompt";
	public static final String PROPERTITY_NAME = "name";
	public static final String PROPERTITY_STYLE = "style";
	public static final String PROPERTITY_VALUE = "value";
	public static final String PROPERTITY_CONFIG = "config";
	public static final String PROPERTITY_EVENTS = "events";
	public static final String PROPERTITY_BINDING = "binding";
	public static final String PROPERTITY_CLASSNAME = "classname";
	public static final String PROPERTITY_WIDTH = "width";
	public static final String PROPERTITY_HEIGHT = "height";
	public static final String PROPERTITY_BINDTARGET = "bindtarget";
	public static final String PROPERTITY_BINDNAME = "bindname";
	public static final String PROPERTITY_HIDDEN = "hidden";
	
	protected static final String WRAP_CSS = "wrapClass";
	
//	public static final String ID_INDEX = "cid_index";
	
	protected String id;
	protected StringBuffer esb = new StringBuffer();
	private StringBuffer bsb = new StringBuffer();
	private JSONObject config = new JSONObject();
	
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		addStyleSheet(session, context, "core/Aurora-all.css");
		addJavaScript(session, context, "core/ext-core-min.js");
		addJavaScript(session, context, "core/Aurora-all.js");
	}
	
	protected int getDefaultWidth(){
		return 150;
	}
	
	protected int getDefaultHeight(){
		return 20;
	}
	
	@SuppressWarnings("unchecked")
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
		CompositeMap view = context.getView();
		Map map = context.getMap();
		
		/** ID属性 **/
		id = view.getString(PROPERTITY_ID, "");
		if("".equals(id)) {
			id = IDGenerator.getInstance().generate();
		}
		view.put(PROPERTITY_ID, id);
		map.put(PROPERTITY_ID, id);
		addConfig(PROPERTITY_ID, id);
		
		/** Width属性**/
		Integer width = Integer.valueOf(view.getString(PROPERTITY_WIDTH, ""+getDefaultWidth()));
		map.put(PROPERTITY_WIDTH, width);
		
		/** Height属性**/
		Integer height = Integer.valueOf(view.getString(PROPERTITY_HEIGHT, ""+getDefaultHeight()));
		if(height.intValue() !=0) map.put(PROPERTITY_HEIGHT, height);
		
		/** NAME属性 **/
		String name = view.getString(PROPERTITY_NAME, "");
		if("".equals(name)) {
			name= IDGenerator.getInstance().generate();
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
					String eventName = event.getString("name", "");
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
			bsb.append("$('"+id+"').bind('" + bindTarget + "','" + bindName + "');\n");
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
	@SuppressWarnings("unchecked")
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
		esb.append("$('"+id+"').on('" + eventName + "'," + handler + ");\n");
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
