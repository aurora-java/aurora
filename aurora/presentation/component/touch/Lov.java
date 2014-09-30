package aurora.presentation.component.touch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;


public class Lov extends InputField {
	
	private static final String PROPERTITY_BIND = "bind";
	private static final String PROPERTITY_PAGE_SIZE = "size";
	private static final String PROPERTITY_RENDERER = "renderer";
	private static final String PROPERTITY_MAPPING = "mapping";
	
	protected String getDefaultClass(BuildSession session, ViewContext context) {
		return "lov";
	}
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		Map map = context.getMap();
		addConfig(PROPERTITY_BIND, view.getString(PROPERTITY_BIND));
		addConfig(PROPERTITY_PAGE_SIZE, view.getInt(PROPERTITY_PAGE_SIZE,10));
		addConfig(PROPERTITY_RENDERER, view.getString(PROPERTITY_RENDERER,""));
		map.put(PROPERTITY_DISABLED, "disabled = 'disabled'");
		processMapping(view);
		map.put(CONFIG, getConfigString());
	}
	private void processMapping(CompositeMap view){
		CompositeMap mapping = view.getChild(PROPERTITY_MAPPING);
		if(null!=mapping){
			List maplist = new ArrayList();
			Iterator it = mapping.getChildIterator();
			while(it.hasNext()){
				maplist.add(new JSONObject((CompositeMap)it.next()));
			}
			if(maplist.size() > 0){
				addConfig(PROPERTITY_MAPPING, new JSONArray(maplist));
			}
		}
	}
}
