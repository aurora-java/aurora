package aurora.presentation.component.std;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

public class Tab extends Component {
	
	protected static final String PROPERTITY_TAB = "tab";
	protected static final String PROPERTITY_REF = "ref";
	protected static final String PROPERTITY_SELECTED = "selected";
	
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		super.onPreparePageContent(session, context);
		addStyleSheet(session, context, "tab/Tab.css");
		addJavaScript(session, context, "tab/Tab.js");
	}
	
	@SuppressWarnings("unchecked")
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		Map map = context.getMap();
		
		Integer bodyWidth = (Integer)map.get(PROPERTITY_WIDTH) - 2;
		Integer bodyHeight = (Integer)map.get(PROPERTITY_HEIGHT) - 25;
		map.put("bodywidth", bodyWidth);
		map.put("bodyheight", bodyHeight);
		map.put("selected", 0);
		map.put("strips", createTabStrips(session,context));
		map.put("bodys", createTabBodys(session,context));
	}
	
	@SuppressWarnings("unchecked")
	private String createTabBodys(BuildSession session, ViewContext context) throws IOException{
		CompositeMap view = context.getView();
		CompositeMap model = context.getModel();
		Map map = context.getMap();
		
		StringBuffer sb = new StringBuffer();
		List childs = view.getChilds();
		if(childs!=null){
			Iterator it = childs.iterator();
			
			while(it.hasNext()){
				CompositeMap tab = (CompositeMap)it.next();
				Integer bodywidth = (Integer)map.get("bodywidth");
				sb.append("<div class='tab' style='width:"+bodywidth+"px'>");
				String ref = tab.getString(PROPERTITY_REF, "");
				
				if("".equals(ref)){
					List tabchilds = tab.getChilds();
					if(tabchilds!=null){
						Iterator tit = tabchilds.iterator();
						while(tit.hasNext()){
							CompositeMap tabchild = (CompositeMap)tit.next();
							try {
								sb.append(session.buildViewAsString(model, tabchild));
							} catch (Exception e) {
								throw new IOException(e.getMessage());
							}			
						}
					}	
				}
				sb.append("</div>");
				
			}
		}
		return sb.toString();
	}
	
	private String createTabStrips(BuildSession session, ViewContext context){
		CompositeMap view = context.getView();
		CompositeMap model = context.getModel();
		Map map = context.getMap();
		
		List jsons = new ArrayList(); 
		StringBuffer sb = new StringBuffer();
		List childs = view.getChilds();
		if(childs!=null){
			Iterator it = childs.iterator();
			int i = 0;
			while(it.hasNext()){
				CompositeMap tab = (CompositeMap)it.next();
				
				String prompt = tab.getString(PROPERTITY_LABEL, "");
				int width = tab.getInt(PROPERTITY_WIDTH, 60);
				String selected = tab.getString(PROPERTITY_SELECTED, "");
				if("true".equals(selected)){
					map.put("selected", i);
				}
				sb.append("<div class='strip' unselectable='on' onselectstart='return false;'>");
				sb.append("<div class='strip-left'></div>");
				sb.append("<div class='strip-center' style='width:"+width+"px;'>"+prompt+"</div>");
				sb.append("<div class='strip-right'></div>");
				sb.append("</div>");
				
				tab.putBoolean(PROPERTITY_SELECTED, tab.getBoolean(PROPERTITY_SELECTED, false));
				JSONObject json = new JSONObject(tab);
				jsons.add(json);
				i++;
			}
		}
		map.put("items", jsons.toString());
		return sb.toString();
	}
}
