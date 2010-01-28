package aurora.presentation.component.std;

import java.io.IOException;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

public class NavBar extends ToolBar {
	
	public static final String PROPERTITY_DATASET = "dataset";
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
		CompositeMap view = context.getView();
		String dataset = view.getString(PROPERTITY_DATASET);
		
		String id = view.getString(PROPERTITY_ID, "");
		if("".equals(id)) {
			id = IDGenerator.getInstance().generate();
		}
		view.putString(PROPERTITY_ID, id);
		
		view.addChild(createButton("nav-prepage","function(){$('"+dataset+"').prePage()}","上一页"));
		view.addChild(createButton("nav-prerecord","function(){$('"+dataset+"').pre()}","上一条记录"));
		
		CompositeLoader loader = new CompositeLoader();
		String text = "<div style='padding:5px;'>页数:</div>";
		try {
			CompositeMap page = loader.loadFromString(text,"UTF-8");
			view.addChild(page);
		} catch (SAXException e) {
			e.printStackTrace();
		}
		CompositeMap button = new CompositeMap("textField");
		button.putString(PROPERTITY_ID, id+"_target");
		button.put(PROPERTITY_WIDTH, new Integer(40));
		view.addChild(button);
		
		view.addChild(createButton("nav-nextrecord","function(){$('"+dataset+"').next()}","下一条记录"));
		view.addChild(createButton("nav-nextpage","function(){$('"+dataset+"').nextPage()}","下一页"));
		view.addChild(createButton("nav-refresh","function(){$('"+dataset+"').query($('"+dataset+"').currentPage)}","刷新"));
		super.onCreateViewContent(session, context);
	}
	
	private CompositeMap createButton(String clz,String function, String title){
		CompositeMap button = new CompositeMap("button");
		button.put(Button.PROPERTITY_ICON, "null");
		button.put(Button.BUTTON_CLASS, clz);
		button.put(Button.PROPERTITY_TITLE, title);
		if(!"".equals(function))button.put(Button.PROPERTITY_CLICK, function);
		return button;
	}
	
}
