package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ButtonConfig;

@SuppressWarnings("unchecked")
public class Button extends Field {
	
	public Button(IObjectRegistry registry) {
		super(registry);
	}


	public static final String VERSION = "$Revision$";
	
	private String CLASSNAME_WRAP = "item-btn";
	private String PROPERTITY_TEXT_HEIGHT = "text_height";
	private int DEFAULT_HEIGHT = 16;
	private int DEFAULT_WIDTH = 60;
	private int DEFAULT_ALIGN_TOP_HEIGHT = 36;
	private int DEFAULT_ALIGN_TOP_WIDTH = 50;
	private boolean isAlignTop = false;
	
	protected int getDefaultWidth(){
		return isAlignTop?DEFAULT_ALIGN_TOP_WIDTH:DEFAULT_WIDTH;
	}
	
	protected int getDefaultHeight(){
		return isAlignTop?DEFAULT_ALIGN_TOP_HEIGHT:DEFAULT_HEIGHT;
	}
	
	protected String getDefaultClass(BuildSession session, ViewContext context){
		CompositeMap view = context.getView();
		ButtonConfig bc = ButtonConfig.getInstance(view);
		
		String text = bc.getText();
		String icon = bc.getIcon();
		String align = bc.getIconAlign();
		String wrapClass = CLASSNAME_WRAP;
		if(!"".equals(icon)){
			if(!"".equals(text)){
				if("top".equals(align)){
					isAlignTop = true;
					wrapClass += " item-btn-icon-text-top";
				}else{
					wrapClass += " item-btn-icon-text";
				}
			}else{
				wrapClass += " item-btn-icon";
			}
		}
		return wrapClass;
	}
	
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
		super.onCreateViewContent(session, context);
		CompositeMap model = context.getModel();
		CompositeMap view = context.getView();
		ButtonConfig bc = ButtonConfig.getInstance(view);
		
		Map map = context.getMap();
		String clickEvent = bc.getClick();
		if(!"".equals(clickEvent)){
			if(clickEvent.indexOf("${") != -1)  //和$()有冲突
			clickEvent = uncertain.composite.TextParser.parse(clickEvent, model);
			addEvent(id, "click", clickEvent);
		}
		String text = "".equals(bc.getText()) ?  "&#160;" : bc.getText();
		text = session.getLocalizedPrompt(text);
		boolean disabled = bc.getDisabled();
		if(disabled != false) {
			addConfig(ButtonConfig.PROPERTITY_DISABLED, Boolean.valueOf(disabled));
		}
		
		String icon = bc.getIcon();
		String btnstyle = bc.getButtonStyle();
		if(!"".equals(icon)){
			if(!"null".equalsIgnoreCase(icon))btnstyle+="background-image:url("+uncertain.composite.TextParser.parse(icon, model)+");";
		}
		Integer text_height = (Integer) map.get(ButtonConfig.PROPERTITY_HEIGHT);
		if(isAlignTop)text_height=null;
		map.put(PROPERTITY_TEXT_HEIGHT, text_height);
//		map.put(ComponentConfig.PROPERTITY_EVENTS, esb.toString());
		map.put(ButtonConfig.PROPERTITY_TEXT, text);
		map.put(ButtonConfig.PROPERTITY_BUTTON_CLASS, bc.getButtonClass());
		map.put(ButtonConfig.PROPERTITY_TITLE, bc.getTitle());
		map.put(ButtonConfig.PROPERTITY_TAB_INDEX, bc.getTabIndex());
		map.put(ButtonConfig.PROPERTITY_BUTTON_STYLE, btnstyle);
		map.put(CONFIG, getConfigString());
	}
}
