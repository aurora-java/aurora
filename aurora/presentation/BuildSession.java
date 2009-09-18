/*
 * Created on 2007-7-8, 23:29
 */

package aurora.presentation;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.event.Configuration;
import uncertain.event.HandleManager;
import uncertain.event.RuntimeContext;
import uncertain.logging.DummyLogger;
import uncertain.logging.ILogger;
import uncertain.logging.ILoggerProvider;
import uncertain.util.template.TextTemplate;

/**
 * The 'cursor' in View creation hierarchy 
 * @author  Zhou Fan
 * @version 
 */
public class BuildSession {
    
    public static final String LOGGING_TOPIC = "aurora.presentation.buildsession";
    
    // Writer to write output content
    protected                   Writer  mWriter;
    
    // Current Configuration generated from root view config
    Configuration               mCurrentConfig;
    
    // PresentationManager that associated with this instance
    PresentationManager         mOwner;

    // provider of current view
    ViewComponentPackage           mCurrentPackage;
    
    // Theme name that applies to this session
    String                      mTheme = "default";
    
    // The build session data container
    CompositeMap              mSessionContext;
    
    // mSessionContext in RuntimeContext type
    RuntimeContext            mRuntimeContext;
    
    // A Set container to save included resource
    Set                       mIncludedResourceSet;    

    // Named ViewContext
    Map                       mNamedViewContextMap;   
    
    // Base configuration
    Configuration             mBaseConfig;

    public BuildSession( PresentationManager pm){
        this.mOwner = pm;
        mSessionContext = new CompositeMap("build-session");
        mRuntimeContext = RuntimeContext.getInstance(mSessionContext);
        
    }
/*    
    public void setConfiguration( Configuration config){
        this.current_config = config;
    }
*/    
    public PresentationManager getPresentationManager(){
        return mOwner;
    }
    
    private void startSession( CompositeMap view){
        mCurrentConfig = mOwner.createConfiguration();
        /*
        if(mBaseConfig!=null)
            mCurrentConfig.setParent(mBaseConfig);
            */
        mCurrentConfig.loadConfig(view);
        mCurrentConfig.setLogger(getLogger());
/*
        if(mSessionContext!=null)
            mSessionContext.clear();
*/            
    }
    
    private void endSession(){
        mCurrentConfig = null;
        mCurrentPackage = null;
  /*
        if(mSessionContext!=null)
            mSessionContext.clear();
                    */
    }
    
    /**
     * Create output content, from given data model and view config
     * @param model Data model in CompositeMap
     * @param view View configuration in CompositeMap
     * @throws Exception 
     */

    public void buildView( CompositeMap model, CompositeMap view ) 
        throws Exception
    {
        ILogger     logger = getLogger();
        boolean from_begin = false;
        if(mCurrentConfig==null){
            startSession(view);
            from_begin = true;
            logger.config("Start build session");
        }
        ViewComponentPackage old_package = mCurrentPackage;
        mCurrentPackage = mOwner.getPackage(view);        
        
        // Init ViewContext
        ViewContext     context = getNamedViewContext(view.getQName());
        if(context!=null){
            context.model = model;
            context.view = view;
        }else{
            context = new ViewContext(model,view);
        }
        
        
        IViewBuilder builder = mOwner.getViewBuilder(view);
        if(builder==null) throw new IllegalStateException("Can't get IViewBuilder instance for "+view.toXML());
        logger.log(Level.CONFIG, "building view: <{0}> -> {1}", new Object[]{view.getName(), builder});
        String[]    events   = builder.getBuildSteps(context);
        if(events!=null)
            fireBuildEvents(events, context);            
        builder.buildView(this, context);
        mCurrentPackage = old_package;
        if(from_begin){
            endSession();
            logger.config("End build session");
        }
    }
    
    public void buildViews( CompositeMap model, Collection view_list )
        throws Exception
    {
        Iterator it = view_list.iterator();
        while(it.hasNext()){
            CompositeMap view = (CompositeMap)it.next();
            buildView( model, view );
        }
    }
    
    public TextTemplate getTemplate( String name )
        throws IOException
    {
       if(mCurrentPackage==null) throw new IllegalStateException("package of current component is undefined");
       File template_file = mCurrentPackage.getTemplateFile( getTheme(), name);
       if( template_file==null ) return null;
       else return mOwner.parseTemplate(template_file);       
    }
    
    /**
     * Fire a build event
     * @param event_name name of event
     * @param context ViewContext that this event apply to
     * @param for_all_components whether this event will fire to current view component,
     *        or all components in one BuildSession
     * @throws Exception
     */
    public void fireBuildEvent( String event_name, ViewContext context, boolean for_all_components )
        throws Exception
    {
        Object[] args = new Object[2];
        args[0] = this;
        args[1] = context;
        if( for_all_components ){
            //mCurrentConfig.getLogger().info("to fire global "+event_name);
            mCurrentConfig.fireEvent(event_name, mSessionContext, args );
            if(mBaseConfig!=null){
                mBaseConfig.fireEvent(event_name, mSessionContext, args );
                mBaseConfig.getLogger().info("Fired "+event_name);
            }
        }else{
            HandleManager manager = mCurrentConfig.createHandleManager(context.getView());
            mCurrentConfig.fireEvent(event_name, args, mSessionContext, manager);            
        }
    }
    
    
    public void fireBuildEvent( String event_name, ViewContext context)
        throws Exception
    {
        fireBuildEvent( event_name, context, false );
    }

    public void fireBuildEvents( String[] event_name, ViewContext context)
        throws Exception
    {
        Object[] args = new Object[2];
        args[0] = this;
        args[1] = context;
        HandleManager manager = mCurrentConfig.createHandleManager(context.getView());
        for(int i=0; i<event_name.length; i++)
            mCurrentConfig.fireEvent(event_name[i], args, mSessionContext, manager);
    }
    
    public String getLocalizedPrompt(String key){
        return key;
    }
    
    public Writer getWriter(){
        return mWriter;
    }

    
    /**
     * @param writer A java.io.Writer to write content
     */
    public void setWriter(Writer writer) {
        this.mWriter = writer;
    }

/*    
    public Object getClientInfo() {
        return client_info;
    }

    public void setClientInfo(Object client_info) {
        this.client_info = client_info;
    }
*/
    
    /**
     * @return the theme
     */
    public String getTheme() {
        return mTheme;
    }
    /**
     * @param theme the theme to set
     */
    public void setTheme(String theme) {
        this.mTheme = theme;
    }
    /**
     * @return the mLogger
     */
    public ILogger getLogger() {
        ILogger logger = (ILogger)mRuntimeContext.getInstanceOfType(ILogger.class);
        return logger==null?DummyLogger.getInstance():logger;
    }
    /**
     * @param logger the mLogger to set
     */
    public void setLogger(ILogger logger) {
        mRuntimeContext.setInstanceOfType(ILogger.class, logger);
    }
    
    public void setLoggerProvider( ILoggerProvider provider ){
        mRuntimeContext.setInstanceOfType(ILoggerProvider.class, provider);
    }
    
    /**
     * Get web URL of a physical resource file, according to current theme
     * @param pkg 
     * @param resource
     * @return
     */
    public String getResourceUrl( ViewComponentPackage pkg, String resource ){
        IResourceUrlMapper mapper = mOwner.getResourceUrlMapper();
        if(mapper==null) throw new IllegalStateException("No instance of " + IResourceUrlMapper.class.getName() + " defined");
        String theme = null;
        if(pkg.isResourceExist(mTheme, resource))
            theme = mTheme;
        else{
            theme = ViewComponentPackage.DEFAULT_THEME;
            if(!pkg.isResourceExist(theme, resource)){
                this.getLogger().warning("Required resource not found:"+resource);
                return null;
            }
        }
        return mapper.getResourceUrl( pkg.getName(), theme, resource );            
    }
    
    
    public String getResourceUrl( String package_name, String resource ){
        ViewComponentPackage pkg = mOwner.getPackage(package_name);
        if(pkg==null) throw new IllegalArgumentException("packge "+package_name+" does not exist");
        return getResourceUrl( pkg, resource );
    }
    
    public String getResourceUrl( String resource ){
        if(mCurrentPackage==null) return null;
        return getResourceUrl( mCurrentPackage, resource );
    }
    
    /**
     * Get full resource name with package prefix concatenated
     * @param package_name name of package
     * @param resource name of resource file, without theme prefix
     */
    protected String getResourceFullName( String package_name, String resource ){
        return package_name +'.' + resource;
    }
    
    protected void checkResourceSet(){
        if( mIncludedResourceSet == null )
            mIncludedResourceSet = new HashSet();       
    }
    
    /**
     * Decides whether a resource file is already included in BuilSession
     * @param package_name
     * @param resource
     * @return false if this resource has not been included yet, and the resource will 
     * be marked as included. true if the resource is already marked as included.
     */
    public boolean includeResource( String package_name, String resource ){
        String full_name = getResourceFullName(package_name, resource);
        checkResourceSet();
        if( mIncludedResourceSet.contains(full_name))
            return true;
        else{
            mIncludedResourceSet.add(full_name);
            return false;
        }
    }
    
    public boolean includeResource( String resource ){
        String pkg_name = mCurrentPackage==null?null:mCurrentPackage.getName();
        return includeResource( pkg_name, resource);
    }
    
    public void setResourceIncluded( String package_name, String resource, boolean included ){
        String full_name = getResourceFullName(package_name, resource);
        checkResourceSet();
        if( included )
            mIncludedResourceSet.add(full_name);
        else
            mIncludedResourceSet.remove(full_name);            
    }
    
    public ViewComponentPackage getCurrentPackage(){
        return mCurrentPackage;
    }

    public CompositeMap getSessionContext() {
        return mSessionContext;
    }
 

    public ViewContext  createNamedViewContext( QualifiedName qname ){
        if(mNamedViewContextMap==null)
            mNamedViewContextMap = new HashMap();        
        ViewContext context = (ViewContext)mNamedViewContextMap.get(qname);
        if(context==null){
            context = new ViewContext();
            mNamedViewContextMap.put(qname, context);
        }
        return context;
    }
    
    
    public ViewContext getNamedViewContext(  QualifiedName qname ){
        return mNamedViewContextMap == null ? null: (ViewContext)mNamedViewContextMap.get(qname);
    }
    
    public Configuration getBaseConfig() {
        return mBaseConfig;
    }
    
    public void setBaseConfig(Configuration baseConfig) {
        mBaseConfig = baseConfig;
    }
    
    public void setInstanceOfType( Class type, Object instance ){
        mRuntimeContext.setInstanceOfType(type, instance);
    }
    
    public Object getInstanceOfType( Class type ){
        return mRuntimeContext.getInstanceOfType(type);
    }
}
