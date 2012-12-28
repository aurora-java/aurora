package aurora.presentation.component.std;

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
import aurora.presentation.component.std.config.ComponentConfig;

public class Tab extends Component {
	
	public static final String VERSION = "$Revision$";
	private static final String DEFAULT_CLASS = "item-tab";
	private static final String VALID_SCRIPT = "validscript";
	private static final String TABS = "tabs";
	
	protected static final String PROPERTITY_TAB = "tab";
	protected static final String PROPERTITY_TAB_CLASS = "tabclassname";
	protected static final String PROPERTITY_TAB_STYLE = "tabstyle";
	protected static final String PROPERTITY_BODY_CLASS = "bodyclassname";
	protected static final String PROPERTITY_BODY_STYLE = "bodystyle";
	protected static final String PROPERTITY_REF = "ref";
	protected static final String PROPERTITY_SELECTED = "selected";
	protected static final String PROPERTITY_CLOSEABLE = "closeable";
	protected static final String PROPERTITY_DISABLED = "disabled";
	
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		super.onPreparePageContent(session, context);
		addStyleSheet(session, context, "tab/Tab-min.css");
		addJavaScript(session, context, "tab/Tab-min.js");
	}
	
	protected String getDefaultClass(BuildSession session, ViewContext context){
		return DEFAULT_CLASS;
	}
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
		super.onCreateViewContent(session, context);
		Map map = context.getMap();
		StringBuffer sb = new StringBuffer();
		
		Integer bodyWidth = new Integer(((Integer)map.get(ComponentConfig.PROPERTITY_WIDTH)).intValue() - 2);
		Integer bodyHeight = new Integer(((Integer)map.get(ComponentConfig.PROPERTITY_HEIGHT)).intValue() - 25);
		map.put("bodywidth", bodyWidth);
		map.put("headwidth", new Integer(bodyWidth.intValue()-36));
		map.put("bodyheight", bodyHeight);
		map.put("strips", createTabStrips(session,context,sb));
		map.put("bodys", createTabBodys(session,context));
		if(((Integer)map.get("stripswidth")).intValue() <= bodyWidth.intValue()-36){
			map.put("display", "none");
		}
		map.put(VALID_SCRIPT, sb.toString());
		//map.put(PROPERTITY_SELECTED, new Integer(0));
		//addConfig(PROPERTITY_SELECTED, new Integer(0));
		map.put(CONFIG, getConfigString());
	}
	
	
	private String createTabBodys(BuildSession session, ViewContext context) throws IOException{
		CompositeMap view = context.getView();
		CompositeMap model = context.getModel();
		Map map = context.getMap();
		
		StringBuffer sb = new StringBuffer();
		CompositeMap tabs = view.getChild(TABS);
		if(tabs!=null) {
			List childs = tabs.getChilds();
			if(childs!=null){
				Iterator it = childs.iterator();
				
				while(it.hasNext()){
					CompositeMap tab = (CompositeMap)it.next();
					if(isHidden(tab, model)) continue;
					
					
					Integer bodywidth = (Integer)map.get("bodywidth");
					Integer bodyheight = (Integer)map.get("bodyheight");
					String ref = tab.getString(PROPERTITY_REF, "");
					String bodyClass = tab.getString(PROPERTITY_BODY_CLASS, "");
					String bodyStyle = tab.getString(PROPERTITY_BODY_STYLE, "");
					sb.append("<div class='tab "+bodyClass+"' hideFocus tabIndex='-1' style='width:"+bodywidth+"px;height:"+bodyheight+"px;"+bodyStyle+"'>");
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
		}
		return sb.toString();
	}
	
	private String createTabStrips(BuildSession session, ViewContext context,StringBuffer st){
		CompositeMap view = context.getView();
		Map map = context.getMap();
		CompositeMap model = context.getModel();
		
		JSONArray jsons = new JSONArray(); 
		StringBuffer sb = new StringBuffer();
		CompositeMap tabs = view.getChild(TABS);
		int stripswidth=0;
		if(tabs!=null){
		List childs = tabs.getChilds();
			if(childs!=null){
				Iterator it = childs.iterator();
				int i = 0;
				while(it.hasNext()){
					CompositeMap tab = (CompositeMap)it.next();
					if(isHidden(tab, model)) continue;
					String prompt = tab.getString(ComponentConfig.PROPERTITY_PROMPT, "");
					prompt = session.getLocalizedPrompt(prompt);
					int width = tab.getInt(ComponentConfig.PROPERTITY_WIDTH, 60);
					stripswidth+=width+6;
					String id = tab.getString(ComponentConfig.PROPERTITY_ID, "");
					String target = tab.getString(ComponentConfig.PROPERTITY_BINDTARGET, "");
					if(!"".equals(target)){
						if("".equals(id))id = IDGenerator.getInstance().generate();
						String[] ts = target.split(",");
						for(int b=0;b<ts.length;b++){
							String tid = ts[b];
							st.append("$('"+tid+"').on('valid',function(ds, record, name, valid){if(!valid && !Ext.get('"+id+"').hasActiveFx()) Ext.get('"+id+"').frame('ff0000', 3, { duration: 1 })});\n");
						}
						
					}
					String selected = tab.getString(PROPERTITY_SELECTED, "");
					if("true".equals(selected)){
						map.put("selected", new Integer(i));
						addConfig(PROPERTITY_SELECTED, new Integer(i));
					}
					String tabClass = tab.getString(PROPERTITY_TAB_CLASS, "");
					String tabStyle = tab.getString(PROPERTITY_TAB_STYLE, "");
					boolean closeable = tab.getBoolean(PROPERTITY_CLOSEABLE, false);
					boolean disabled = tab.getBoolean(PROPERTITY_DISABLED, false);
					if(!"".equals(tabStyle)){
						tabStyle = "style='"+tabStyle+"'";
					}
					sb.append("<div class='strip unactive");
					if(disabled){
						sb.append(" scroll-disabled");
					}
					sb.append("' "+tabStyle+" unselectable='on' "+((!"".equals(id)) ? "id='"+id+"'" : "") +" onselectstart='return false;'><div style='height:26px;width:"+(width+6)+"px'>");
					sb.append("<div class='strip-left "+tabClass+"'></div>");
					sb.append("<div class='strip-center "+tabClass+"' style='width:"+width+"px;'>");
					if(closeable){
						sb.append("<div class='tab-close'></div>");
					}
					sb.append(prompt+"</div>");
					sb.append("<div class='strip-right "+tabClass+"'></div>");
					sb.append("</div></div>");
					
					tab.putBoolean(PROPERTITY_SELECTED, tab.getBoolean(PROPERTITY_SELECTED, false));
					String ref = tab.getString(PROPERTITY_REF, "");
					ref = uncertain.composite.TextParser.parse(ref, model);
					tab.putString(PROPERTITY_REF, ref);
					JSONObject json = new JSONObject(tab);
					jsons.put(json);
					i++;
				}
			}
		}
		map.put("stripswidth", new Integer(stripswidth));
		addConfig("items", jsons);
		map.put("items", jsons.toString());
		return sb.toString();
	}
}
