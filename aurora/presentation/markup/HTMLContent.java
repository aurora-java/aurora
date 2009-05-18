/*
 * Created on 2007-8-4
 */
package aurora.presentation.markup;

import java.util.HashMap;
import java.util.Map;

import aurora.presentation.ViewContext;

/**
 * A helper class to create HTML related contents in ViewContext
 * HTMLContent
 * @author Zhou Fan
 *
 */
public class HTMLContent {
    

    public static final String KEY_CLASS = "class";
    public static final String KEY_STYLE = "html.style";
    public static final String KEY_NAME = "name";    
    public static final String KEY_ID = "id";    

    public static final String KEY_ATTRIBUTES = "attributes";
    public static final String KEY_HTML_EVENT_HANDLES = "html.event.handles";

    Map context_map;
    
    
    public HTMLContent( ViewContext context ){
        context_map = context.getMap();
    }
    
    public HTMLContent( Map map){
        this.context_map = map;
    }
    
    public Attributes getAttributes(String key){
        Attributes a = (Attributes)context_map.get(key);
        if(a==null){
            a = new Attributes();
            context_map.put(key, a);
        }
        return a;
    }
    
    public Attributes getHtmlAttributes(){
        return getAttributes(KEY_ATTRIBUTES);
    }

    
    /*
    public Map getAttributes(){
        return  getMapInternal(KEY_ATTRIBUTES);
    }
    
    public Map getStyle(){
        return  getMapInternal(KEY_STYLE);
    }
    */
    
    public void setName(String name){
        context_map.put(KEY_NAME, name);
    }
    
    public void setHtmlClass(String class_name){
        context_map.put(KEY_CLASS, class_name);
    }
    
    /**
     * Add a javascript event handle. If there is already a event handle with this event,
     * script_content will be appended after existing script code. 
     * @param event_name name of javascript event, such as "onclick"
     * @param script_content javascript content to be hooked with this event
     */
    public void addEventHandle(String event_name, String script_content){
        event_name = event_name.toLowerCase();
        Attributes events = getAttributes(KEY_HTML_EVENT_HANDLES);
        StringBuffer buf = (StringBuffer)events.get(event_name);
        if(buf==null){
            buf = new StringBuffer();
            events.put(event_name, buf);
        }
        buf.append(script_content);
    }
    
    public Map getEventHandles(){
        return (Map)context_map.get(KEY_HTML_EVENT_HANDLES);
    }

}
