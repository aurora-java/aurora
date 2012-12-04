package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;

public class Button extends Field {
	
	public static final String TAG_NAME = "button";
	
	public static final String CLASSNAME_WRAP = "item-btn";
	public static final String PROPERTITY_TEXT = "text";
	public static final String PROPERTITY_TEXT_HEIGHT = "text_height";
	public static final String PROPERTITY_ICON = "icon";
	public static final String BUTTON_STYLE = "btnstyle";
	public static final String BUTTON_CLASS = "btnclass";
	public static final String PROPERTITY_CLICK = "click";
	public static final String PROPERTITY_TITLE = "title";
	public static final String PROPERTITY_DISABLED = "disabled";
	private static final String PROPERTITY_ICON_ALIGN = "iconalign";
	private static final int DEFAULT_HEIGHT = 16;
	private static final int DEFAULT_WIDTH = 60;
	private static final int DEFAULT_ALIGN_TOP_HEIGHT = 36;
	private static final int DEFAULT_ALIGN_TOP_WIDTH = 50;
	private boolean isAlignTop = false;
	protected int getDefaultWidth(){
		return isAlignTop?DEFAULT_ALIGN_TOP_WIDTH:DEFAULT_WIDTH;
	}
	
	protected int getDefaultHeight(){
		return isAlignTop?DEFAULT_ALIGN_TOP_HEIGHT:DEFAULT_HEIGHT;
	}
	
	protected String getDefaultClass(BuildSession session, ViewContext context){
		CompositeMap view = context.getView();
		String text = view.getString(PROPERTITY_TEXT, "");
		String icon = view.getString(PROPERTITY_ICON, "");
		String align = view.getString(PROPERTITY_ICON_ALIGN, "left");
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
		Map map = context.getMap();
		String clickEvent = view.getString(PROPERTITY_CLICK, "");
		if(!"".equals(clickEvent)){
			if(clickEvent.indexOf("${") != -1)  //和$()有冲突
			clickEvent = uncertain.composite.TextParser.parse(clickEvent, model);
			addEvent(id, "click", clickEvent);
		}
		String text = view.getString(PROPERTITY_TEXT, "&#160;");
		text = session.getLocalizedPrompt(text);
		boolean disabled = view.getBoolean(PROPERTITY_DISABLED, false);
		if(disabled != false) {
			addConfig(PROPERTITY_DISABLED, Boolean.valueOf(disabled));
		}
		
		String icon = view.getString(PROPERTITY_ICON, "");
		String btnstyle = view.getString(BUTTON_STYLE, "");
		if(!"".equals(icon)){
			if(!"null".equalsIgnoreCase(icon))btnstyle+="background-image:url("+uncertain.composite.TextParser.parse(icon, model)+");";
		}
		Integer text_height = (Integer) map.get(ComponentConfig.PROPERTITY_HEIGHT);
		if(isAlignTop)text_height=null;
		map.put(PROPERTITY_TEXT_HEIGHT, text_height);
//		map.put(ComponentConfig.PROPERTITY_EVENTS, esb.toString());
		map.put(PROPERTITY_TEXT, text);
		map.put(BUTTON_CLASS, view.getString(BUTTON_CLASS, ""));
		map.put(PROPERTITY_TITLE, view.getString(PROPERTITY_TITLE, ""));
		map.put(ComponentConfig.PROPERTITY_TAB_INDEX, new Integer(view.getInt(ComponentConfig.PROPERTITY_TAB_INDEX, 0)));
		map.put(BUTTON_STYLE, btnstyle);
		map.put(CONFIG, getConfigString());
	}
}
