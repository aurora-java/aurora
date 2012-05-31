package aurora.presentation.component.touch;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;


public class Ajax extends Component{
	
	private static final String PROPERTITY_ID = "id";
	private static final String PROPERTITY_TYPE = "type";
	private static final String PROPERTITY_URL = "url";
	private static final String PROPERTITY_TIMEOUT = "timeout";
	private static final String PROPERTITY_ASYNC = "async";
	private static final String PROPERTITY_DATATYPE = "dataType";
	private static final String PARAMETERS = "parameters";
	private static final String EVENTS = "events";
	private JSONObject config = new JSONObject();
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException {
		CompositeMap view = context.getView();
		Map map = context.getMap();
		CompositeMap model = context.getModel();
		try {
			if(null != view.getString(PROPERTITY_ID))
				config.put(PROPERTITY_ID, view.getString(PROPERTITY_ID));
			if(null != view.getString(PROPERTITY_TYPE))
				config.put(PROPERTITY_TYPE, view.getString(PROPERTITY_TYPE));
			if(null != view.getString(PROPERTITY_URL))
				config.put(PROPERTITY_URL, view.getString(PROPERTITY_URL));
			if(null != view.getInt(PROPERTITY_TIMEOUT))
				config.put(PROPERTITY_URL, view.getInt(PROPERTITY_TIMEOUT));
			if(null != view.getBoolean(PROPERTITY_ASYNC))
				config.put(PROPERTITY_URL, view.getBoolean(PROPERTITY_ASYNC));
			if(null != view.getString(PROPERTITY_DATATYPE.toLowerCase()))
				config.put(PROPERTITY_DATATYPE, view.getString(PROPERTITY_DATATYPE.toLowerCase()));
			processParameters(view);
			processEvents(view);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
		map.put("config", config.toString());
	}
	
	private void processParameters(CompositeMap parent) throws JSONException{
		CompositeMap parameters = parent.getChild(PARAMETERS);
		Iterator childs = parameters.getChildIterator();
		Map datas = new HashMap();
		while(childs.hasNext()){
			CompositeMap child = (CompositeMap) childs.next();
			String key = child.getString("name");
			String value = child.getString("value");
			datas.put(key, value);
		}
		if(!datas.isEmpty())
			config.put(PARAMETERS, new JSONObject(datas));
	}
	private void processEvents(CompositeMap parent) throws JSONException{
		CompositeMap parameters = parent.getChild(EVENTS);
		Iterator childs = parameters.getChildIterator();
		Map datas = new HashMap();
		while(childs.hasNext()){
			CompositeMap child = (CompositeMap) childs.next();
			String key = child.getString("name");
			String handler = child.getString("handler");
			config.put(key, new JSONFunction(handler));
		}
	}
}
