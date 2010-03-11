package aurora.presentation.component.std;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

public class Radio extends Component {
	
	private static final String RROPERTITY_ITEMS = "items";
	private static final String PROPERTITY_LABEL = "label";
	private static final String PROPERTITY_VALUE = "value";
	private static final String PROPERTITY_LAYOUT = "layout";
	
	public void onCreateViewContent(BuildSession session, ViewContext view_context) throws IOException{
		super.onCreateViewContent(session, view_context);
		Map map = view_context.getMap();
		CompositeMap view = view_context.getView();	
		
		String layout = view.getString(PROPERTITY_LAYOUT, "horizontal");
		CompositeMap items = view.getChild(RROPERTITY_ITEMS);
		if(items!=null){
			try {
				createOptions(map,items,layout);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		
		map.put(PROPERTITY_CONFIG, getConfigString());
	}
	
	
	private void createOptions(Map map, CompositeMap items,String layout) throws JSONException {
		StringBuffer sb = new StringBuffer();
		List children = items.getChilds();
		List options = new ArrayList();
		if(children!=null){
			Iterator it = children.iterator();
			while(it.hasNext()){
				CompositeMap item = (CompositeMap)it.next();
				String label = item.getString(PROPERTITY_LABEL, "");
				String value = item.getString(PROPERTITY_VALUE, "");
				
				JSONObject option = new JSONObject();
				option.put(PROPERTITY_LABEL, label);
				option.put(PROPERTITY_VALUE, value);
				options.add(option);
				
				if(!"".equals(label)){
					label = ":"+label;
				}
				
				sb.append("<div class='item-radio-option'");
				if("horizontal".equalsIgnoreCase(layout)) sb.append(" style='float:left'");
				sb.append(" itemvalue='"+value+"'>"); 
				sb.append("<div class='item-radio-img'></div>");
				sb.append("<label class='item-radio-lb'>"+label+"</label>");
				sb.append("</div>");
			}
		}
		
		addConfig("options", new JSONArray(options));
		map.put("options", sb.toString());
	}

}
