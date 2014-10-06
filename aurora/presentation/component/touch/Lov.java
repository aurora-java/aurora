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
	private static final String PROPERTITY_AUTO_QUERY = "autoquery";
	private static final String PROPERTITY_MAPPING = "mapping";
	private static final String PROPERTITY_LOV_FIELDS = "lovFields";
	private static final String PROPERTITY_LOV_FIELD_FOR_DISPLAY = "fordisplay";
	private static final String PROPERTITY_LOV_FIELD_FOR_QUERY = "forquery";
	
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
		addConfig(PROPERTITY_AUTO_QUERY, view.getBoolean(PROPERTITY_AUTO_QUERY,true));
		map.put(PROPERTITY_DISABLED, "disabled = 'disabled'");
		processMapping(view);
		processLovFields(view);
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
	private void processLovFields(CompositeMap view){
		CompositeMap fields = view.getChild(PROPERTITY_LOV_FIELDS);
		if(null!=fields){
			List maplist = new ArrayList();
			Iterator it = fields.getChildIterator();
			while(it.hasNext()){
				CompositeMap field = (CompositeMap)it.next();
				boolean forDisplay = field.getBoolean(PROPERTITY_LOV_FIELD_FOR_DISPLAY,true);
				if(forDisplay){
					field.putBoolean(PROPERTITY_LOV_FIELD_FOR_DISPLAY, forDisplay);
				}
				boolean forQuery = field.getBoolean(PROPERTITY_LOV_FIELD_FOR_QUERY,true);
				if(forQuery){
					field.putBoolean(PROPERTITY_LOV_FIELD_FOR_QUERY, forQuery);
				}
				maplist.add(new JSONObject(field));
			}
			if(maplist.size() > 0){
				addConfig(PROPERTITY_LOV_FIELDS, new JSONArray(maplist));
			}
		}
	}
}
