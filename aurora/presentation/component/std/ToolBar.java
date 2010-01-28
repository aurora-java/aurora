package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

public class ToolBar extends Component {
	
	public static final String PROPERTITY_ITEMS = "items";
	
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		super.onPreparePageContent(session, context);
		addStyleSheet(session, context, "toolbar/ToolBar.css");
		addJavaScript(session, context, "toolbar/ToolBar.js");
	}

	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		Map map = context.getMap();
		CompositeMap model = context.getModel();
		
//		CompositeMap items = view.getChild(PROPERTITY_ITEMS);
		StringBuffer sb = new StringBuffer();
		if(view != null && view.getChilds() != null) {
			Iterator it = view.getChildIterator();
			while(it.hasNext()){
				CompositeMap editor = (CompositeMap)it.next();
				String style = editor.getString(PROPERTITY_STYLE,"");
				if(editor.getName().equalsIgnoreCase("button")){
					editor.put(PROPERTITY_WIDTH, new Integer(1));
					style = "float:left;margin-right:1px;margin-top:2px;" + style;
				} else if(editor.getName().equalsIgnoreCase("separator")){
					style = "float:left;margin-right:1px;" + style;	
				}else{
					style = "float:left;margin-right:1px;margin-top:4px;" + style;					
				}
				editor.put(PROPERTITY_STYLE, style);
				try {
					sb.append(session.buildViewAsString(model, editor));
				} catch (Exception e) {
					throw new IOException(e.getMessage());
				}
			}
		}
//		int width =  ((Integer)map.get(PROPERTITY_WIDTH)).intValue() - 10; 
//		map.put(PROPERTITY_WIDTH, new Integer(width));
		map.put(PROPERTITY_ITEMS, sb.toString());
	}

}
