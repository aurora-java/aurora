package aurora.presentation.component.touch;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.event.Configuration;
import uncertain.proc.IFeature;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewComponentPackage;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.IDGenerator;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.EventConfig;
import aurora.presentation.markup.HtmlPageContext;

@SuppressWarnings("unchecked")
public class Component implements IFeature {
	CompositeMap  view_config;
	protected static final String CLASS = "cls";
	
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		addJavaScript(session, context, "base/zepto.js");
		addStyleSheet(session, context, "base/touch-all-min.css");
	}
	
	protected String getDefaultClass(BuildSession session, ViewContext context) {
		return "";
	}
	
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException {
		CompositeMap view = context.getView();
		Map map = context.getMap();
		CompositeMap model = context.getModel();
		
		String id = view.getString(ComponentConfig.PROPERTITY_ID, "");
		if (!"".equals(id)) {
			String ids = "id=\""+id+"\"";
			map.put(ComponentConfig.PROPERTITY_ID, ids);
		}
		
		
		String clazz = getDefaultClass(session, context);
		String className = view.getString(ComponentConfig.PROPERTITY_CLASSNAME,"");
		if (!"".equals(className)) {
			clazz += " " + className;
		}
		map.put(CLASS, clazz);
		
		String style = view.getString(ComponentConfig.PROPERTITY_STYLE, "");
		if (!"".equals(style)) {
			style="style=\"" + style+"\"";
		}
		map.put(ComponentConfig.PROPERTITY_STYLE, style);
		
		/** 组件注册事件 * */
		CompositeMap events = view.getChild(ComponentConfig.PROPERTITY_EVENTS);
		if (events != null) {
			if ("".equals(id)) {
				id = IDGenerator.getInstance().generate();
			}
			String ids = "id=\""+id+"\"";
			map.put(ComponentConfig.PROPERTITY_ID, ids);
			
			List list = events.getChilds();
			if (list != null) {
				StringBuffer sb = new StringBuffer("<script>");
				Iterator it = list.iterator();
				while (it.hasNext()) {
					CompositeMap event = (CompositeMap) it.next();
					EventConfig eventConfig = EventConfig.getInstance(event);
					String eventName = eventConfig.getEventName();
					String handler = eventConfig.getHandler();
					if (!"".equals(eventName) && !"".equals(handler))
						handler = uncertain.composite.TextParser.parse(handler, model);
					sb.append("$('#").append(id).append("')").append(".on('").append(eventName).append("',").append(handler).append(");");
				}
				sb.append("</script>");
				map.put(ComponentConfig.PROPERTITY_EVENTS, sb.toString());
			}
		}
	}
	
	
	/**
	 * 加入JavaScript
	 * 
	 * @param session
	 * @param context
	 * @param javascript
	 * @return String
	 */
	protected void addJavaScript(BuildSession session, ViewContext context, String javascript) {
		if (!session.includeResource(javascript)) {
			HtmlPageContext page = HtmlPageContext.getInstance(context);
			ViewComponentPackage pkg = session.getPresentationManager().getPackage(view_config);
			String js = session.getResourceUrl(pkg,javascript);
			page.addScript(js);
		}
	}

	/**
	 * 加入StyleSheet
	 * 
	 * @param session
	 * @param context
	 * @param style
	 * @return String
	 */
	protected void addStyleSheet(BuildSession session, ViewContext context, String style) {
		if (!session.includeResource(style)) {
			HtmlPageContext page = HtmlPageContext.getInstance(context);
			ViewComponentPackage pkg = session.getPresentationManager().getPackage(view_config);
			String styleSheet = session.getResourceUrl(pkg, style);
			page.addStyleSheet(styleSheet);
		}
	}

    @Override
    public int attachTo(CompositeMap config_data, Configuration config) {
        this.view_config = config_data;
        return IFeature.NORMAL;
    }
}
