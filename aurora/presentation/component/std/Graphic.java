package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.EventConfig;
import aurora.presentation.component.std.config.GraphicConfig;

/**
 * Panel
 * 
 * @version $Id$
 * @author <a href="mailto:hugh.hz.wu@gmail.com">Hugh</a>
 */
public class Graphic extends Component {
	
	public static final String VERSION = "$Revision$";
	
	protected int getDefaultWidth() {
		return 600;
	}

	protected int getDefaultHeight() {
		return 300;
	}
	
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		super.onPreparePageContent(session, context);
		addJavaScript(session, context, "graphic/Graphics-min.js");
	}
	
	public void onCreateViewContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onCreateViewContent(session, context);
		Map map = context.getMap();
		CompositeMap view = context.getView();
		GraphicConfig gc = GraphicConfig.getInstance(view);
		if(null!= gc.getDropTo()){
			addConfig(GraphicConfig.PROPERTITY_DROP_TO, gc.getDropTo());
		}
		if(gc.isMoveable()){
			addConfig(GraphicConfig.PROPERTITY_MOVEABLE, new Boolean(gc.isMoveable()));
		}
		if(gc.isEditable()){
			addConfig(GraphicConfig.PROPERTITY_EDITABLE, new Boolean(gc.isEditable()));
		}
		if(null!= gc.getRenderer()){
			addConfig(GraphicConfig.PROPERTITY_RENDERER, gc.getRenderer());
		}
		List childs=view.getChilds();
		StringBuffer create=new StringBuffer("");
		if(null != childs){
			Iterator it = childs.iterator();
			while (it.hasNext()) {
				CompositeMap child = (CompositeMap) it.next();
				if(ComponentConfig.PROPERTITY_EVENTS.equalsIgnoreCase(child.getName()))
					continue;
				CompositeMap events = child.getChild(ComponentConfig.PROPERTITY_EVENTS);
				CompositeMap filters = child.getChild(GraphicConfig.PROPERTITY_FILTERS);
				if(filters != null){
					List list = filters.getChilds();
					if (list != null) {
						Iterator it2 = list.iterator();
						while (it2.hasNext()) {
							CompositeMap filter = (CompositeMap) it2.next();
							child.put(filter.getName(), toJson(filter));
						}
					}
				}
				if (events != null) {
					List list = events.getChilds();
					if (list != null) {
						Iterator it2 = list.iterator();
						JSONObject listeners = new JSONObject();
						while (it2.hasNext()) {
							CompositeMap event = (CompositeMap) it2.next();
							EventConfig eventConfig = EventConfig.getInstance(event);
							String eventName = eventConfig.getEventName();// event.getString(ComponentConfig.PROPERTITY_EVENT_NAME,// "");
							String handler = eventConfig.getHandler();// event.getString(ComponentConfig.PROPERTITY_EVENT_HANDLER,// "");
							if (!"".equals(eventName) && !"".equals(handler)){
								try {
									listeners.put(eventName, new JSONFunction(handler));
								} catch (JSONException e) {
								}
							}
						}
						child.put("listeners", listeners);
					}
				}
				create.append(".createGElement('");
				create.append(child.getName());
				create.append("',");
				JSONObject object=new JSONObject(child);
				create.append(object.toString());
				create.append(")");
			}
			map.put("creates", create.toString());
		}
		
		map.put(CONFIG, getConfigString());
	}
	private JSONObject toJson(CompositeMap filter){
		List list = filter.getChilds();
		if(list != null){
			Iterator it = list.iterator();
			while(it.hasNext()){
				CompositeMap child = (CompositeMap) it.next();
				JSONArray o = (JSONArray) filter.get(child.getName());
				if(null == o){
					o = new JSONArray();
					filter.put(child.getName(),o);
				}
				o.put(toJson(child));
			}
		}
		return new JSONObject(filter);
	}
}
