package aurora.presentation.component.std;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;

public class SwitchButton extends Field {
	
	public SwitchButton(IObjectRegistry registry) {
		super(registry);
	}

	public static final String PROPERTITY_ITEMS = "items";
	public static final String PROPERTITY_ACTIVE_VALUE = "activeValue";
	public static final String PROPERTITY_ALIGN = "align";
	
	private static final String DEFAULT_TEMPLATES = "switchButton.ftl";
	private static final String DEFAULT_CLASSNAME = "s-switch-button";
	
	public String getTemplate(){
		return DEFAULT_TEMPLATES;
	}
	
	protected String getDefaultClass(BuildSession session, ViewContext context){
		return DEFAULT_CLASSNAME;
	}
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		Map map = context.getMap();
		
		CompositeMap items = view.getChild(PROPERTITY_ITEMS);
		String av = view.getString(PROPERTITY_ACTIVE_VALUE.toLowerCase());		
		if (items != null && items.getChilds() != null) {
			StringBuilder sb = new StringBuilder();
			List<CompositeMap> children = items.getChilds();
			int i = 1,len = children.size();
			for (CompositeMap item : children) {
				String value = item.getString("value","");
				String style = item.getString("style","");
				String text = item.getString("text","");
				sb.append("<li code=\"").append(value).append("\" ");
				sb.append("class=\"");
				if((av==null && i == 1) || value.equals(av)) {
					addConfig(PROPERTITY_ACTIVE_VALUE,(av==null && i == 1) ? value : av);
					sb.append("cur");
				}
				if(i==len) {
					sb.append(" last");
				}
				sb.append("\" ");
				if(!"".equals(style)){
					sb.append("style=\"").append(style).append("\"");
				}
				sb.append(">").append(text).append("</li>");
				i++;
			}
			map.put(PROPERTITY_ITEMS, sb.toString());
			map.put(CONFIG, getConfigString());
		}
	}
	
}
