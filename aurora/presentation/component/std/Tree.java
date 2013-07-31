package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.TreeConfig;

@SuppressWarnings("unchecked")
public class Tree extends Component {
	public static final String VERSION = "$Revision$";
	
	private static final String DEFAULT_CLASS = "item-tree";
	
	public static final String CONFIG_CONTEXT = "context";
	
	protected String getDefaultClass(BuildSession session, ViewContext context){
		return DEFAULT_CLASS;
	}
	
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		super.onPreparePageContent(session, context);
		addStyleSheet(session, context, "tree/Tree-min.css");
		addJavaScript(session, context, "tree/Tree-min.js");
	}
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
		super.onCreateViewContent(session, context);
		Map map = context.getMap();
		CompositeMap view = context.getView();
		CompositeMap model = context.getModel();
		
		TreeConfig tc = TreeConfig.getInstance(view);
		
		String size = "";
		/** Width属性**/
		String width = tc.getWidthStr();
		if(!"".endsWith(width)) {
			size += "width:"+width+"px;";
			addConfig(ComponentConfig.PROPERTITY_WIDTH, width);
		}
		/** Height属性**/
		String height = tc.getHeightStr();
		if(!"".endsWith(height)) {
			size += "height:"+height+"px;";
			addConfig(ComponentConfig.PROPERTITY_HEIGHT, height);
		}
		
		map.put("size", size);
//		map.put(ComponentConfig.PROPERTITY_BINDTARGET, view.getString(ComponentConfig.PROPERTITY_BINDTARGET));
		
		if(session.getContextPath()!=null) addConfig(CONFIG_CONTEXT,session.getContextPath()+"/");
		addConfig(TreeConfig.PROPERTITY_FIELD_DISPLAY, tc.getDisplayField(model));
		if(tc.getRenderer()!=null)addConfig(TreeConfig.PROPERTITY_RENDERER, tc.getRenderer());
		addConfig(TreeConfig.PROPERTITY_FIELD_ID, tc.getIdField(model));
		addConfig(TreeConfig.PROPERTITY_FIELD_PARENT, tc.getParentField(model));
		addConfig(TreeConfig.PROPERTITY_SHOWCHECKBOX,  new Boolean(tc.isShowCheckBox()));
		addConfig(TreeConfig.PROPERTITY_FIELD_CHECKED, tc.getCheckField());
		addConfig(TreeConfig.PROPERTITY_FIELD_EXPAND, tc.getExpandField());
		addConfig(TreeConfig.PROPERTITY_FIELD_SEQUENCE, tc.getSequenceField(model));
		addConfig(TreeConfig.PROPERTITY_FIELD_ICON, tc.getIconField());
		map.put(CONFIG, getConfigString());
	}
	
	
	
}
