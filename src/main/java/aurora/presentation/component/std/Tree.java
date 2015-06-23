package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;

import aurora.application.AuroraApplication;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.TreeConfig;

@SuppressWarnings("unchecked")
public class Tree extends Component {
	
	public Tree(IObjectRegistry registry) {
		super(registry);
	}

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
		int h = 0;
		if(!"".endsWith(height)) {
			size += "height:"+height+"px;";
			addConfig(ComponentConfig.PROPERTITY_HEIGHT, height);
			h = Integer.parseInt(height);
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
		String searchField = tc.getSearchField(model);
		if(null!=searchField && !"".equals(searchField)){
			addConfig(TreeConfig.PROPERTITY_FIELD_SEARCH,searchField);
			createSearchField(session,context);
			h-=22;
		}
		if(h>0){
			map.put("bodyheight", new Integer(h));
		}
		map.put(CONFIG, getConfigString());
	}
	
	private void createSearchField(BuildSession session, ViewContext context) throws IOException{
		CompositeMap view = context.getView();
		CompositeMap model = context.getModel();
		Map map = context.getMap();
		Integer width = (Integer)map.get(ComponentConfig.PROPERTITY_WIDTH);
		CompositeMap textField = new CompositeMap("textField");
		textField.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
		textField.put(ComponentConfig.PROPERTITY_ID, map.get(ComponentConfig.PROPERTITY_ID)+"_search_field");
		textField.put(ComponentConfig.PROPERTITY_WIDTH, new Integer(width.intValue()-2));
		try {
			map.put("searchField", session.buildViewAsString(model, textField));
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
	
}
