package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import aurora.application.config.ScreenConfig;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.DataSetConfig;
import aurora.presentation.component.std.config.EventConfig;
import aurora.presentation.markup.HtmlPageContext;
import aurora.service.IService;
import aurora.service.ServiceInstance;
import aurora.service.http.HttpServiceInstance;

/**
 * 
 * @version $Id: Component.java v 1.0 2010-5-11 下午04:43:07 znjqolf Exp $
 * @author <a href="mailto:znjqolf@126.com">vincent</a>
 */
public class Component {
	
	protected static final String CONFIG = "config";
	protected static final String WRAP_CSS = "wrapClass";
	protected static final String BINDING = "binding";
	
	protected String id;
	protected StringBuffer esb = new StringBuffer();
	private StringBuffer bsb = new StringBuffer();
	private JSONObject config = new JSONObject();
	
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		addStyleSheet(session, context, "core/Aurora-all.css");
		addJavaScript(session, context, "core/ext-core-min.js");
		addJavaScript(session, context, "core/Aurora-all.js");
	}
	
	protected String getDefaultClass(BuildSession session, ViewContext context){
		return "";
	}
	
	protected int getDefaultWidth(){
		return 150;
	}
	
	protected int getDefaultHeight(){
		return 20;
	}
	
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
		CompositeMap view = context.getView();
		CompositeMap model = context.getModel();
		Map map = context.getMap();
		
		/** ID属性 **/
		id = view.getString(ComponentConfig.PROPERTITY_ID, "");
		if("".equals(id)) {
			id = IDGenerator.getInstance().generate();
		}
//		view.put(ComponentConfig.PROPERTITY_ID, id);
		map.put(ComponentConfig.PROPERTITY_ID, id);
		addConfig(ComponentConfig.PROPERTITY_ID, id);
		
		String clazz = getDefaultClass(session, context);
		String className = view.getString(ComponentConfig.PROPERTITY_CLASSNAME, "");
		if(!"".equals(className)) {
			clazz += " " + className;
		}
		map.put(WRAP_CSS, clazz);
		
		CompositeMap vwc = null,vhc = null;
		String vws = null,vhs = null;
		Integer vw = null,vh = null;
		CompositeMap root = view.getRoot();
		if(root !=null) {			
			vws = (String)root.getObject("/parameter/@_vw");
			vhs = (String)root.getObject("/parameter/@_vh");			
			if(vws==null){
				vwc = (CompositeMap)root.getObject("/cookie/@vw");
				if(vwc !=null){
					vw = vwc.getInt("value");
				}
			}else{
				vw = Integer.valueOf(vws);
			}
			if(vhs==null){
				vhc = (CompositeMap)root.getObject("/cookie/@vh");
				if(vhc !=null){
					vh = vhc.getInt("value");
				}
			}else{
				vh = Integer.valueOf(vhs);
			}			
		}

		/** Width属性**/
		String widthStr = view.getString(ComponentConfig.PROPERTITY_WIDTH, ""+getDefaultWidth());
		String wstr = uncertain.composite.TextParser.parse(widthStr, model);
		Integer width = "".equals(wstr) ? new Integer(getDefaultWidth()) : Integer.valueOf(wstr);
		map.put(ComponentConfig.OLD_WIDTH, width);
		int marginWidth = view.getInt("marginwidth",0);
		if(marginWidth!=0&&vw!=null) 
			width = new Integer((vw.intValue() - marginWidth) > width.intValue() ? (vw.intValue() - marginWidth) :  width.intValue());
		map.put(ComponentConfig.PROPERTITY_WIDTH, width);
		
		/** Height属性**/
		String heightStr = view.getString(ComponentConfig.PROPERTITY_HEIGHT, ""+getDefaultHeight());
		String hstr = uncertain.composite.TextParser.parse(heightStr, model);
		Integer height = "".equals(hstr) ? new Integer(getDefaultHeight()) :  Integer.valueOf(hstr);
		int marginHeight = view.getInt("marginheight",0);
		if(marginHeight!=0&&vh!=null)
			height = new Integer((vh.intValue() - marginHeight)>height.intValue() ? (vh.intValue() - marginHeight) : height.intValue());
		if(height.intValue() !=0) map.put(ComponentConfig.PROPERTITY_HEIGHT, height);
		
		/** NAME属性 **/
		String name = view.getString(ComponentConfig.PROPERTITY_NAME, "");
		if("".equals(name)) {
			name= IDGenerator.getInstance().generate();
		}
		map.put(ComponentConfig.PROPERTITY_NAME, name);
		
		String style = view.getString(ComponentConfig.PROPERTITY_STYLE, "");
//		if(!"".equals(style)) {
			map.put(ComponentConfig.PROPERTITY_STYLE, style);
//		}

		
		/** 值 **/
		String value = view.getString(ComponentConfig.PROPERTITY_VALUE, "");		
		map.put(ComponentConfig.PROPERTITY_VALUE, value);
		
		/** 组件注册事件 **/
		CompositeMap events = view.getChild(ComponentConfig.PROPERTITY_EVENTS);
		if(events != null){
			List list = events.getChilds();
			if(list != null){
				Iterator it = list.iterator();
				while(it.hasNext()){
					CompositeMap event = (CompositeMap)it.next();
					EventConfig eventConfig = EventConfig.getInstance(event);
					String eventName = eventConfig.getEventName();//event.getString(ComponentConfig.PROPERTITY_EVENT_NAME, "");
					String handler = eventConfig.getHandler();//event.getString(ComponentConfig.PROPERTITY_EVENT_HANDLER, "");
					if(!"".equals(eventName) && !"".equals(handler));
					addEvent(id, eventName,handler);
				}
				
			}
		}
		map.put(ComponentConfig.PROPERTITY_EVENTS, esb.toString());
		
		/** 绑定DataSet **/
		String bindTarget = view.getString(ComponentConfig.PROPERTITY_BINDTARGET, "");
		if(!bindTarget.equals("")){	
			bsb.append("$('"+id+"').bind('" + bindTarget + "','" + name + "');\n");
			map.put(BINDING, bsb.toString());
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
	
	
	protected String getFieldPrompt(BuildSession session, CompositeMap field, String dataset){
		String label = field.getString(ComponentConfig.PROPERTITY_PROMPT, "");
		if("".equals(label)){
			String name = field.getString(ComponentConfig.PROPERTITY_NAME, "");
			CompositeMap ds = getDataSet(session, dataset);
			if(ds!=null){
				CompositeMap fieldcm = ds.getChild(DataSetConfig.PROPERTITY_FIELDS);
				if(fieldcm !=null){
					List fields = fieldcm.getChilds();
					Iterator it = fields.iterator();
					while(it.hasNext()){
						CompositeMap fieldMap = (CompositeMap)it.next();
						String fn = fieldMap.getString(ComponentConfig.PROPERTITY_NAME,"");
						if(name.equals(fn)){
							label = fieldMap.getString(ComponentConfig.PROPERTITY_PROMPT,"");
							break;
						}
					}
				}
			}
		}
		return label;
	}
	
	private CompositeMap getDataSet(BuildSession session, String dataSetName){
		CompositeMap dataset = null;
		ServiceInstance svc = (ServiceInstance)session.getInstanceOfType(IService.class);
        ScreenConfig screen = ScreenConfig.createScreenConfig(svc.getServiceConfigData());
        CompositeMap datasets = screen.getDataSetsConfig();
        if(datasets!=null){
	        List list = datasets.getChilds();
	        Iterator it =list.iterator();
	        while(it.hasNext()){
	        	CompositeMap ds = (CompositeMap)it.next();
	        	String dsname = ds.getString("id", "");
	        	if(dataSetName.equals(dsname)){
	        		dataset = ds;
	        		break;
	        	}
	        }
        }
        return dataset;
	}
}
