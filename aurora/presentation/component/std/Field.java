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
public class Field extends Component {
	
	protected static final String CLASSNAME_WRAP = "item-wrap";
	
	protected static final String PROPERTITY_REQUIRED = "required";
	protected static final String PROPERTITY_READONLY = "readonly";
	
	protected static final String CLASSNAME_NOTBLANK = "item-notBlank";
	protected static final String CLASSNAME_READONLY = "item-readOnly";

	
	
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
	}

	public void onCreateViewContent(BuildSession session, ViewContext context){
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		Map map = context.getMap();
		
		/** 包装样式 **/
		map.put(WRAP_CSS, CLASSNAME_WRAP);
		
		/** 是否为空 **/
		Boolean notBlank = view.getBoolean(PROPERTITY_REQUIRED, false);
		map.put(PROPERTITY_REQUIRED, notBlank);
		if(notBlank) {
			String wrapClass = (String)map.get(WRAP_CSS);
			wrapClass += " " + CLASSNAME_NOTBLANK;
			map.put(WRAP_CSS, wrapClass);
			addConfig(PROPERTITY_REQUIRED, notBlank);
		}
		addConfig(PROPERTITY_REQUIRED, notBlank);
		
		/** 是否只读 **/
		Boolean readOnly = view.getBoolean(PROPERTITY_READONLY, false);
		if(readOnly) {
			String wrapClass = (String)map.get(WRAP_CSS);
			wrapClass += " "+CLASSNAME_READONLY;
			map.put(WRAP_CSS, wrapClass);
			map.put(PROPERTITY_READONLY, "readonly");
		}
		addConfig(PROPERTITY_READONLY, readOnly);
	}
	
	
}
