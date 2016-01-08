package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.TabConfig;

public class Tab extends Component {
	
	public Tab(IObjectRegistry registry) {
		super(registry);
	}

	public static final String VERSION = "$Revision: 8602 $";
	private static final String DEFAULT_CLASS = "item-tab";
	private static final String VALID_SCRIPT = "validscript";
	private static final String TABS = "tabs";
	
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
		CompositeMap view = context.getView();
		Map map = context.getMap();
		StringBuffer sb = new StringBuffer();
		
		Integer bodyWidth = new Integer(((Integer)map.get(ComponentConfig.PROPERTITY_WIDTH)).intValue() - 2);
		Integer bodyHeight = new Integer(((Integer)map.get(ComponentConfig.PROPERTITY_HEIGHT)).intValue() - 25);
//		map.put("bodywidth", bodyWidth);
		map.put("headwidth", new Integer(bodyWidth.intValue()-36));
		map.put("bodyheight", bodyHeight);
		map.put("strips", createTabStrips(session,context,sb));
		map.put("bodys", createTabBodys(session,context));
		if(((Integer)map.get("stripswidth")).intValue() <= bodyWidth.intValue()-32){
			map.put("display", "none");
		}
		map.put(VALID_SCRIPT, sb.toString());
		//map.put(PROPERTITY_SELECTED, new Integer(0));
		//addConfig(PROPERTITY_SELECTED, new Integer(0));
		
		if("iframe".equalsIgnoreCase(view.getString("loadtype"))){
			addConfig("loadtype", "iframe");
		}
		
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
					TabConfig tc = TabConfig.getInstance(tab);
					
					Integer bodywidth = (Integer)map.get("bodywidth");
					Integer bodyheight = (Integer)map.get("bodyheight");
					String ref = tc.getRef();
					String bodyClass = tc.getBodyClass();
					String bodyStyle = tc.getBodyStyle();
					sb.append("<div class='tab "+bodyClass+"' hideFocus tabIndex='-1' style='"+bodyStyle+"'");
					if("".equals(ref)){
						String hostid =  IDGenerator.getInstance().generate();
						sb.append(" id='"+hostid+"'>");
						List tabchilds = tab.getChilds();
						if(tabchilds!=null){
							Iterator tit = tabchilds.iterator();
							while(tit.hasNext()){
								CompositeMap tabchild = (CompositeMap)tit.next();
								transferHostId(tabchild,hostid);
								try {
									sb.append(session.buildViewAsString(model, tabchild));
								} catch (Exception e) {
									throw new IOException(e);
								}			
							}
						}	
					}else{
						sb.append(">");
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
					TabConfig tc = TabConfig.getInstance(tab);
					String prompt = session.getLocalizedPrompt(tc.getPrompt());
					int width = tc.getWidth(60);
					stripswidth+=width+6;
					String id = tc.getId("");
					String target = tc.getBindTarget();
					if(!"".equals(target)){
						if("".equals(id))id = IDGenerator.getInstance().generate();
						String[] ts = target.split(",");
						for(int b=0;b<ts.length;b++){
							String tid = ts[b];
							st.append("$('"+tid+"').on('valid',function(ds, record, name, valid){if(!valid && !Ext.get('"+id+"').hasActiveFx()) Ext.get('"+id+"').frame('ff0000', 3, { duration: 1 })});\n");
						}
						
					}
					String tabClass = tc.getTabClass();
					String tabStyle = tc.getTabStyle();
					boolean closeable = tc.isCloseable();
					boolean disabled = tc.isDisabled();
					boolean selected = tc.isSelected();
					if(selected){
						map.put("selected", new Integer(i));
						addConfig(TabConfig.PROPERTITY_SELECTED, new Integer(i));
					}
					if(!"".equals(tabStyle)){
						tabStyle = "style='"+tabStyle+"'";
					}
					sb.append("<div class='strip unactive");
					if(disabled){
						sb.append(" scroll-disabled");
					}
					sb.append("' "+tabStyle+" unselectable='on' "+((!"".equals(id)) ? "id='"+id+"'" : "") +" onselectstart='return false;'>");
					sb.append("<div class='strip-left "+tabClass+"'></div>");
					sb.append("<div class='strip-center "+tabClass+"' style='width:"+width+"px;'>");
					if(closeable){
						sb.append("<div class='tab-close'></div>");
					}
					sb.append(prompt+"</div>");
					sb.append("<div class='strip-right "+tabClass+"'></div>");
					sb.append("</div>");
					
					tab.putBoolean(TabConfig.PROPERTITY_SELECTED, Boolean.valueOf(selected));
					String ref = uncertain.composite.TextParser.parse(tc.getRef(), model);
					tab.putString(TabConfig.PROPERTITY_REF, ref);
					JSONObject json = new JSONObject(tab);
					jsons.put(json);
					i++;
				}
			}
		}
		map.put("stripswidth", new Integer(stripswidth+4));
		addConfig("items", jsons);
		map.put("items", jsons.toString());
		return sb.toString();
	}
}
