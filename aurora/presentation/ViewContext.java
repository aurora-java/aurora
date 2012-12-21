/*
 * Created on 2007-8-1
 */
package aurora.presentation;

import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.event.RuntimeContext;
import uncertain.util.template.TextTemplate;

/**
 * A container to hold data shared among view creators, such as html tag attributes, 
 * variable values, js/css paths, etc
 * @author Zhou Fan
 *
 */
public class ViewContext {
    
    public static final String KEY_TEMPLATE = "__view_template";
    
    CompositeMap        model;
    CompositeMap        view;
    CompositeMap        context_map;
    //IViewBuilder        view_builder;
    TextTemplate        view_template;

/*    
    public static BuildSession getBuildSession( CompositeMap view_context_map ){
        return (BuildSession)view_context_map.get(RuntimeContext.getTypeKey(BuildSession.class));
    }
    
    
    public void setBuildSession(BuildSession session){
        context_map.put(RuntimeContext.getTypeKey(BuildSession.class), session);
    }    
*/
    public static ViewContext getViewContext( CompositeMap view_context_map ){
        return (ViewContext)view_context_map.get(RuntimeContext.getTypeKey(ViewContext.class));
    }

    
    public ViewContext(){
        context_map = new CompositeMap();
        context_map.put(RuntimeContext.getTypeKey(ViewContext.class),this);
    }
    
    public ViewContext(CompositeMap model, CompositeMap view) {
        this();
        this.model = model;
        this.view = view;
    }

    /**
     * Get a Map containing all attributes in ViewContext
     * @return
     */
    public Map getMap() {
        return context_map;
    }
    
    public CompositeMap getContextMap(){
        return context_map;
    }
    
    public TextTemplate getTemplate(){
        if(view_template==null){
            return (TextTemplate)context_map.get(KEY_TEMPLATE);
        }else
            return view_template;
    }
    
    public void setTemplate(TextTemplate template){
        view_template = template;
    }
    
    public CompositeMap getModel(){
        return model;
    }
    
    public CompositeMap getView(){
        return view;
    }

    
    /**
     * Get a value from view config identified by view_attrib_name, and put it into 
     * context with key context_attrib_name 
     * @param view_attrib_name name of attribute to get from view config
     * @param context_attrib_name name of attribute to set in view context
     * @param parse_tag Weather the value contains access tag 
     */
    public void transferAttribute( String view_attrib_name, String context_attrib_name, boolean parse_tag){
        String value = view.getString(view_attrib_name);
        if( value != null && parse_tag ){
            value = parseString(value);
        }
        context_map.put(context_attrib_name, value);
    }
    
    public void transferAttribute( String name, boolean parse_tag){
        transferAttribute(name, name, parse_tag);
    }
    
    /**
     * Parse a string that contains access tag and replace with value 
     * got from model
     * @param content
     * @return
     */
    
    public String parseString( String content ){
        return TextParser.parse(content, model);
    }
    
    public String parseString( String content, CompositeMap map){
        return TextParser.parse(content, map);
    }
    
    public Object getInstance(Class type){
        return context_map.get(type.getName());
    }
    
    public void setInstance(Class type, Object instance){
        context_map.put(type.getName(), instance);
    }
}
