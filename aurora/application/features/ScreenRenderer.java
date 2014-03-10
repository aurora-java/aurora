/*
 * Created on 2009-5-14
 */
package aurora.application.features;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uncertain.cache.ICache;
import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.event.Configuration;
import uncertain.event.EventModel;
import uncertain.event.RuntimeContext;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.IFeature;
import uncertain.proc.ProcedureRunner;
import aurora.application.ApplicationConfig;
import aurora.application.ApplicationViewConfig;
import aurora.application.IApplicationConfig;
import aurora.application.config.ScreenConfig;
import aurora.database.profile.IDatabaseFactory;
import aurora.i18n.DummyMessageProvider;
import aurora.i18n.ILocalizedMessageProvider;
import aurora.i18n.IMessageProvider;
import aurora.presentation.BuildSession;
import aurora.presentation.PresentationManager;
import aurora.presentation.cache.IResponseCacheProvider;
import aurora.presentation.component.TemplateRenderer;
import aurora.service.IService;
import aurora.service.ServiceContext;
import aurora.service.ServiceInstance;
import aurora.service.http.HttpServiceInstance;

public class ScreenRenderer implements IFeature {
	

    public static final String HTML_PAGE = "html-page";

    private static final String DEFAULT_LANG_CODE = "ZHS";

    private String contentType = "text/html;charset=utf-8";
    
    /**
     * @param prtManager
     */
    public ScreenRenderer(PresentationManager prtManager,
            IObjectRegistry registry, IDatabaseFactory factory) {
        super();
        mPrtManager = prtManager;
        mRegistry = registry;
        databaseFactory = factory;
        
        mCacheProvider = (IResponseCacheProvider)mRegistry.getInstanceOfType(IResponseCacheProvider.class);

        mMessageProvider = (IMessageProvider) mRegistry
                .getInstanceOfType(IMessageProvider.class);
        if (mMessageProvider == null)
            mMessageProvider = DummyMessageProvider.DEFAULT_INSTANCE;

        mApplicationConfig = (ApplicationConfig) mRegistry
                .getInstanceOfType(IApplicationConfig.class);
        if (mApplicationConfig != null) {
            ApplicationViewConfig view_config = mApplicationConfig
                    .getApplicationViewConfig();
            if (view_config != null) {
                mDefaultPackage = view_config.getDefaultPackage();
                mDefaultTemplate = view_config.getDefaultTemplate();
            }
        }
    }

    PresentationManager mPrtManager;
    HttpServiceInstance mService;
    CompositeMap mContext;
    CompositeMap mScreen;
    IResponseCacheProvider  mCacheProvider;
    
    
    IDatabaseFactory databaseFactory;
    IObjectRegistry mRegistry;
    IMessageProvider mMessageProvider;
    
    

    // String mLangPath = "/session/@lang";
    ApplicationConfig mApplicationConfig;
    String mDefaultPackage;
    String mDefaultTemplate;
    String mDefaultLabelSeparator;
    
//    String      mScreenCacheKey;
    boolean     mIsCache = false;

    // DatabaseServiceFactory mServiceFactory;

    public int onCreateView(ProcedureRunner runner) {
        mContext = runner.getContext();
        mService = (HttpServiceInstance) ServiceInstance.getInstance(mContext);
        ScreenConfig cfg = ScreenConfig.createScreenConfig(mService
                .getServiceConfigData());
        mIsCache = cfg.isCacheEnabled();
/*
        mScreenCacheKey = cfg.getCacheKey();
        if(mScreenCacheKey!=null){
            if(mCacheProvider==null)
                throw new IllegalStateException("cacheKey is set in screen, but no IResponseCacheProvider found");
            mScreenCacheKey = mCacheProvider.getFullCacheKey(mScreenCacheKey);
            mScreenCacheKey = TextParser.parse(mScreenCacheKey, mContext);
        }
*/        
        mScreen = cfg.getViewConfig();
        if (mScreen != null) {
            File source = mScreen.getSourceFile();
            if(source!=null)
                mScreen.setSourceFile(source);
            mScreen.setName(HTML_PAGE);
            mScreen.setNameSpaceURI(null);
            if (mScreen.getString(TemplateRenderer.KEY_TEMPLATE) == null)
                mScreen.putString(TemplateRenderer.KEY_TEMPLATE,
                        mDefaultTemplate);
            if (mScreen.getString(TemplateRenderer.KEY_PACKAGE) == null)
                mScreen.putString(TemplateRenderer.KEY_PACKAGE, mDefaultPackage);
            if (mScreen.getString(TemplateRenderer.KEY_LABEL_SEPARATOR) != null)
            	mDefaultLabelSeparator = mScreen.getString(TemplateRenderer.KEY_LABEL_SEPARATOR);
            if (mScreen.getString(TemplateRenderer.KEY_CONTENT_TYPE) != null)
                setContentType(mScreen.getString(TemplateRenderer.KEY_CONTENT_TYPE));           
            //mContext.addChild(mScreen);
            
            mContext.putBoolean("output", true);
        }
        return EventModel.HANDLE_NORMAL;
    }

    public int onBuildOutputContent(ProcedureRunner runner) throws Exception {
        if (mScreen == null)
            return EventModel.HANDLE_NORMAL;

        CompositeMap context = runner.getContext();

        ILogger logger = LoggingContext.getLogger(context,
                BuildSession.LOGGING_TOPIC);
        RuntimeContext ctx = ServiceContext.getInstance(context);
        BuildSession session = (BuildSession) ctx
                .getInstanceOfType(BuildSession.class);
       
        // check if BuildSession is created, if not yet then create one
        if (session == null) {
            ByteArrayOutputStream   baos = null;
            Writer out = null;
 
            HttpServletResponse response = mService.getResponse();
            HttpServletRequest request = mService.getRequest();

            response.setContentType(getContentType());
            if(mIsCache){
                baos = new ByteArrayOutputStream();
                out = new OutputStreamWriter(baos);
            }else{
                out = response.getWriter();
            }

            session = mPrtManager.createSession(out);
            session.setContextPath(request.getContextPath());

            // set localized message provider for i18n
            // TODO *** REFACTOR NEEDED ***
            CompositeMap dbProperties = databaseFactory.getProperties();
            if (dbProperties == null)
                throw new Exception("Database Properties undifined");
            String language_code = getLanguageCode(runner, mService,
                    dbProperties.getString("language_path"),
                    mMessageProvider.getDefaultLang());// mMessageProvider.getLangPath()

            session.setLanguage(language_code);
            ILocalizedMessageProvider lp = mMessageProvider
                    .getLocalizedMessageProvider(language_code);
            session.setMessageProvider(lp);

            // set theme
            Cookie[] cookies = request.getCookies();
            ApplicationViewConfig view_config = mApplicationConfig.getApplicationViewConfig();
            String appTheme = view_config.getDefaultTheme();
            if (cookies != null) {
                for (int i = 0; i < cookies.length; i++) {
                    Cookie cookie = cookies[i];
                    String cname = cookie.getName();
                    if ("app_theme".equals(cname)) {
                        appTheme = cookie.getValue();
                    }
                }
            }
            session.getSessionContext().put(TemplateRenderer.KEY_LABEL_SEPARATOR, mDefaultLabelSeparator);
            session.setTheme(appTheme);
            session.setBaseConfig(mService.getServiceConfig());
            session.setInstanceOfType(IService.class, mService);
            session.setLogger(logger);
            
            // register instance in current context
            ctx.setInstanceOfType(BuildSession.class, session);
            
            session.buildView(mService.getServiceContext().getModel(), mScreen);
            out.flush();
            
            // write cache if necessary
            if(mIsCache && mCacheProvider!=null){
                String output = baos.toString();
                output = output.replace("$c{", "${");
                ICache cache = mCacheProvider.getCacheForResponse();
                //String key = CachedScreenListener.getFullKey(mCacheProvider, mService, mScreenCacheKey);
                String key = CachedScreenListener.getCacheKey(context);
                if(key!=null)
                    cache.setValue(key, output);
                PrintWriter response_writer = response.getWriter();
                output = TextParser.parse(output, context);
                response_writer.write(output);
                response_writer.flush();
            }
        }else{
            IService oldService = (IService)session.getInstanceOfType(IService.class);
            session.setInstanceOfType(IService.class, mService);
            session.buildViewFromBegin(mService.getServiceContext().getModel(), mScreen);
            session.setInstanceOfType(IService.class, oldService);
            session.getWriter().flush();
        }

        return EventModel.HANDLE_NO_SAME_SEQUENCE;
    }

    // TODO *** REFACTOR NEEDED ***
    private String getLanguageCode(ProcedureRunner runner,
            HttpServiceInstance service, String langPath, String defaultLange) {
        String langCode = "";
        CompositeMap context = runner.getContext();
        if (!"".equals(langPath)) {
            Object lo = context.getObject(langPath);
            langCode = lo != null ? lo.toString() : "";
        }
        if ("".equals(langCode) && !"".equals(defaultLange)) {
            langCode = defaultLange;
        }
        if ("".equals(langCode)) {
            HttpServletRequest request = service.getRequest();
            String acceptLanguage = request.getHeader("Accept-Language");
            langCode = translateLanguageCode(acceptLanguage.toLowerCase());
        }
        return langCode;
    }

    // TODO *** REFACTOR NEEDED ***
    private String translateLanguageCode(String acceptLanguage) {
        String code = DEFAULT_LANG_CODE;
        if (acceptLanguage.indexOf("zh-cn") != -1) {
            code = "ZHS";
        } else if (acceptLanguage.indexOf("en-us") != -1) {
            code = "US";
        }
        return code;
    }

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

    public int attachTo(CompositeMap config_data, Configuration config) {
        //return IFeature.NO_CHILD_CONFIG;
        return IFeature.NORMAL;
    }
}
