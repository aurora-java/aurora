package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;

/**
 * 组件基类.
 * 
 * @version $Id$
 * @author <a href="mailto:znjqolf@126.com">vincent</a>
 * 
 * notBlank:是否为空  true|false
 * readOnly:是否只读  true|false
 * emptyText:输入提示文字	String
 * className:额外样式名	String
 * style:样式描述			String
 */
public class Field extends Component {
	
	public static final String VERSION = "$Revision$";
	
	protected static final String CLASSNAME_WRAP = "item-wrap";
	
	protected static final String PROPERTITY_REQUIRED = "required";
	protected static final String PROPERTITY_READONLY = "readonly";
	protected static final String PROPERTITY_MAX_LENGHT = "maxlength";
	protected static final String PROPERTITY_RENDERER = "renderer";

	protected static final String CLASSNAME_NOTBLANK = "item-notBlank";
	protected static final String CLASSNAME_READONLY = "item-readOnly";
	
	private static final String CONFIG_CONTEXT = "context";
	

	protected String getDefaultClass(BuildSession session, ViewContext context){
		CompositeMap view = context.getView();
		boolean notBlank = view.getBoolean(PROPERTITY_REQUIRED, false);
		String wrapClass = CLASSNAME_WRAP;
		if(notBlank) {
			wrapClass += " " + CLASSNAME_NOTBLANK;
		}
		boolean readOnly = view.getBoolean(PROPERTITY_READONLY, false);
		if(readOnly) {
			wrapClass += " "+CLASSNAME_READONLY;
		}
		return wrapClass;
	}
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		Map map = context.getMap();
		if(session.getContextPath()!=null) addConfig(CONFIG_CONTEXT,session.getContextPath()+"/");
		/** 是否为空 **/
		boolean notBlank = view.getBoolean(PROPERTITY_REQUIRED, false);
		map.put(PROPERTITY_REQUIRED, Boolean.valueOf(notBlank));
		if(notBlank) {
			addConfig(PROPERTITY_REQUIRED, Boolean.valueOf(notBlank));
		}
		addConfig(PROPERTITY_REQUIRED, Boolean.valueOf(notBlank));
		
		/** 是否隐藏 **/
		boolean hidden = view.getBoolean(ComponentConfig.PROPERTITY_HIDDEN, false);
		if(hidden != false)
		addConfig(ComponentConfig.PROPERTITY_HIDDEN, Boolean.valueOf(hidden));
		
		/** Renderer **/
		String renderer = view.getString(PROPERTITY_RENDERER);
		if(renderer != null)
		addConfig(PROPERTITY_RENDERER, renderer);
		
		/** Renderer **/
		String maxlength = view.getString(PROPERTITY_MAX_LENGHT);
		if(maxlength != null)
		addConfig(PROPERTITY_MAX_LENGHT, maxlength);
		
		/** 是否只读 **/
		boolean readOnly = view.getBoolean(PROPERTITY_READONLY, false);
		if(readOnly) {
			map.put(PROPERTITY_READONLY, "readonly");
		}
		addConfig(PROPERTITY_READONLY, Boolean.valueOf(readOnly));
	}
	
	
}
