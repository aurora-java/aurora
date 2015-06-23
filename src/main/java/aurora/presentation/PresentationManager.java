/*
 * Created on 2007-8-4
 */
package aurora.presentation;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.logging.Level;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.core.DirectoryConfig;
import uncertain.core.IGlobalInstance;
import uncertain.core.UncertainEngine;
import uncertain.event.Configuration;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.exception.MessageFactory;
import uncertain.logging.DummyLogger;
import uncertain.logging.DummyLoggerProvider;
import uncertain.logging.ILogger;
import uncertain.logging.ILoggerProvider;
import uncertain.logging.LoggingContext;
import uncertain.ocm.OCManager;
import uncertain.pkg.IPackageManager;
import uncertain.pkg.PackageManager;
import uncertain.pkg.PackagePath;
import uncertain.proc.ParticipantRegistry;
import uncertain.schema.SchemaManager;
import uncertain.util.template.ITagCreatorRegistry;
import uncertain.util.template.TagCreatorRegistry;
import uncertain.util.template.TagTemplateParser;
import uncertain.util.template.TextTemplate;
import aurora.presentation.component.ScreenIncludeTagCreator;

/**
 * Manage all aspects of aurora presentation framework
 * 
 * @author Zhou Fan
 */
public class PresentationManager implements IGlobalInstance {
    
    // load builtin exceptions
    static {
        MessageFactory.loadResource("resources.aurora_presentation_exceptions");
    }

    public static final String LOGGING_TOPIC = "aurora.presentation";

    //static final TemplateBasedView TEMPLATE_BASED_VIEW = new TemplateBasedView();

    OCManager mOcManager;
    ParticipantRegistry mRegistry;
    // ITemplateFactory template_factory;
    UncertainEngine mUncertainEngine;
    TagTemplateParser mParser = new TagTemplateParser();

    // ElementID -> Component
    HashMap mComponentIdMap = new HashMap();

    PackageManager mPackageManager;
    ILogger mLogger;
    ILoggerProvider mLoggerProvider;

    IResourceUrlMapper mResourceUrlMapper = DefaultResourceMapper.getInstance();
    TagCreatorRegistry mTagCreatorRegistry = new TagCreatorRegistry();

    // DefaultViewBuilder for unknown view config
    DefaultViewBuilder mDefaultViewBuilder = new DefaultViewBuilder();

    // mappable properties
    // String resource_url;

    public PresentationManager() {
        // DocumentFactory docFact = new DocumentFactory();
        mOcManager = OCManager.getInstance();
        mRegistry = ParticipantRegistry.defaultInstance();
        CompositeLoader loader = CompositeLoader.createInstanceForOCM();
        mPackageManager = new PackageManager(loader, mOcManager, new SchemaManager() );
        ViewComponentPackage.loadBuiltInRegistry(mOcManager.getClassRegistry());
        mLogger = DummyLogger.getInstance();
        mLoggerProvider = DummyLoggerProvider.getInstance();
    }

    /*
     * public PresentationManager( OCManager manager ){ this.mOcManager =
     * manager; mRegistry = ParticipantRegistry.defaultInstance(); }
     */
    public PresentationManager(UncertainEngine engine) {
        this.mUncertainEngine = engine;
        this.mOcManager = engine.getOcManager();
        this.mRegistry = engine.getParticipantRegistry();
        /*
        mPackageManager = new PackageManager(engine.getCompositeLoader(),
                engine.getOcManager());
        */
        mPackageManager = engine.getPackageManager();
        ViewComponentPackage.loadBuiltInRegistry(engine.getClassRegistry());
        mLoggerProvider = LoggingContext.getLoggerProvider(engine
                .getObjectRegistry());
        mLogger = mLoggerProvider.getLogger(LOGGING_TOPIC);
        //TODO refactor to config files
        mTagCreatorRegistry.registerTagCreator("screen", new ScreenIncludeTagCreator(engine.getObjectRegistry()));
        mLogger.info("Aurora Presentation Framework Startup... ");
    }

    public BuildSession createSession(Writer writer) {
        BuildSession session = new BuildSession(this);
        session.setWriter(writer);
        ILogger logger = mLoggerProvider.getLogger(BuildSession.LOGGING_TOPIC);
        session.setLogger(logger);
        return session;
    }

    public Configuration createConfiguration() {
        if (mUncertainEngine == null)
            return new Configuration(mRegistry, mOcManager);
        else
            return mUncertainEngine.createConfig();
    }

    protected ViewComponent getComponent(CompositeMap view) {
        return (ViewComponent) mComponentIdMap.get(view.getQName());
    }

    /**
     * Get IViewBuilder instance associated with view config, to perform actual
     * building.
     * 
     * @param view_config
     * @return
     */
    public IViewBuilder getViewBuilder(CompositeMap view_config) {
        ViewComponent component = getComponent(view_config);
        if (component == null) {
            return getDefaultViewBuilder();
        } else {
            Class type = component.getBuilder();
            if (type == null)
                return null;
            try {
                return (IViewBuilder) mOcManager.getObjectCreator()
                        .createInstance(type);
            } catch (Exception ex) {
                throw new RuntimeException("can't create instance of "
                        + type.getName()
                        + " when getting IViewBuilder from view config");
            }
        }
    }

    public ViewComponentPackage getPackage(CompositeMap view) {
        ViewComponent component = getComponent(view);
        if (component == null)
            return null;
        return component.getOwner();
    }

    public ViewComponentPackage getPackage(String name) {
        return (ViewComponentPackage) mPackageManager.getPackage(name);
    }

    public IPackageManager getPackageManager() {
        return mPackageManager;
    }

    public ViewComponentPackage loadViewComponentPackage(String path)
            throws IOException {
        mLogger.log(Level.INFO, " =============== Loading package from "
                + path);
        ViewComponentPackage pkg = null;
        pkg = (ViewComponentPackage) mPackageManager.loadPackage(path,
                ViewComponentPackage.class);
        addPackage(pkg);
        mLogger.log(Level.INFO, "Loaded package " + pkg.getName());
        return pkg;
    }

    public void addPackage(ViewComponentPackage p) {
        if (p.getComponentMap() != null)
            mComponentIdMap.putAll(p.getComponentMap());

        if (mUncertainEngine != null)
            if (p.getClassRegistry() != null) {
                mUncertainEngine.getClassRegistry()
                        .addAll(p.getClassRegistry());
            }
        //mPackageManager.addPackage(p);
        mLogger.log(Level.CONFIG, "Components:{0}", new Object[] { p
                .getComponentMap() });       
    }
    
    /**
     * @param template_file
     * @return
     * @throws IOException
     */
    public TextTemplate parseTemplate(File template_file, ITagCreatorRegistry reg ) throws IOException {
        if(reg!=null){
            return mParser.buildTemplate(template_file, reg);
        }else{
            return mParser.buildTemplate(template_file);
        }
    }

    public TagTemplateParser getTemplateParser() {
        return mParser;
    }


    public void addPackages(PackagePath[] pkg_path) 
        throws IOException
    {
        DirectoryConfig dc = mUncertainEngine.getDirectoryConfig();
        mLogger.log(Level.CONFIG, "Loading " + pkg_path.length
                + " view component packages");
        for (int i = 0; i < pkg_path.length; i++) {
            String path = pkg_path[i].getPath();
            if(path==null)
                throw BuiltinExceptionFactory.createAttributeMissing(pkg_path[i], "path");
            path = dc.translateRealPath(path);
            DirectoryConfig.checkIsPathValid(pkg_path[i], path);
            loadViewComponentPackage(path);
        }
    }

    /**
     * @return the mResourceUrlMapper
     */
    public IResourceUrlMapper getResourceUrlMapper() {
        return mResourceUrlMapper;
    }

    /**
     * @param resourceUrlMapper
     *            the mResourceUrlMapper to set
     */
    public void setResourceUrlMapper(IResourceUrlMapper resourceUrlMapper) {
        mResourceUrlMapper = resourceUrlMapper;
    }

    public ITagCreatorRegistry getTagCreatorRegistry() {
        return mTagCreatorRegistry;
    }

    public ILoggerProvider getLoggerProvider() {
        return mLoggerProvider;
    }

    public ILogger getLogger() {
        return mLogger;
    }

    public void setLogger(ILogger logger) {
        mLogger = logger;
    }

    public IViewBuilder getDefaultViewBuilder() {
        return mDefaultViewBuilder;
    }

}
