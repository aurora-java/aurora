package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import aurora.application.ApplicationConfig;
import aurora.application.ApplicationViewConfig;
import aurora.application.IApplicationConfig;
import aurora.application.config.ScreenConfig;
import aurora.application.features.cstm.CustomSourceCode;
import aurora.application.features.cstm.CustomizationDataProvider;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.DataSetConfig;
import aurora.presentation.component.std.config.EventConfig;
import aurora.presentation.component.std.config.FieldConfig;
import aurora.presentation.markup.HtmlPageContext;
import aurora.service.IService;
import aurora.service.ServiceInstance;

/**
 * 
 * @version $Id$
 * @author <a href="mailto:znjqolf@126.com">vincent</a>
 */
@SuppressWarnings("unchecked")
public class Component {
	
	public static final String VERSION = "$Revision$";

	protected static final String CONFIG = "config";
	protected static final String WRAP_CSS = "wrapClass";
	protected static final String BINDING = "binding";
	
	public static final String THEME_DEFAULT = "default";
	public static final String THEME_MAC = "mac";

	protected String id;
	// protected StringBuilder esb = new StringBuilder();
	private JSONObject listeners = new JSONObject();
	private StringBuilder bsb = new StringBuilder();
	private JSONObject config = new JSONObject();
	protected ApplicationConfig mApplicationConfig;	
	
	public Component(IObjectRegistry registry){
        mApplicationConfig = (ApplicationConfig) registry.getInstanceOfType(IApplicationConfig.class);
	}

	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {

		addStyleSheet(session, context, "base/Aurora-all-min.css");
		addJavaScript(session, context, "base/ext-core-min.js");
		addJavaScript(session, context, "base/Aurora-all-min.js");
		addJavaScript(session, context, "locale/aurora-lang-" + session.getLanguage() + ".js");

	}

	protected String getDefaultClass(BuildSession session, ViewContext context) {
		return "";
	}

	protected int getDefaultWidth() {
		return 150;
	}

	protected int getDefaultHeight() {
		return 20;
	}

	protected Integer getComponentWidth(CompositeMap model, CompositeMap view, Map map) {
		CompositeMap root = model.getRoot();
		CompositeMap vwc = null;
		ComponentConfig cc = new ComponentConfig();
		cc.initialize(view);
		
		String vws = null;
		Integer vw = null;
		if (root != null) {
			vws = (String) root.getObject("/parameter/@_vw");
			if (vws == null) {
				vwc = (CompositeMap) root.getObject("/cookie/@vw");
				if (vwc != null&& !vwc.get("value").equals("NaN")) {
					vw = vwc.getInt("value");
				}
			} else {
				vw = Integer.valueOf(vws);
			}
		}
		String widthStr = view.getString(ComponentConfig.PROPERTITY_WIDTH, ""+ getDefaultWidth());
		String wstr = uncertain.composite.TextParser.parse(widthStr, model);
		Integer width = "".equals(wstr) ? new Integer(getDefaultWidth()) : Integer.valueOf(wstr);
		map.put(ComponentConfig.PROPERTITY_OLD_WIDTH, width);
		Integer marginWidth = cc.getMarginWidth(model);
		if (marginWidth != null && vw != null) {
			width = new Integer((vw.intValue() - marginWidth.intValue()) > 0 ? (vw.intValue() - marginWidth.intValue()) : width.intValue());
			//中集特殊做法！
			//width = new Integer((vw.intValue() - marginWidth.intValue()) < width.intValue() ? (vw.intValue() - marginWidth.intValue()) : width.intValue());
			addConfig(ComponentConfig.PROPERTITY_MARGIN_WIDTH, marginWidth);
		}
		return width;
	}

	protected Integer getComponentHeight(CompositeMap model, CompositeMap view, Map map) {
		CompositeMap root = model.getRoot();
		CompositeMap vhc = null;
		ComponentConfig cc = new ComponentConfig();
		cc.initialize(view);
		
		String vhs = null;
		Integer vh = null;
		if (root != null) {
			vhs = (String) root.getObject("/parameter/@_vh");
			if (vhs == null) {
				vhc = (CompositeMap) root.getObject("/cookie/@vh");
				if (vhc != null && !vhc.get("value").equals("NaN")) {
					
					vh = vhc.getInt("value");
				}
			} else {
				vh = Integer.valueOf(vhs);
			}
		}
		String heightStr = view.getString(ComponentConfig.PROPERTITY_HEIGHT, "" + getDefaultHeight());
		String hstr = uncertain.composite.TextParser.parse(heightStr, model);
		Integer height = "".equals(hstr) ? new Integer(getDefaultHeight()) : Integer.valueOf(hstr);
		Integer marginHeight = cc.getMarginHeight(model);
		if (marginHeight != null && vh != null) {
//			height = new Integer((vh.intValue() - marginHeight.intValue()) > height.intValue() ? (vh.intValue() - marginHeight.intValue()) : height.intValue());
			height = new Integer((vh.intValue() - marginHeight.intValue()) > 0 ? (vh.intValue() - marginHeight.intValue()) : height.intValue());
			addConfig(ComponentConfig.PROPERTITY_MARGIN_HEIGHT, marginHeight);
		}
		return height;
	}

	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException {
		CompositeMap view = context.getView();
		CompositeMap model = context.getModel();
		Map map = context.getMap();
		ComponentConfig cc = new ComponentConfig();
		cc.initialize(view);
		ApplicationViewConfig view_config = mApplicationConfig.getApplicationViewConfig();
		
		Boolean isCust = cc.isCust();
		/** ID属性 * */
		id = cc.getId();
		id = uncertain.composite.TextParser.parse(id, model);
		if (id == null || "".equals(id)) {
			id = IDGenerator.getInstance().generate();
		}else if(isCust==null){
			isCust =  new Boolean(true);
		}
		addConfig(ComponentConfig.PROPERTITY_IS_CUST,isCust);
		map.put(ComponentConfig.PROPERTITY_ID, id);
		addConfig(ComponentConfig.PROPERTITY_ID, id);

		String hostId = cc.getHostId();
		if(null != hostId){
			addConfig(ComponentConfig.PROPERTITY_HOST_ID, hostId);
		}
		String clazz = getDefaultClass(session, context);
		String className = cc.getClassName();
		if (className != null) {
			clazz += " " + className;
		}
		map.put(WRAP_CSS, clazz);
		
		map.put(ComponentConfig.PROPERTITY_TAB_INDEX, cc.getTabIndex());

		/** Width属性 * */
		Integer width = getComponentWidth(model, view, map);
		map.put(ComponentConfig.PROPERTITY_WIDTH, width);
		addConfig(ComponentConfig.PROPERTITY_WIDTH, width);

		/** Height属性 * */
		Integer height = getComponentHeight(model, view, map);
		if (height.intValue() != 0) {
			map.put(ComponentConfig.PROPERTITY_HEIGHT, height);
			addConfig(ComponentConfig.PROPERTITY_HEIGHT, height);
		}

		/** NAME属性 * */
		String name = cc.getName();
		if (name == null) {
			name = IDGenerator.getInstance().generate();
		}
		map.put(ComponentConfig.PROPERTITY_NAME, name);

		String style = cc.getStyle();
		map.put(ComponentConfig.PROPERTITY_STYLE, style == null ? "" : style);

		/** 值 * */
		String value = cc.getValue();
		map.put(ComponentConfig.PROPERTITY_VALUE, value == null ?  "" : value);
		
		addConfig(ComponentConfig.PROPERTITY_CLIENT_RESIZE, cc.isClientResize()==null?view_config.getDefaultClientResize():cc.isClientResize());
		
		/** 是否隐藏 **/
		boolean hidden = cc.getHidden(false);
		if(hidden != false)
		addConfig(FieldConfig.PROPERTITY_HIDDEN, Boolean.valueOf(hidden));
		
		/** 组件注册事件 * */
		CompositeMap events = view.getChild(ComponentConfig.PROPERTITY_EVENTS);
		if (events != null) {
			List list = events.getChilds();
			if (list != null) {
				Iterator it = list.iterator();
				while (it.hasNext()) {
					CompositeMap event = (CompositeMap) it.next();
					EventConfig eventConfig = EventConfig.getInstance(event);
					String eventName = eventConfig.getEventName();// event.getString(ComponentConfig.PROPERTITY_EVENT_NAME,// "");
					String handler = eventConfig.getHandler();// event.getString(ComponentConfig.PROPERTITY_EVENT_HANDLER,// "");
					if (!"".equals(eventName) && !"".equals(handler))
						handler = uncertain.composite.TextParser.parse(handler, model);
						addEvent(id, eventName, handler);
				}

			}
		}
		// map.put(ComponentConfig.PROPERTITY_EVENTS, esb.toString());
		addConfig("listeners", listeners);

		/** 绑定DataSet * */
		String bindTarget = cc.getBindTarget();
		if (!bindTarget.equals("")) {
			bindTarget = uncertain.composite.TextParser.parse(bindTarget, model);
			map.put(ComponentConfig.PROPERTITY_BINDTARGET, bindTarget);
			bsb.append("$('" + id + "').bind('" + bindTarget + "','" + name + "');\n");
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
		if (!session.includeResource(javascript)) {
			HtmlPageContext page = HtmlPageContext.getInstance(context);
			String js = session.getResourceUrl(javascript);
			page.addScript(js);
		}
	}

	/**
	 * 加入StyleSheet
	 * 
	 * @param session
	 * @param context
	 * @param style
	 * @return String
	 */
	protected void addStyleSheet(BuildSession session, ViewContext context, String style) {
		if (!session.includeResource(style)) {
			HtmlPageContext page = HtmlPageContext.getInstance(context);
			String styleSheet = session.getResourceUrl(style);
			page.addStyleSheet(styleSheet);
		}
	}

	/**
	 * 增加事件
	 * 
	 * @param id 组件ID
	 * @param eventName  事件名
	 * @param handler 事件函数
	 */
	protected void addEvent(String id, String eventName, String handler) {
		// esb.append("$('"+id+"').on('" + eventName + "'," + handler + ");\n");
		try {
			listeners.put(eventName, new JSONFunction(handler));
		} catch (JSONException e) {
		}
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
	 * 
	 * @return
	 */
	protected String getConfigString() {
		return config.toString();
	}
	
	protected JSONObject getConfig(){
		return config;
	}

	protected String getFieldPrompt(BuildSession session, CompositeMap field, String dataset) {
		String label = field.getString(ComponentConfig.PROPERTITY_PROMPT, "");
		if ("".equals(label)) {
			String name = field.getString(ComponentConfig.PROPERTITY_NAME, "");
			CompositeMap ds = getDataSet(session, dataset);
			if (ds != null) {
				CompositeMap fieldcm = ds.getChild(DataSetConfig.PROPERTITY_FIELDS);
				if (fieldcm != null) {
					List fields = fieldcm.getChilds();
					Iterator it = fields.iterator();
					while (it.hasNext()) {
						CompositeMap fieldMap = (CompositeMap) it.next();
						String fn = fieldMap.getString(ComponentConfig.PROPERTITY_NAME, "");
						if (name.equals(fn)) {
							label = fieldMap.getString(ComponentConfig.PROPERTITY_PROMPT, "");
							break;
						}
					}
				}
			}
		}
		return label;
	}

	private CompositeMap getDataSet(BuildSession session, String dataSetName) {
		CompositeMap dataset = null;
		ServiceInstance svc = (ServiceInstance) session.getInstanceOfType(IService.class);
		ScreenConfig screen = ScreenConfig.createScreenConfig(svc.getServiceConfigData());
		CompositeMap datasets = screen.getDataSetsConfig();
		if (datasets != null) {
			List list = datasets.getChilds();
			Iterator it = list.iterator();
			while (it.hasNext()) {
				CompositeMap ds = (CompositeMap) it.next();
				String dsname = ds.getString("id", "");
				if (dataSetName.equals(dsname)) {
					dataset = ds;
					break;
				}
			}
		}
		return dataset;
	}
	
	public boolean isHidden(CompositeMap view, CompositeMap model){
		CompositeMap cd = model.getParent().getChild(CustomizationDataProvider.DEFAULT_CUSTOM_DATA);
		if(cd!=null){
			List list = cd.getChilds();
			Iterator it = list.iterator();
			String fid = view.getString(ComponentConfig.PROPERTITY_ID, "");
			while(it.hasNext()){
				CompositeMap record = (CompositeMap)it.next();
				String id = record.getString(CustomSourceCode.KEY_ID_VALUE);
				String mt = record.getString(CustomSourceCode.KEY_MOD_TYPE);
				String ak = record.getString(CustomSourceCode.KEY_ATTRIB_KEY);
				String av = record.getString(CustomSourceCode.KEY_ATTRIB_VALUE);
				String an = record.getString(CustomSourceCode.KEY_ARRAY_NAME);
				String idf = record.getString(CustomSourceCode.KEY_INDEX_FIELD);
				String idv = record.getString(CustomSourceCode.KEY_INDEX_VALUE);
				if("set_attrib".equals(mt) && id.equals(fid)&&"hidden".equals(ak)&&"true".equals(av)&&an==null&&idf==null&&idv==null){
					return true;
				}
			}
		}
		return false;
	}
	
	public int getChildLength(CompositeMap view, CompositeMap model){
		int count = 0;
		Iterator it = view.getChildIterator();
		if(it!=null)
		while(it.hasNext()){
			CompositeMap field = (CompositeMap)it.next();
			if(isHidden(field,model)){
				continue;
			}else{
				count ++;
			}
		}
		return count;
	}
}

class JSONFunction implements JSONString {
	private String funciton;

	public JSONFunction(String func) {
		funciton = func;
	}

	public String toJSONString() {
		return funciton;
	}

}
