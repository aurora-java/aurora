/*
 * BuildSession.java
 *
 * Created on 2007Äê7ÔÂ8ÈÕ, 23:29
 */

package aurora.presentation;

import java.io.File;
import java.io.Writer;

import uncertain.composite.CompositeMap;
import uncertain.event.Configuration;
import uncertain.event.HandleManager;
import aurora.util.template.TextTemplate;

/**
 * The 'cursor' in View creation hierarchy 
 * @author  Zhou Fan
 * @version 
 */
public class BuildSession {
    
    // Writer to write output content
    protected                   Writer  writer;
    
    // Current Configuration generated from root view config
    Configuration               current_config;
    
    // PresentationManager that associated with this instance
    PresentationManager         owner;
    
    // A object that can identify client that make this request 
    Object                      client_info;
    
    // provider of current view
    ComponentPackage           current_package;
    
    // Theme name that applies to this session
    String                      theme;
    
    
    public BuildSession( PresentationManager pm){
        this.owner = pm;
    }
/*    
    public void setConfiguration( Configuration config){
        this.current_config = config;
    }
*/    
    public PresentationManager getPresentationManager(){
        return owner;
    }
    
    private void startSession( CompositeMap view){
        current_config = owner.createConfiguration();
        current_config.loadConfig(view);
    }
    
    private void endSession(){
        current_config = null;
        current_package = null;
    }
    


    public void buildView( CompositeMap model, CompositeMap view ) 
        throws Exception
    {
        boolean from_begin = false;
        if(current_config==null){
            startSession(view);
            from_begin = true;
        }
        current_package = owner.getPackage(view);
        
        ViewContext     context = new ViewContext(model,view);
  
        IViewBuilder builder = owner.getViewBuilder(view);
        if(builder==null) throw new IllegalStateException("Can't get IViewBuilder instance for "+view.toXML());
        String[]    events   = builder.getBuildSteps(context);
        if(events!=null)
            fireBuildEvents(events, context);            
        builder.buildView(this, context);
        
        if(from_begin){
            endSession();
        }
    }
    
    public TextTemplate getTemplate( String name ){
        return null;
        /*
        if(current_package==null)
        else{
            File file = current_package.getResourceFile();
        }
        */
    }
    
    public String getResourceURL(String resource_name) {
        if(current_package==null)
            return null;
        else
            return current_package.getResourceURL(theme, resource_name);
    }
    
    public File getResourceFile(String resource_name){
        if(current_package==null)
            return null;
        else
            return null;
    }    
/*
    public ViewContext fireBuildEvent( String event_name, CompositeMap model, CompositeMap view )
        throws Exception
    {
        ViewContext     context = new ViewContext(model,view);
        fireBuildEvent(event_name, context);
        return context;
    }
  */  
    public void fireBuildEvent( String event_name, ViewContext context)
        throws Exception
    {
        Object[] args = new Object[2];
        args[0] = this;
        args[1] = context;
        HandleManager manager = current_config.createHandleManager(context.getView());
        current_config.fireEvent(event_name, args, manager);
    }

    public void fireBuildEvents( String[] event_name, ViewContext context)
        throws Exception
    {
        Object[] args = new Object[2];
        args[0] = this;
        args[1] = context;
        HandleManager manager = current_config.createHandleManager(context.getView());
        for(int i=0; i<event_name.length; i++)
            current_config.fireEvent(event_name[i], args, manager);
    }
    
    public String getLocalizedPrompt(String key){
        return key;
    }
    
    /*
    public Object getInstanceOf(Class type, ViewContext context)
        throws Exception
    {
        String name = type.getName();
        name = name.substring(name.lastIndexOf('.')+1);
        fireBuildEvent( "Create"+name, context);
        return context.getMap().get("instance."+name);
    }
    */
    
    
    
    /*    
    
    public void fireBuildEvent( String event_name, Object[] args) 
        throws Exception
    {
        current_config.fireEvent(event_name, args);
    }
    */

    public void buildNamedPart( String name, CompositeMap model, ViewContext context ){
        
    }
    
    
    public Writer getWriter(){
        return writer;
    }
/*
    public String parseText( String content, CompositeMap model){
        return TextParser.parse(content, model);
    }
  */
    
    /**
     * @param writer A java.io.Writer to write content
     */
    public void setWriter(Writer writer) {
        this.writer = writer;
    }
    
    /**
     * @return An object that can identify client request
     */
    public Object getClientInfo() {
        return client_info;
    }
    /**
     * @param Sets an object that can identify client request, such as a HttpSession 
     */
    public void setClientInfo(Object client_info) {
        this.client_info = client_info;
    }
    /**
     * @return the theme
     */
    public String getTheme() {
        return theme;
    }
    /**
     * @param theme the theme to set
     */
    public void setTheme(String theme) {
        this.theme = theme;
    }
}
