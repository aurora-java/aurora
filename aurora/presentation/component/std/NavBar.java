package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;

import aurora.application.AuroraApplication;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ButtonConfig;
import aurora.presentation.component.std.config.ComboBoxConfig;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.NumberFieldConfig;

@SuppressWarnings("unchecked")
public class NavBar extends ToolBar {
	
	public static final String VERSION = "$Revision$";
	
	public static final String PROPERTITY_DATASET = "dataset";
	public static final String PROPERTITY_NAVBAR_TYPE = "navbartype";
	public static final String PROPERTITY_PAGE_SIZE_EDITABLE = "enablepagesize";
	public static final String PROPERTITY_MAX_PAGE_COUNT = "maxpagecount";
	
	protected int getDefaultWidth() {
		return -1;
	}
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
		try {
			CompositeMap view = context.getView();
			CompositeMap model = context.getModel();
			Map map = context.getMap();		
			
			String dataset = view.getString(PROPERTITY_DATASET);
			map.put(PROPERTITY_DATASET, dataset);
			
			String id = view.getString(ComponentConfig.PROPERTITY_ID, "");
			if("".equals(id)) {
				id = IDGenerator.getInstance().generate();
			}
			id = uncertain.composite.TextParser.parse(id, model);
			view.putString(ComponentConfig.PROPERTITY_ID, id);
			String type = view.getString(NavBar.PROPERTITY_NAVBAR_TYPE,"complex");
			map.put(PROPERTITY_NAVBAR_TYPE, type);
			map.put(PROPERTITY_MAX_PAGE_COUNT, new Integer(view.getInt(PROPERTITY_MAX_PAGE_COUNT,10)));
			if("simple".equalsIgnoreCase(type)||"tiny".equalsIgnoreCase(type)){
				view.put(ComponentConfig.PROPERTITY_CLASSNAME, view.getString(ComponentConfig.PROPERTITY_CLASSNAME)+" simple-navbar");
				createSimpleNavBar(session, context);
			}else{
				createComplexNavBar(session, context);
			}
		} catch (SAXException e) {
			throw new IOException(e.getMessage());
		}
		super.onCreateViewContent(session, context);
	}
	private void createSimpleNavBar(BuildSession session, ViewContext context) throws IOException, SAXException{
		CompositeMap view = context.getView();
		CompositeLoader loader = new CompositeLoader();
		String pageInfoText = "<div atype='displayInfo' class='nav-label' style='float:right;'></div>";
		CompositeMap pageInfo = loader.loadFromString(pageInfoText,"UTF-8");
		view.addChild(pageInfo);
	}
	
	private void createComplexNavBar(BuildSession session, ViewContext context) throws IOException, SAXException{
		String theme = session.getTheme();
		Map map = context.getMap();
		CompositeMap view = context.getView();
		String dataset = view.getString(PROPERTITY_DATASET);
		if(!THEME_MAC.equals(theme)){
			view.addChild(createButton("nav-firstpage","background-position:1px 1px;","function(){$('"+dataset+"').firstPage()}",session.getLocalizedPrompt("HAP_FIRST_PAGE")));			
		}
		view.addChild(createButton("nav-prepage","background-position:0px -31px;","function(){$('"+dataset+"').prePage()}",session.getLocalizedPrompt("HAP_PREVIOUS_PAGE")));
		CompositeLoader loader = new CompositeLoader();
		if(!THEME_MAC.equals(theme)) {
			view.addChild(createSeparator());
		}else{
			view.addChild(createButton("nav-nextpage","background-position:1px -47px;","function(){$('"+dataset+"').nextPage()}",session.getLocalizedPrompt("HAP_NEXT_PAGE")));
			view.addChild(createButton("nav-refresh","background-position:0px -64px;","function(){$('"+dataset+"').query($('"+dataset+"').currentPage)}",session.getLocalizedPrompt("HAP_REFRESH")));
		}
		String pagetext = "<div class='item-label' atype='currentPage' style='"+(THEME_MAC.equals(theme) ? "display:none;" : "")+"margin-left:2px;margin-right:2px;'>&#160;</div>";
		CompositeMap pageinfo = loader.loadFromString(pagetext,"UTF-8");
		view.addChild(pageinfo);
		
		String inputId = IDGenerator.getInstance().generate();
		map.put("inputid", inputId);
		CompositeMap button = new CompositeMap("numberField");
		button.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
		button.putString(ComponentConfig.PROPERTITY_ID, inputId);
		button.put(ComponentConfig.PROPERTITY_IS_CUST, new Boolean(false));
		button.put(ComponentConfig.PROPERTITY_WIDTH, new Integer(30));
		button.put(NumberFieldConfig.PROPERTITY_ALLOWDECIMALS, new Boolean(false));
		button.put(NumberFieldConfig.PROPERTITY_ALLOWNEGATIVE, new Boolean(false));
		view.addChild(button);
		
		String text = "<div class='item-label' atype='pageInfo' style='"+(THEME_MAC.equals(theme) ? "display:none;" : "")+"margin-left:5px;margin-right:5px;'>    </div>";
		CompositeMap totalpage = loader.loadFromString(text,"UTF-8");
		view.addChild(totalpage);
		
		if(!THEME_MAC.equals(theme)) {
			view.addChild(createSeparator());
			view.addChild(createButton("nav-nextpage","background-position:1px -47px;","function(){$('"+dataset+"').nextPage()}",session.getLocalizedPrompt("HAP_NEXT_PAGE")));
			view.addChild(createButton("nav-lastpage","background-position:1px -15px","function(){$('"+dataset+"').lastPage()}",session.getLocalizedPrompt("HAP_LAST_PAGE")));
			view.addChild(createButton("nav-refresh","background-position:0px -64px;","function(){$('"+dataset+"').query($('"+dataset+"').currentPage)}",session.getLocalizedPrompt("HAP_REFRESH")));
			view.addChild(createSeparator());
		}
		
		if(view.getBoolean(PROPERTITY_PAGE_SIZE_EDITABLE, true)){
			String pageSizeInfo="<div class='item-label' atype='pageSizeInfo' style='"+(THEME_MAC.equals(theme) ? "display:none;" : "")+"margin-left:5px;margin-right:5px;'>    </div>";
			CompositeMap pagesize = loader.loadFromString(pageSizeInfo,"UTF-8");
			view.addChild(pagesize);
			String comboBoxId = IDGenerator.getInstance().generate();
			map.put("comboBoxId", comboBoxId);
			CompositeMap comboBox = new CompositeMap("comboBox");
			comboBox.put(ComponentConfig.PROPERTITY_IS_CUST, new Boolean(false));
			comboBox.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
			comboBox.putString(ComponentConfig.PROPERTITY_ID, comboBoxId);
			comboBox.put(ComponentConfig.PROPERTITY_WIDTH, new Integer(50));
			comboBox.put(ComboBoxConfig.PROPERTITY_FETCH_RECORD, new Boolean(false));
			view.addChild(comboBox);
			String pageSizeInfo2="<div class='item-label' atype='pageSizeInfo2' style='"+(THEME_MAC.equals(theme) ? "display:none;" : "")+"margin-left:5px;margin-right:5px;'>    </div>";
			CompositeMap pagesize2 = loader.loadFromString(pageSizeInfo2,"UTF-8");
			view.addChild(pagesize2);
			if(!THEME_MAC.equals(theme)) {
				view.addChild(createSeparator());
			}
			
			
		}
		String pageInfoText = "<div atype='displayInfo' class='item-label' style='float:right;'></div>";
		CompositeMap pageInfo = loader.loadFromString(pageInfoText,"UTF-8");
		view.addChild(pageInfo);
	}
	
	private CompositeMap createSeparator(){
		CompositeMap sep = new CompositeMap("separator");
		sep.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
		return sep;
	}
	
	private CompositeMap createButton(String clz,String style, String function, String title){
		CompositeMap button = new CompositeMap("button");
		button.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
		button.put(ComponentConfig.PROPERTITY_WIDTH, new Integer(16));
		button.put(ButtonConfig.PROPERTITY_ICON, "null");
		button.put(ComponentConfig.PROPERTITY_IS_CUST, new Boolean(false));
		button.put(ButtonConfig.PROPERTITY_BUTTON_CLASS, clz);
		button.put(ButtonConfig.PROPERTITY_TITLE, title);
		button.put(ButtonConfig.PROPERTITY_BUTTON_STYLE, style);
		if(!"".equals(function))button.put(ButtonConfig.PROPERTITY_CLICK, function);
		return button;
	}
	
}
