/*
 * Created on 2007-7-8, 23:29
 */

package aurora.presentation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
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
import uncertain.util.template.ITagCreatorRegistry;
import uncertain.util.template.TagCreatorRegistry;
import uncertain.util.template.TextTemplate;
import aurora.application.features.ILookupCodeProvider;
import aurora.i18n.DummyLocalizedMessageProvider;
import aurora.i18n.ILocalizedMessageProvider;
import aurora.i18n.LocalizedMessageTagCreator;

/**
 * The 'cursor' in View creation hierarchy
 * 
 * @author Zhou Fan
 * @version
 */
public class BuildSession {

    public static final String LOGGING_TOPIC = "aurora.presentation.buildsession";

    // Writer to write output content
    protected Writer mWriter;

    // Current Configuration generated from root view config
    Configuration mCurrentConfig;

    // PresentationManager that associated with this instance
    PresentationManager mOwner;

    // provider of current view
    ViewComponentPackage mCurrentPackage;

    // Theme name that applies to this session
    String mTheme = "default";

    // The build session data container
    CompositeMap mSessionContext;

    // mSessionContext in RuntimeContext type
    RuntimeContext mRuntimeContext;

    // A Set container to save included resource
    Set mIncludedResourceSet;

    // Named ViewContext
    Map mNamedViewContextMap;

    // Base configuration
    Configuration mBaseConfig;

    // Path of web context
    String contextPath;

    // provide localized message translate
    ILocalizedMessageProvider mMessageProvider = DummyLocalizedMessageProvider.DEFAULT_INSTANCE;

    // session level tag creator registry
    ITagCreatorRegistry mSessionTagCreatorRegistry;

    /** @todo refactor out */
    String title;
    String labelSeparator;
    String radioSeparator;
    ILookupCodeProvider lookupProvider;
    int defaultPageSize;

    String language;

    public BuildSession(PresentationManager pm) {
        this.mOwner = pm;
        mSessionContext = new CompositeMap("build-session");
        mRuntimeContext = RuntimeContext.getInstance(mSessionContext);

    }

    /*
     * public void setConfiguration( Configuration config){ this.current_config
     * = config; }
     */
    public PresentationManager getPresentationManager() {
        return mOwner;
    }

    private void startSession(CompositeMap view) {
        if(mBaseConfig!=null){
            mCurrentConfig = mBaseConfig;
            
        }else{
            mCurrentConfig = mOwner.createConfiguration();
            mCurrentConfig.setLogger(getLogger());
        }
        
        // Anyway root node of view will be loaded, in case new nodes were
        // added dynamiccally in previous process. Configuration will guarantee
        // no double creation of feature instance
        mCurrentConfig.loadConfig(view);
    }

    private void endSession() {
        mCurrentConfig = null;
        mCurrentPackage = null;
        /*
         * if(mSessionContext!=null) mSessionContext.clear();
         */
    }
    
    public void buildViewFromBegin( CompositeMap model, CompositeMap view )
        throws Exception
    {
        Configuration config = mCurrentConfig;
        ViewComponentPackage pkg = mCurrentPackage;
        mCurrentConfig = null;
        mCurrentPackage = null;
        try{
            buildView( model, view );
        }finally{
            mCurrentConfig = config;
            mCurrentPackage = pkg;
        }
    }

    /**
     * Create output content, from given data model and view config
     * 
     * @param model
     *            Data model in CompositeMap
     * @param view
     *            View configuration in CompositeMap
     * @throws Exception
     */

    public void buildView(CompositeMap model, CompositeMap view)
            throws Exception {
        ILogger logger = getLogger();
        boolean from_begin = false;
        if (mCurrentConfig == null) {
            startSession(view);
            from_begin = true;
            logger.config("Start build session");
        }
        ViewComponentPackage old_package = mCurrentPackage;
        mCurrentPackage = mOwner.getPackage(view);

        // Init ViewContext
        ViewContext context = getNamedViewContext(view.getQName());
        if (context != null) {
            context.model = model;
            context.view = view;
        } else {
            context = new ViewContext(model, view);
        }

//        if(from_begin){
//            this.fireBuildEvent("StartBuildSession", context, true);
//        }
        
        IViewBuilder builder = mOwner.getViewBuilder(view);
        if (builder == null)
            throw new IllegalStateException(
                    "Can't get IViewBuilder instance for " + view.toXML());
        logger.log(Level.CONFIG, "building view: <{0}> -> {1}", new Object[] {
                view.getName(), builder });
        String[] events = builder.getBuildSteps(context);
        if (events != null)
            fireBuildEvents(events, context);
        builder.buildView(this, context);
        mCurrentPackage = old_package;
        if (from_begin) {
            endSession();
            logger.config("End build session");
        }
    }

    /**
     * Build view content into a string buffer, without affecting current output
     * 
     * @param model
     *            Data model in CompositeMap
     * @param view
     *            View configuration in CompositeMap
     * @return Generated view content
     * @throws Exception
     */
    public String buildViewAsString(CompositeMap model, CompositeMap view)
            throws Exception {
        Writer old_writer = mWriter;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            PrintWriter out = new PrintWriter(bos);
            setWriter(out);
            buildView(model, view);
            out.flush();
            String str = bos.toString();
            out.close();
            bos.close();
            return str;
        } finally {
            mWriter = old_writer;
        }
    }

    public void buildViews(CompositeMap model, Collection view_list)
            throws Exception {
        if (view_list == null)
            return;
        Iterator it = view_list.iterator();
        while (it.hasNext()) {
            CompositeMap view = (CompositeMap) it.next();
            buildView(model, view);
        }
    }
    
    public String buildViewsAsString( CompositeMap model, Collection view_list )
        throws Exception
    {
        Writer old_writer = mWriter;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            PrintWriter out = new PrintWriter(bos);
            setWriter(out);
            buildViews(model, view_list);
            out.flush();
            String str = bos.toString();
            out.close();
            bos.close();
            return str;
        } finally {
            mWriter = old_writer;
        }        
    }

    public TextTemplate getTemplateByName(String name) throws IOException {
        if (mCurrentPackage == null)
            throw new IllegalStateException(
                    "package of current component is undefined");
        File template_file = mCurrentPackage.getTemplateFile(getTheme(), name);
        if (template_file == null)
            return null;
        else
            return mOwner.parseTemplate(template_file, getTagCreatorRegistry());
    }

    public TextTemplate getTemplateFromString(String content)
            throws IOException {
        return mOwner.getTemplateParser().buildTemplate(
                new StringReader(content), getTagCreatorRegistry());
    }

    /*
     * // Parse a String with tags, such as CompositeMap access tag,
     * multi-language translation tag public String parseText( String text,
     * CompositeMap context ) throws IOException { ByteArrayOutputStream baos =
     * new ByteArrayOutputStream(); OutputStreamWriter writer = new
     * OutputStreamWriter(baos); TextTemplate tplt =
     * getTemplateFromString(text); tplt.createOutput(writer, context);
     * writer.flush(); baos.flush(); String result = baos.toString();
     * baos.close(); return result; }
     */

    /**
     * Fire a build event
     * 
     * @param event_name
     *            name of event
     * @param context
     *            ViewContext that this event apply to
     * @param for_all_components
     *            whether this event will fire to current view component, or all
     *            components in one BuildSession
     * @throws Exception
     */
    public void fireBuildEvent(String event_name, ViewContext context,
            boolean for_all_components) throws Exception {
        Object[] args = new Object[2];
        args[0] = this;
        args[1] = context;
        if (for_all_components) {
            // mCurrentConfig.getLogger().info("to fire global "+event_name);
            mCurrentConfig.fireEvent(event_name, mSessionContext, args);
            if (mBaseConfig != null && mBaseConfig!=mCurrentConfig) {
                mBaseConfig.fireEvent(event_name, mSessionContext, args);
                //mBaseConfig.getLogger().info("Fired " + event_name);
            }
        } else {
            HandleManager manager = mCurrentConfig.createHandleManager(context
                    .getView());
            mCurrentConfig
                    .fireEvent(event_name, args, mSessionContext, manager);
        }
    }

    public void fireBuildEvent(String event_name, ViewContext context)
            throws Exception {
        fireBuildEvent(event_name, context, false);
    }

    public void fireBuildEvents(String[] event_name, ViewContext context)
            throws Exception {
        Object[] args = new Object[2];
        args[0] = this;
        args[1] = context;
        HandleManager manager = mCurrentConfig.createHandleManager(context
                .getView());
        for (int i = 0; i < event_name.length; i++)
            mCurrentConfig.fireEvent(event_name[i], args, mSessionContext,
                    manager);
    }

    public String getLocalizedPrompt(String key) {
        ILocalizedMessageProvider provider = getMessageProvider();
        if (provider != null) {
            String p = provider.getMessage(key);
            return p == null ? key : p;
        } else {
            return key;
        }
    }

    public Writer getWriter() {
        return mWriter;
    }

    /**
     * @param writer
     *            A java.io.Writer to write content
     */
    public void setWriter(Writer writer) {
        this.mWriter = writer;
    }

    /*
     * public Object getClientInfo() { return client_info; }
     * 
     * public void setClientInfo(Object client_info) { this.client_info =
     * client_info; }
     */

    /**
     * @return the theme
     */
    public String getTheme() {
        return mTheme;
    }

    /**
     * @param theme
     *            the theme to set
     */
    public void setTheme(String theme) {
        this.mTheme = theme;
    }

    /**
     * @return the mLogger
     */
    public ILogger getLogger() {
        ILogger logger = (ILogger) mRuntimeContext
                .getInstanceOfType(ILogger.class);
        return logger == null ? DummyLogger.getInstance() : logger;
    }

    /**
     * @param logger
     *            the mLogger to set
     */
    public void setLogger(ILogger logger) {
        mRuntimeContext.setInstanceOfType(ILogger.class, logger);
    }

    public void setLoggerProvider(ILoggerProvider provider) {
        mRuntimeContext.setInstanceOfType(ILoggerProvider.class, provider);
    }

    /**
     * Get web URL of a physical resource file, according to current theme
     * 
     * @param pkg
     * @param resource
     * @return
     */
    public String getResourceUrl(ViewComponentPackage pkg, String resource) {
        IResourceUrlMapper mapper = mOwner.getResourceUrlMapper();
        if (mapper == null)
            throw new IllegalStateException("No instance of "
                    + IResourceUrlMapper.class.getName() + " defined");
        String theme = null;
        if (pkg.isResourceExist(mTheme, resource))
            theme = mTheme;
        else {
            theme = ViewComponentPackage.DEFAULT_THEME;
            if (!pkg.isResourceExist(theme, resource)) {
                this.getLogger().warning(
                        "Required resource not found:" + resource);
                return null;
            }
        }
        StringBuffer buf = new StringBuffer();
        String path = mapper.getResourceUrl(pkg.getName(), theme, resource);
        String contextPath = getContextPath();
        if (contextPath != null) {
            buf.append(contextPath);
            if (!contextPath.endsWith("/")) {
                buf.append("/");
            }
        }
        buf.append(path);
        return buf.toString();

    }

    public String getResourceUrl(String package_name, String resource) {
        ViewComponentPackage pkg = mOwner.getPackage(package_name);
        if (pkg == null)
            throw new IllegalArgumentException("packge " + package_name
                    + " does not exist");
        return getResourceUrl(pkg, resource);
    }

    public String getResourceUrl(String resource) {
        if (mCurrentPackage == null)
            return null;
        return getResourceUrl(mCurrentPackage, resource);
    }

    /**
     * Get full resource name with package prefix concatenated
     * 
     * @param package_name
     *            name of package
     * @param resource
     *            name of resource file, without theme prefix
     */
    protected String getResourceFullName(String package_name, String resource) {
        return package_name + '.' + resource;
    }

    protected void checkResourceSet() {
        if (mIncludedResourceSet == null)
            mIncludedResourceSet = new HashSet();
    }

    /**
     * Decides whether a resource file is already included in BuilSession
     * 
     * @param package_name
     * @param resource
     * @return false if this resource has not been included yet, and the
     *         resource will be marked as included. true if the resource is
     *         already marked as included.
     */
    public boolean includeResource(String package_name, String resource) {
        String full_name = getResourceFullName(package_name, resource);
        checkResourceSet();
        if (mIncludedResourceSet.contains(full_name))
            return true;
        else {
            mIncludedResourceSet.add(full_name);
            return false;
        }
    }

    public boolean includeResource(String resource) {
        String pkg_name = mCurrentPackage == null ? null : mCurrentPackage
                .getName();
        return includeResource(pkg_name, resource);
    }

    public void setResourceIncluded(String package_name, String resource,
            boolean included) {
        String full_name = getResourceFullName(package_name, resource);
        checkResourceSet();
        if (included)
            mIncludedResourceSet.add(full_name);
        else
            mIncludedResourceSet.remove(full_name);
    }

    public ViewComponentPackage getCurrentPackage() {
        return mCurrentPackage;
    }

    public CompositeMap getSessionContext() {
        return mSessionContext;
    }

    public ViewContext createNamedViewContext(QualifiedName qname) {
        if (mNamedViewContextMap == null)
            mNamedViewContextMap = new HashMap();
        ViewContext context = (ViewContext) mNamedViewContextMap.get(qname);
        if (context == null) {
            context = new ViewContext();
            mNamedViewContextMap.put(qname, context);
        }
        return context;
    }

    public ViewContext getNamedViewContext(QualifiedName qname) {
        return mNamedViewContextMap == null ? null
                : (ViewContext) mNamedViewContextMap.get(qname);
    }

    public Configuration getBaseConfig() {
        return mBaseConfig;
    }

    public void setBaseConfig(Configuration baseConfig) {
        mBaseConfig = baseConfig;
    }

    public void setInstanceOfType(Class type, Object instance) {
        mRuntimeContext.setInstanceOfType(type, instance);
    }

    public Object getInstanceOfType(Class type) {
        return mRuntimeContext.getInstanceOfType(type);
    }

    /**
     * Parse a string containing tag, replacing tag with dynamic content from 
     * specified CompositeMap
     * @param text A String containing access tag
     * @param model A CompositeMap instance containing dynamic content
     * @return Parsed string
     * @throws IOException
     */
    public String parseString(String text, CompositeMap model)
            throws IOException {
        return TagParseUtil.parseStringFromSession(this, text, model);
    }

    public ILocalizedMessageProvider getMessageProvider() {
        return mMessageProvider;
    }

    public ITagCreatorRegistry getTagCreatorRegistry() {
        return mSessionTagCreatorRegistry == null ? mOwner
                .getTagCreatorRegistry() : mSessionTagCreatorRegistry;
    }

    /**
     * @return A session level ITagCreatorRegistry, to do some session scope
     *         template tag creation
     */
    public ITagCreatorRegistry getSessionTagCreatorRegistry() {
        return mSessionTagCreatorRegistry;
    }

    protected void setSessionTagCreatorRegistry(ITagCreatorRegistry registry) {
        this.mSessionTagCreatorRegistry = registry;
    }

    /** make sure that mSessionTagCreatorRegistry is created */
    private void checkTagCreator() {
        if (mSessionTagCreatorRegistry == null) {
            TagCreatorRegistry r = new TagCreatorRegistry();
            // r.setDefaultCreator( new CompositeMapTagCreator());
            mSessionTagCreatorRegistry = r;
            mSessionTagCreatorRegistry
                    .setParent(mOwner.getTagCreatorRegistry());
        }
    }

    public void setMessageProvider(ILocalizedMessageProvider messageProvider) {
        mMessageProvider = messageProvider;
        checkTagCreator();
        LocalizedMessageTagCreator creator = new LocalizedMessageTagCreator(
                messageProvider);
        mSessionTagCreatorRegistry
                .registerTagCreator(
                        LocalizedMessageTagCreator.LOCALIZED_MESSAGE_NAMESPACE,
                        creator);
    }

    public ILookupCodeProvider getLookupProvider() {
        return lookupProvider;
    }

    public void setLookupProvider(ILookupCodeProvider provider) {
        lookupProvider = provider;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getLabelSeparator() {
    	return labelSeparator;
    }
    
    public void setLabelSeparator(String labelSeparator) {
    	this.labelSeparator = labelSeparator;
    }
    public String getRadioSeparator() {
    	return radioSeparator;
    }
    
    public void setRadioSeparator(String radioSeparator) {
    	this.radioSeparator = radioSeparator;
    }
    
    public int getDefaultPageSize(){
    	return defaultPageSize;
    }
    
    public void setDefaultPageSize(int size){
    	this.defaultPageSize = size;
    }

}
