package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.markup.HtmlPageContext;
import uncertain.composite.CompositeMap;

/**
 * 带Input类型的组件基类.
 * 
 * @version $Id: Field.java v 1.0 2009-7-20 上午11:29:55 znjqolf Exp $
 * @author <a href="mailto:znjqolf@126.com">vincent</a>
 * 
 * notBlank:是否为空  true|false
 * readOnly:是否只读  true|false
 * emptyText:输入提示文字	String
 * className:额外样式名	String
 * style:样式描述			String
 */
public class Field extends Component{
	
	protected static final String PROPERTITY_NOTBLANK = "notBlank";
	protected static final String PROPERTITY_READONLY = "readOnly";
	protected static final String PROPERTITY_EMPTYTEXT = "emptyText";
	protected static final String PROPERTITY_INPUTWIDTH = "inputWidth";
	
	protected static final String CLASSNAME_WRAP = "item-wrap";
	protected static final String CLASSNAME_NOTBLANK = "item-notBlank";
	protected static final String CLASSNAME_READONLY = "item-readOnly";

	protected static final String CLASSNAME_EMPTYTEXT = "item-emptyText";
	
	
	private JSONObject config = new JSONObject();
	
	
	/**
	 * 加载Aurora组件库以及样式文件
	 * 
	 * @param session
	 * @param context
	 * @throws IOException
	 */
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		super.onPreparePageContent(session, context);
		addStyleSheet(session, context, "core/Aurora.css");
		addJavaScript(session, context, "core/ext-core.js");
		addJavaScript(session, context, "core/Aurora.js");
		addJavaScript(session, context, "core/Component.js");
		addJavaScript(session, context, "core/Field.js");
	}

	public void onCreateViewContent(BuildSession session, ViewContext context){
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		Map map = context.getMap();
		
		/** 包装样式 **/
		map.put(WRAP_CSS, CLASSNAME_WRAP);
		
		/** 输入框宽度**/
		Integer width = (Integer)map.get(PROPERTITY_WIDTH);
		map.put(PROPERTITY_INPUTWIDTH, new Integer(width.intValue()-3));
		
		
		/** 是否为空 **/
		String notBlank = view.getString(PROPERTITY_NOTBLANK, "false");
		map.put(PROPERTITY_NOTBLANK, notBlank);
		if("true".equals(notBlank)) {
			String wrapClass = (String)map.get(WRAP_CSS);
			wrapClass += " " + CLASSNAME_NOTBLANK;
			map.put(WRAP_CSS, wrapClass);
			addConfig(PROPERTITY_NOTBLANK, notBlank);
		}
		
		/** 是否只读 **/
		String readOnly = view.getString(PROPERTITY_READONLY,"false");
		if("true".equals(readOnly)) {
			String wrapClass = (String)map.get(WRAP_CSS);
			wrapClass += " "+CLASSNAME_READONLY;
			map.put(WRAP_CSS, wrapClass);
			map.put(PROPERTITY_READONLY, "readonly");
		}
		
		
		/** 值 **/
		String value = (String)map.get(PROPERTITY_VALUE);
//		if(value != null && !"".equals(value)) {
//			String wrapClass = (String)map.get(WRAP_CSS);
//			wrapClass = wrapClass.replaceAll(CLASSNAME_EMPTYTEXT, "");
//			map.put(WRAP_CSS, wrapClass);
//		}
		
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
	
	public void addClassName(CompositeMap view, Map map){
		String className = view.getString(PROPERTITY_CLASSNAME, "");
		if(!"".equals(className)) {
			String wrapClass = (String)map.get(WRAP_CSS);
			wrapClass += " " + className;
			map.put(WRAP_CSS, wrapClass);
		}		
	}
	
	protected void addConfig(String key, Object value) {
		try {
			config.put(key, value);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected String getConfigString(){
		return config.toString();
	}
}
