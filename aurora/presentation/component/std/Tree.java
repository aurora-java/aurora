package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;

public class Tree extends Component {
	
//	public static final String PROPERTITY_DATASET = "dataset";
	public static final String PROPERTITY_RENDERER = "renderer";
	public static final String PROPERTITY_FIELD_ID = "idfield";
	public static final String PROPERTITY_SHOWCHECKBOX = "showcheckbox";
	public static final String PROPERTITY_FIELD_PARENT = "parentfield";
	public static final String PROPERTITY_FIELD_DISPLAY = "displayfield";
	private static final String DEFAULT_CLASS = "item-tree";
	
	public static final String CONFIG_CONTEXT = "context";
	
	protected String getDefaultClass(BuildSession session, ViewContext context){
		return DEFAULT_CLASS;
	}
	
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		super.onPreparePageContent(session, context);
		addStyleSheet(session, context, "tree/Tree.css");
		addJavaScript(session, context, "tree/Tree.js");
	}
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
		super.onCreateViewContent(session, context);
		Map map = context.getMap();
		CompositeMap view = context.getView();

		String size = "";
		/** Width属性**/
		String width = view.getString(ComponentConfig.PROPERTITY_WIDTH, "");
		if(!"".endsWith(width)) {
			size += "width:"+width+"px;";
			addConfig(ComponentConfig.PROPERTITY_WIDTH, width);
		}
		/** Height属性**/
		String height = view.getString(ComponentConfig.PROPERTITY_HEIGHT, "");
		if(!"".endsWith(height)) {
			size += "height:"+height+"px;";
			addConfig(ComponentConfig.PROPERTITY_HEIGHT, height);
		}
		
		map.put("size", size);
		map.put(ComponentConfig.PROPERTITY_BINDTARGET, view.getString(ComponentConfig.PROPERTITY_BINDTARGET));
		
		if(session.getContextPath()!=null) addConfig(CONFIG_CONTEXT,session.getContextPath()+"/");
		addConfig(PROPERTITY_FIELD_DISPLAY, view.getString(PROPERTITY_FIELD_DISPLAY,"name"));
		addConfig(PROPERTITY_RENDERER, view.getString(PROPERTITY_RENDERER,""));
		addConfig(PROPERTITY_FIELD_ID, view.getString(PROPERTITY_FIELD_ID,"id"));
		addConfig(PROPERTITY_FIELD_PARENT, view.getString(PROPERTITY_FIELD_PARENT,"pid"));
		addConfig(PROPERTITY_SHOWCHECKBOX, new Boolean(view.getBoolean(PROPERTITY_SHOWCHECKBOX, false)));
		map.put(CONFIG, getConfigString());
	}
	
	
	
}
