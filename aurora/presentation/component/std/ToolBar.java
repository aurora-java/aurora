package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;

public class ToolBar extends Component {
	
	public ToolBar(IObjectRegistry registry) {
		super(registry);
	}

	public static final String VERSION = "$Revision$";
	private static final String DEFAULT_CLASS = "item-toolbar";
	public static final String PROPERTITY_ITEMS = "items";
	
	protected String getDefaultClass(BuildSession session, ViewContext context){
		return DEFAULT_CLASS;
	}
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		Map map = context.getMap();
		CompositeMap model = context.getModel();
		
		StringBuffer sb = new StringBuffer();
		if(view != null && view.getChilds() != null) {
			Iterator it = view.getChildIterator();
			while(it.hasNext()){
				CompositeMap editor = (CompositeMap)it.next();
				String style = editor.getString(ComponentConfig.PROPERTITY_STYLE,"");
				if(editor.getName().equalsIgnoreCase("button")){
					editor.put(ComponentConfig.PROPERTITY_WIDTH, new Integer(1));
					style = "float:left;margin-right:1px;margin-top:3px;" + style;
				} else if(editor.getName().equalsIgnoreCase("separator")){
					style = "float:left;margin-right:1px;" + style;	
				}else{
					style = "float:left;margin-right:1px;margin-top:2px;" + style;					
				}
				editor.put(ComponentConfig.PROPERTITY_STYLE, style);
				try {
					sb.append(session.buildViewAsString(model, editor));
				} catch (Exception e) {
					throw new IOException(e.getMessage());
				}
			}
		}
		map.put(PROPERTITY_ITEMS, sb.toString());
	}

}
