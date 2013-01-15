package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.FieldConfig;

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
@SuppressWarnings("unchecked")
public class Field extends Component {
	public static final String VERSION = "$Revision$";
	
	protected static final String CLASSNAME_WRAP = "item-wrap";
	protected static final String CLASSNAME_NOTBLANK = "item-notBlank";
	protected static final String CLASSNAME_READONLY = "item-readOnly";
	
	private String CONFIG_CONTEXT = "context";

	protected String getDefaultClass(BuildSession session, ViewContext context){
		CompositeMap view = context.getView();
		FieldConfig fc = new FieldConfig();
		fc.initialize(view);
		
		boolean notBlank = fc.getRequired();
		String wrapClass = CLASSNAME_WRAP;
		if(notBlank) {
			wrapClass += " " + CLASSNAME_NOTBLANK;
		}
		boolean readOnly = fc.getReadonly();
		if(readOnly) {
			wrapClass += " "+CLASSNAME_READONLY;
		}
		return wrapClass;
	}
	
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		Map map = context.getMap();
		FieldConfig fc = new FieldConfig();
		fc.initialize(view);
		
		if(session.getContextPath()!=null) addConfig(CONFIG_CONTEXT,session.getContextPath()+"/");
		/** 是否为空 **/
		boolean notBlank = fc.getRequired();
		map.put(FieldConfig.PROPERTITY_REQUIRED, notBlank);
		addConfig(FieldConfig.PROPERTITY_REQUIRED, Boolean.valueOf(notBlank));
		
		/** 是否隐藏 **/
		boolean hidden = view.getBoolean(FieldConfig.PROPERTITY_HIDDEN, false);
		if(hidden != false)
		addConfig(FieldConfig.PROPERTITY_HIDDEN, Boolean.valueOf(hidden));
		
		/** Renderer **/
		String renderer = fc.getRenderer();
		if(renderer != null)
		addConfig(FieldConfig.PROPERTITY_RENDERER, renderer);
		
		/** Renderer **/
		Integer maxlength = fc.getMaxLength();
		if(maxlength != null)
		addConfig(FieldConfig.PROPERTITY_MAX_LENGHT, maxlength);
		
		/** 是否只读 **/
		boolean readOnly = fc.getReadonly();
		if(readOnly) {
			map.put(FieldConfig.PROPERTITY_READONLY, "readonly");
		}
		addConfig(FieldConfig.PROPERTITY_READONLY, readOnly);
	}
}
