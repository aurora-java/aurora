package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

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



	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		Map map = context.getMap();
		
		/** 包装样式 **/
		map.put(WRAP_CSS, CLASSNAME_WRAP);
		
		/** 是否为空 **/
		boolean notBlank = view.getBoolean(PROPERTITY_REQUIRED, false);
		map.put(PROPERTITY_REQUIRED, Boolean.valueOf(notBlank));
		if(notBlank) {
			String wrapClass = (String)map.get(WRAP_CSS);
			wrapClass += " " + CLASSNAME_NOTBLANK;
			map.put(WRAP_CSS, wrapClass);
			addConfig(PROPERTITY_REQUIRED, Boolean.valueOf(notBlank));
		}
		addConfig(PROPERTITY_REQUIRED, Boolean.valueOf(notBlank));
		
		/** 是否隐藏 **/
		boolean hidden = view.getBoolean(PROPERTITY_HIDDEN, false);
		if(hidden != false)
//		map.put(PROPERTITY_HIDDEN, Boolean.valueOf(hidden));
		addConfig(PROPERTITY_HIDDEN, Boolean.valueOf(hidden));
		
		/** 是否只读 **/
		boolean readOnly = view.getBoolean(PROPERTITY_READONLY, false);
		if(readOnly) {
			String wrapClass = (String)map.get(WRAP_CSS);
			wrapClass += " "+CLASSNAME_READONLY;
			map.put(WRAP_CSS, wrapClass);
			map.put(PROPERTITY_READONLY, "readonly");
		}
		addConfig(PROPERTITY_READONLY, Boolean.valueOf(readOnly));
	}
	
	
}
