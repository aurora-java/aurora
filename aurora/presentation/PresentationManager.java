/*
 * Created on 2007-8-4
 */
package aurora.presentation;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.logging.Level;

import uncertain.composite.CompositeMap;
import uncertain.core.IGlobalInstance;
import uncertain.core.UncertainEngine;
import uncertain.event.Configuration;
import uncertain.logging.DummyLogger;
import uncertain.logging.ILogger;
import uncertain.ocm.ISingleton;
import uncertain.ocm.OCManager;
import uncertain.pkg.PackageManager;
import uncertain.proc.ParticipantRegistry;
import uncertain.util.template.*;
/*
import aurora.util.template.TagTemplateParser;
import aurora.util.template.TextTemplate;
*/

/**
 * Manage all aspects of aurora presentation framework
 * @author Zhou Fan
 */
public class PresentationManager implements IGlobalInstance {
    
    public static final String LOGGING_TOPIC = "aurora.presentation.manager";
    
    static final TemplateBasedView TEMPLATE_BASED_VIEW = new TemplateBasedView();
    
    OCManager               mOcManager;
    ParticipantRegistry     mRegistry;
    //ITemplateFactory        template_factory;
    UncertainEngine         mUncertainEngine;
    TagTemplateParser       mParser  = new TagTemplateParser();
    
    // ElementID -> Component
    HashMap                 mComponentIdMap = new HashMap();
    
    PackageManager          mPackageManager;
    ILogger                 mLogger;
    IResourceUrlMapper      mResourceUrlMapper = DefaultResourceMapper.getInstance();
    TagCreatorRegistry      mTagCreatorRegistry = new TagCreatorRegistry();
    
    // mappable properties
    // String                  resource_url;
    
    public PresentationManager(){
        mOcManager = OCManager.getInstance();
        mRegistry = ParticipantRegistry.defaultInstance();
        mPackageManager = new PackageManager();
        mLogger = DummyLogger.getInstance();
    }
/*
    public PresentationManager( OCManager manager ){
        this.mOcManager = manager;
        mRegistry = ParticipantRegistry.defaultInstance();
    }
*/    
    public PresentationManager( UncertainEngine engine){
        this.mUncertainEngine = engine;
        this.mOcManager = engine.getOcManager();
        this.mRegistry = engine.getParticipantRegistry();
        mPackageManager = new PackageManager(engine.getCompositeLoader(), engine.getOcManager());
        ViewComponentPackage.loadBuiltInRegistry(engine.getClassRegistry());
        mLogger = engine.getLogger(LOGGING_TOPIC);
    }
    
/*
    public ITemplateFactory getTemplateFactory() {
        return template_factory;
    }

    public void setTemplateFactory(ITemplateFactory template_factory) {
        this.template_factory = template_factory;
    }
*/
    
    public BuildSession createSession( Writer writer ){
        BuildSession session = new BuildSession(this );
        session.setWriter(writer);
        return session;
    }
    
    public Configuration createConfiguration(){
        if(mUncertainEngine==null)
            return new Configuration(mRegistry, mOcManager);
        else
            return mUncertainEngine.createConfig();
    }
    
    protected ViewComponent getComponent( CompositeMap view ){
        return (ViewComponent)mComponentIdMap.get(view.getIdentifier());
    }
    
    /**
     * Get IViewBuilder instance associated with view config, to perform
     * actual building.
     * @param view_config
     * @return
     */
    public IViewBuilder getViewBuilder( CompositeMap view_config ){
        ViewComponent component = getComponent(view_config);
        if(component==null){
            return new TemplateBasedView();
        }
        else{
            Class type = component.getBuilder();
            if(type==null) return null;
            try{
                return (IViewBuilder)mOcManager.getObjectCreator().createInstance(type);
            } catch(Exception ex){
                throw new RuntimeException("can't create instance of "+type.getName()+" when getting IViewBuilder from view config");
            }
        }
    }
    
    public ViewComponentPackage getPackage( CompositeMap view ){
        ViewComponent component = getComponent(view);
        if(component==null) return null;
        return component.getOwner();
    }
    
    public ViewComponentPackage getPackage( String name){
        return (ViewComponentPackage)mPackageManager.getPackage(name);
    }
    
    public PackageManager getPackageManager(){
        return mPackageManager;
    }
    
    public ViewComponentPackage loadViewComponentPackage( String path )
        throws IOException
    {
        mLogger.log(Level.CONFIG, "Loading package from "+path);
        ViewComponentPackage pkg = (ViewComponentPackage)mPackageManager.loadPackage(path, ViewComponentPackage.class );
        addPackage(pkg);
        mLogger.log(Level.CONFIG, "Loaded package "+pkg.getName() );
        return pkg;
    }
    
    public void addPackage( ViewComponentPackage p ){
        mComponentIdMap.putAll(p.getComponentMap());
        if(mUncertainEngine!=null){
            mUncertainEngine.getClassRegistry().addAll(p.getClassRegistry());
        }
        mPackageManager.addPackage(p);
        //mPackageManager.l
        // Add all attached features
        // ClassRegistry cr = mOcManager.getClassRegistry();
        /*
        Iterator it = p.getComponents().iterator();
        while(it.hasNext()){
            ViewComponent c = (ViewComponent)it.next();
            Class[] types = c.getFeatureClassArray();
            if(types!=null)
                for(int i=0; i<types.length; i++){
                    FeatureAttach f = new FeatureAttach(c.getNameSpace(), c.getName(), types[i].getName());
                    try{
                        cr.addFeatureAttach(f);
                    }catch(ClassNotFoundException ex){
                        throw new RuntimeException(ex);
                    }
                }
            
        }
             */
    }
    
    /*
    public ViewComponentPackage loadPackage( File base_path, CompositeMap config ){
        ViewComponentPackage pkg = new ViewComponentPackage();
        //pkg.base_path = base_path;        
        mOcManager.populateObject(config, pkg);
        addPackage(pkg);
        return pkg;
    }
    */
 
    public TextTemplate parseTemplate( File template_file )
        throws IOException
    {
       return mParser.buildTemplate( new FileReader(template_file));
    }
    
    public TagTemplateParser getTemplateParser(){
        return mParser;
    }

    
    /*
    public String getThemeFromRequest( HttpServletRequest request ){
        HttpSession session = request.getSession();
        if(session == null) return "default";
        String theme = (String)session.getAttribute("_theme_");
        if(theme==null) return "default";
        return theme;
    }
    */
    
    public void addPackages( PackagePath[] pkg_path ){
        mLogger.log(Level.CONFIG, "Loading "+pkg_path.length+" view component packages");
        for( int i=0; i<pkg_path.length; i++){
            String path = pkg_path[i].getPath();
            if(path==null){
                mLogger.warning("Package No."+i+" doesn't define path property");
                continue;
            }
            try{
                loadViewComponentPackage(path);
            }catch(Exception ex){
                mLogger.log(Level.WARNING, "Error when loading package "+path, ex);
            }
        }
    }
    /**
     * @return the mResourceUrlMapper
     */
    public IResourceUrlMapper getResourceUrlMapper() {
        return mResourceUrlMapper;
    }
    /**
     * @param resourceUrlMapper the mResourceUrlMapper to set
     */
    public void setResourceUrlMapper(IResourceUrlMapper resourceUrlMapper) {
        mResourceUrlMapper = resourceUrlMapper;
    }
    
    public ITagCreatorRegistry getTagCreatorRegistry(){
        return mTagCreatorRegistry;
    }

}
