package aurora.presentation.component.std;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.EventConfig;

/**
 * Panel
 * 
 * @version $Id: Panel.java v 1.0 2011-4-21 上午10:37:19 hugh Exp $
 * @author <a href="mailto:hugh.hz.wu@gmail.com">Hugh</a>
 */
public class Graphic extends Component {
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		super.onPreparePageContent(session, context);
		//addStyleSheet(session, context, "grid/Grid-min.css");
		addJavaScript(session, context, "graphic/Graphics.js");
	}
	public void onCreateViewContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onCreateViewContent(session, context);
		Map map = context.getMap();
		CompositeMap view = context.getView();
		List childs=view.getChilds();
		StringBuffer create=new StringBuffer("");
		if(null != childs){
			Iterator it = childs.iterator();
			while (it.hasNext()) {
				CompositeMap child = (CompositeMap) it.next();
				if(ComponentConfig.PROPERTITY_EVENTS.equalsIgnoreCase(child.getName()))
					continue;
				CompositeMap events = child.getChild(ComponentConfig.PROPERTITY_EVENTS);
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
}
