package aurora.presentation.component.std;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;

public class Tab extends Component {
	
	private static final String DEFAULT_CLASS = "item-tab";
	private static final String VALID_SCRIPT = "validscript";
	
	protected static final String PROPERTITY_TAB = "tab";
	protected static final String PROPERTITY_REF = "ref";
	protected static final String PROPERTITY_SELECTED = "selected";
	
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		super.onPreparePageContent(session, context);
		addStyleSheet(session, context, "tab/Tab.css");
		addJavaScript(session, context, "tab/Tab.js");
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
		map.put("bodyheight", bodyHeight);
		map.put("selected", new Integer(0));
		map.put("strips", createTabStrips(session,context,sb));
		map.put("bodys", createTabBodys(session,context));
		map.put(VALID_SCRIPT, sb.toString());
	}
	
	
	private String createTabBodys(BuildSession session, ViewContext context) throws IOException{
		CompositeMap view = context.getView();
		CompositeMap model = context.getModel();
		Map map = context.getMap();
		
		StringBuffer sb = new StringBuffer();
		List childs = view.getChilds();
		if(childs!=null){
			Iterator it = childs.iterator();
			
			while(it.hasNext()){
				CompositeMap tab = (CompositeMap)it.next();
				Integer bodywidth = (Integer)map.get("bodywidth");
				sb.append("<div class='tab' style='width:"+bodywidth+"px'>");
				String ref = tab.getString(PROPERTITY_REF, "");
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
		return sb.toString();
	}
	
	private String createTabStrips(BuildSession session, ViewContext context,StringBuffer st){
		CompositeMap view = context.getView();
		Map map = context.getMap();
		CompositeMap model = context.getModel();
		
		List jsons = new ArrayList(); 
		StringBuffer sb = new StringBuffer();
		List childs = view.getChilds();
		if(childs!=null){
			Iterator it = childs.iterator();
			int i = 0;
			while(it.hasNext()){
				CompositeMap tab = (CompositeMap)it.next();
				
				String prompt = tab.getString(ComponentConfig.PROPERTITY_PROMPT, "");
				prompt = session.getLocalizedPrompt(prompt);
				int width = tab.getInt(ComponentConfig.PROPERTITY_WIDTH, 60);
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
				}
				sb.append("<div class='strip' unselectable='on' "+((!"".equals(id)) ? "id='"+id+"'" : "") +" onselectstart='return false;'>");
				sb.append("<div class='strip-left'></div>");
				sb.append("<div class='strip-center' style='width:"+width+"px;'>"+prompt+"</div>");
				sb.append("<div class='strip-right'></div>");
				sb.append("</div>");
				
				tab.putBoolean(PROPERTITY_SELECTED, tab.getBoolean(PROPERTITY_SELECTED, false));
				String ref = tab.getString(PROPERTITY_REF, "");
				ref = uncertain.composite.TextParser.parse(ref, model);
				tab.putString(PROPERTITY_REF, ref);
				JSONObject json = new JSONObject(tab);
				jsons.add(json);
				i++;
			}
		}
		map.put("items", jsons.toString());
		return sb.toString();
	}
}
