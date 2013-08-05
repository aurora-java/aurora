package aurora.presentation.component.std.config;

import aurora.application.AuroraApplication;
import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;

public class ComponentConfig extends DynamicObject {

	public static final String VERSION = "$Revision$";
	public static final String TAG_NAME = "component";
	public static final String PROPERTITY_ID = "id";
	public static final String PROPERTITY_NAME = "name";
	public static final String PROPERTITY_PROMPT = "prompt";
	public static final String PROPERTITY_PROMPT_STYLE = "promptstyle";
	public static final String PROPERTITY_STYLE = "style";
	public static final String PROPERTITY_VALUE = "value";
	public static final String PROPERTITY_WIDTH = "width";
	public static final String PROPERTITY_HEIGHT = "height";
	public static final String PROPERTITY_CLASSNAME = "classname";
	public static final String PROPERTITY_BINDTARGET = "bindtarget";
	public static final String PROPERTITY_HIDDEN = "hidden";
	public static final String PROPERTITY_TAB_INDEX = "tabindex";
	public static final String PROPERTITY_EVENTS = "events";
	public static final String PROPERTITY_IS_CUST = "iscust";
	public static final String PROPERTITY_MARGIN_WIDTH = "marginwidth";
	public static final String PROPERTITY_MARGIN_HEIGHT = "marginheight";
	public static final String PROPERTITY_OLD_WIDTH = "oldwidth";
	public static final String PROPERTITY_CLIENT_RESIZE = "clientresize";

	public static CompositeMap createContext(CompositeMap map, String tagName) {
		CompositeMap context = new CompositeMap(tagName);
		context.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
		if (map != null) {
			context.copy(map);
		}
		return context;
	}

	public String getId() {
		return getString(PROPERTITY_ID);
	}
	public String getId(String defaultValue) {
		return getString(PROPERTITY_ID,defaultValue);
	}

	public void setId(String id) {
		putString(PROPERTITY_ID, id);
	}

	public String getName() {
		return getString(PROPERTITY_NAME);
	}

	public void setName(String name) {
		putString(PROPERTITY_NAME, name);
	}

	public String getPrompt() {
		return getString(PROPERTITY_PROMPT,"");
	}

	public void setPrompt(String prompt) {
		putString(PROPERTITY_PROMPT, prompt);
	}

	public String getPromptStyle() {
		return getString(PROPERTITY_PROMPT_STYLE);
	}

	public void setPromptStyle(String style) {
		putString(PROPERTITY_PROMPT_STYLE, style);
	}

	public String getStyle(String defaultValue) {
		return getString(PROPERTITY_STYLE,defaultValue);
	}
	
	public String getStyle() {
		return getString(PROPERTITY_STYLE);
	}

	public void setStyle(String style) {
		putString(PROPERTITY_STYLE, style);
	}

	public String getValue() {
		return getString(PROPERTITY_VALUE);
	}
	
	public String getValue(String defaultValue) {
		return getString(PROPERTITY_VALUE,defaultValue);
	}

	public void setValue(String value) {
		putString(PROPERTITY_VALUE, value);
	}

	public int getWidth(CompositeMap model,int defaultValue) {
		String str = uncertain.composite.TextParser.parse(getString(PROPERTITY_WIDTH), model);
		if(null == str||"".equals(str)){
			return defaultValue;
		}
		return Integer.valueOf(str).intValue();
	}
	
	public int getWidth(int defaultValue) {
		return getInt(PROPERTITY_WIDTH, defaultValue);
	}
	
	public int getWidth() {
		return getInt(PROPERTITY_WIDTH, 150);
	}

	public void setWidth(int width) {
		putInt(PROPERTITY_WIDTH, width);
	}
	
	public int getHeight(CompositeMap model,int defaultValue) {
		String str = uncertain.composite.TextParser.parse(getString(PROPERTITY_HEIGHT), model);
		if(null == str||"".equals(str)){
			return defaultValue;
		}
		return Integer.valueOf(str).intValue();
	}
	
	public int getHeight(int defaultHeight) {
		return getInt(PROPERTITY_HEIGHT, defaultHeight);
	}

	public int getHeight() {
		return getInt(PROPERTITY_HEIGHT, 20);
	}

	public void setHeight(int height) {
		putInt(PROPERTITY_HEIGHT, height);
	}
	
	public String getClassName(String defaultValue) {
		return getString(PROPERTITY_CLASSNAME, defaultValue);
	}

	public String getClassName() {
		return getString(PROPERTITY_CLASSNAME);
	}

	public void setClassName(String className) {
		putString(PROPERTITY_CLASSNAME, className);
	}

	public String getBindTarget() {
		return getString(PROPERTITY_BINDTARGET, "");
	}

	public void setBindTarget(String target) {
		putString(PROPERTITY_BINDTARGET, target);
	}
	
	public Boolean getHidden(boolean defaultValue) {
		return getBoolean(PROPERTITY_HIDDEN,defaultValue);
	}

	public Boolean getHidden() {
		return getBoolean(PROPERTITY_HIDDEN);
	}

	public void setHidden(boolean hidden) {
		putBoolean(PROPERTITY_HIDDEN, hidden);
	}
	
	public Boolean isCust() {
		return getBoolean(PROPERTITY_IS_CUST);
	}

	public void setCust(boolean isCust) {
		putBoolean(PROPERTITY_IS_CUST, isCust);
	}

	public int getTabIndex() {
		return getInt(PROPERTITY_TAB_INDEX, 0);
	}

	public void setTabIndex(int width) {
		putInt(PROPERTITY_TAB_INDEX, width);
	}
	public boolean isClientResize() {
		return getBoolean(PROPERTITY_CLIENT_RESIZE, true);
	}
	
	public void setClientResize(boolean clientResize) {
		putBoolean(PROPERTITY_CLIENT_RESIZE, clientResize);
	}

	private CompositeMap getEvents() {
		CompositeMap context = getObjectContext();
		CompositeMap events = context.getChild(PROPERTITY_EVENTS);
		if (events == null) {
			events = new CompositeMap(PROPERTITY_EVENTS);
			context.addChild(events);
		}
		return events;
	}

	public void addEvent(EventConfig event) {
		CompositeMap events = getEvents();
		events.addChild(event.getObjectContext());
	}

	public Integer getMarginWidth(CompositeMap model) {
		String str = uncertain.composite.TextParser.parse(getString(PROPERTITY_MARGIN_WIDTH), model);
		if(null == str||"".equals(str)){
			return null;
		}
		return Integer.valueOf(str);
	}

	public void setMarginWidth(Integer w) {
		putInt(PROPERTITY_MARGIN_WIDTH, w);
	}
	
	public Integer getMarginHeight(CompositeMap model) {
		String str = uncertain.composite.TextParser.parse(getString(PROPERTITY_MARGIN_HEIGHT), model);
		if(null == str||"".equals(str)){
			return null;
		}
		return Integer.valueOf(str);
	}

	public void setMarginHeight(Integer v) {
		putInt(PROPERTITY_MARGIN_HEIGHT, v);
	}

}
