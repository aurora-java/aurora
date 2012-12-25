package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class EventConfig extends ComponentConfig {
	
	public static final String TAG_NAME = "event";
	
	public static final String PROPERTITY_EVENT_NAME = "name";
	public static final String PROPERTITY_EVENT_HANDLER = "handler";
	
	public static final String EVENT_ENTERDOWN = "enterdown";
	
	
	public static EventConfig getInstance(){
		EventConfig model = new EventConfig();
        model.initialize(EventConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static EventConfig getInstance(CompositeMap context){
		EventConfig model = new EventConfig();
        model.initialize(EventConfig.createContext(context,TAG_NAME));
        return model;
    }
	
	
	public String getEventName(){
		return getString(PROPERTITY_EVENT_NAME,"");		
	}
	public void setEventName(String eventName){
		putString(PROPERTITY_EVENT_NAME, eventName);
	}
	
	public String getHandler(){
		return getString(PROPERTITY_EVENT_HANDLER,"");
	}
	public void setHandler(String handler){
		putString(PROPERTITY_EVENT_HANDLER, handler);
	}
}
