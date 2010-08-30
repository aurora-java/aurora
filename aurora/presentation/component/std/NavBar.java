package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;

import aurora.application.Namespace;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;

public class NavBar extends ToolBar {
	
	public static final String PROPERTITY_DATASET = "dataset";
	
	//TODO:多语言!
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
		try {
			CompositeMap view = context.getView();
			Map map = context.getMap();		
			
			String dataset = view.getString(PROPERTITY_DATASET);
			map.put(PROPERTITY_DATASET, dataset);
			
			String id = view.getString(ComponentConfig.PROPERTITY_ID, "");
			if("".equals(id)) {
				id = IDGenerator.getInstance().generate();
			}
			view.putString(ComponentConfig.PROPERTITY_ID, id);
			
			view.addChild(createButton("nav-firstpage","background-position:1px 1px;","function(){$('"+dataset+"').firstPage()}",session.getLocalizedPrompt("HAP_FIRST_PAGE")));
			view.addChild(createButton("nav-prepage","background-position:0px -31px;","function(){$('"+dataset+"').prePage()}",session.getLocalizedPrompt("HAP_PREVIOUS_PAGE")));
			view.addChild(createSeparator());
			
			CompositeLoader loader = new CompositeLoader();
			String pagetext = "<div class='item-label' style='margin-left:2px;margin-right:2px;'>"+session.getLocalizedPrompt("HAP_PAGENUM")+":</div>";
			CompositeMap pageinfo = loader.loadFromString(pagetext,"UTF-8");
			view.addChild(pageinfo);
			
			
			
			String inputId = IDGenerator.getInstance().generate();
			map.put("inputid", inputId);
			CompositeMap button = new CompositeMap("textField");
			button.setNameSpaceURI(Namespace.AURORA_FRAMEWORK_NAMESPACE);
			button.putString(ComponentConfig.PROPERTITY_ID, inputId);
			button.put(ComponentConfig.PROPERTITY_WIDTH, new Integer(30));
			view.addChild(button);
			
			String pageId = IDGenerator.getInstance().generate();
			map.put("pageId", pageId);
			//TODO:多语言
			String text = "<div id='"+pageId+"' class='item-label' style='margin-left:5px;margin-right:5px;'>共1页</div>";
			CompositeMap totalpage = loader.loadFromString(text,"UTF-8");
			view.addChild(totalpage);
			view.addChild(createSeparator());
			view.addChild(createButton("nav-nextpage","background-position:1px -47px;","function(){$('"+dataset+"').nextPage()}",session.getLocalizedPrompt("HAP_NEXT_PAGE")));
			view.addChild(createButton("nav-lastpage","background-position:1px -15px","function(){$('"+dataset+"').lastPage()}",session.getLocalizedPrompt("HAP_LAST_PAGE")));
			view.addChild(createButton("nav-refresh","background-position:0px -64px;","function(){$('"+dataset+"').query($('"+dataset+"').currentPage)}",session.getLocalizedPrompt("AURORA_REFRESH")));
			view.addChild(createSeparator());
			
			String infoId = IDGenerator.getInstance().generate();
			map.put("infoid", infoId);
			String pageInfoText = "<div id='"+infoId+"' class='item-label' style='float:right;'></div>";
			CompositeMap pageInfo = loader.loadFromString(pageInfoText,"UTF-8");
			view.addChild(pageInfo);
		} catch (SAXException e) {
			throw new IOException(e.getMessage());
		}
		super.onCreateViewContent(session, context);
	}
	
	private CompositeMap createSeparator(){
		CompositeMap sep = new CompositeMap("separator");
		sep.setNameSpaceURI(Namespace.AURORA_FRAMEWORK_NAMESPACE);
		return sep;
	}
	
	private CompositeMap createButton(String clz,String style, String function, String title){
		CompositeMap button = new CompositeMap("button");
		button.setNameSpaceURI(Namespace.AURORA_FRAMEWORK_NAMESPACE);
		button.put(ComponentConfig.PROPERTITY_WIDTH, new Integer(16));
		button.put(Button.PROPERTITY_ICON, "null");
		button.put(Button.BUTTON_CLASS, clz);
		button.put(Button.PROPERTITY_TITLE, title);
		button.put(Button.BUTTON_STYLE, style);
		if(!"".equals(function))button.put(Button.PROPERTITY_CLICK, function);
		return button;
	}
	
}
