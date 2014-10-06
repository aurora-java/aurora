package aurora.presentation.component.touch;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.TabConfig;

public class Tab extends Component {
	

	private static final String DEFAULT_CLASS = "touch-tabpanel";
	private static final String PROPERTITY_PROMPT = "prompt";
	private static final String PROPERTITY_SELECTED = "selected";
	private static final String PROPERTITY_DISABLED = "disabled";
	private static final String PROPERTITY_BOTTOM_BAR = "bottombar";
	private static final String TABS = "tabs";
	
	protected String getDefaultClass(BuildSession session, ViewContext context){
		return DEFAULT_CLASS;
	}
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		Map map = context.getMap();
		StringBuffer sb = new StringBuffer();
		CompositeMap tabs = view.getChild(TABS);
		boolean hasSelected = false;
		if(null != tabs) {
			Iterator it = tabs.getChildIterator();
			if(null != it){
				while(it.hasNext()){
					CompositeMap tab = (CompositeMap)it.next();
					if(tab.getBoolean(PROPERTITY_SELECTED, false)){
						hasSelected = true;
					}
				}
			}
		}
		if(view.getBoolean(PROPERTITY_BOTTOM_BAR, false)){
			createTabStrips(session,context,sb,false,hasSelected);
			createTabBodys(session,context,sb,hasSelected);
		}else{
			createTabBodys(session,context,sb,hasSelected);
			createTabStrips(session,context,sb,true,hasSelected);
		}
		map.put(TABS, sb.toString());
		map.put(CONFIG, getConfigString());
	}
	
	
	private void createTabBodys(BuildSession session, ViewContext context,StringBuffer sb,boolean hasSelected) throws IOException{
		CompositeMap view = context.getView();
		CompositeMap model = context.getModel();
		
		CompositeMap tabs = view.getChild(TABS);
		sb.append("<div class='touch-tabpanel-body'>");
		sb.append("<div class='touch-tabpanel-card-container'>");
		if(null != tabs) {
			Iterator it = tabs.getChildIterator();
			if(null != it){
				int i = 0;
				while(it.hasNext()){
					CompositeMap tab = (CompositeMap)it.next();
					sb.append("<div class='touch-tabpanel-card-item");
					if(hasSelected?!tab.getBoolean(PROPERTITY_SELECTED, false):i!=0){
						sb.append(" touch-tabpanel-card-item-hide");
					}
					sb.append("'>");
					Iterator tit = tab.getChildIterator();
					if(null != tit){
						while(tit.hasNext()){
							CompositeMap tabchild = (CompositeMap)tit.next();
							try {
								sb.append(session.buildViewAsString(model, tabchild));
							} catch (Exception e) {
								throw new IOException(e);
							}			
						}
					}
					sb.append("</div>");
					i++;
				}
			}
		}
		sb.append("</div>");
		sb.append("</div>");
	}
	
	private void createTabStrips(BuildSession session, ViewContext context,StringBuffer sb,boolean isBottom,boolean hasSelected){
		CompositeMap view = context.getView();
		CompositeMap tabs = view.getChild(TABS);
		sb.append("<div class='touch-tabpanel-tabbar");
		if(isBottom){
			sb.append(" touch-tabpanel-tabbar-bottom");
		}
		sb.append("'>");
		sb.append("<div class='touch-box-horizontal touch-tabpanel-strip-container'>");
		if(null != tabs){
			Iterator it = tabs.getChildIterator();
			if(null != it){
				int i=0;
				while(it.hasNext()){
					CompositeMap tab = (CompositeMap)it.next();
					sb.append("<div class='touch-tabpanel-strip");
					if(tab.getBoolean(PROPERTITY_DISABLED, false)){
						sb.append(" touch-tabpanel-strip-disabled");
					}
					if(hasSelected?tab.getBoolean(PROPERTITY_SELECTED, false):i==0){
						sb.append(" touch-tabpanel-strip-active");
					}
					sb.append("' unselectable='on' onselectstart='return false;'>");
					sb.append(session.getLocalizedPrompt(tab.getString(PROPERTITY_PROMPT)));
					sb.append("</div>");
					i++;
				}
			}
		}
		sb.append("</div>");
		sb.append("</div>");
	}
}
