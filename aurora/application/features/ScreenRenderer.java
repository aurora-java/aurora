/*
 * Created on 2009-5-14
 */
package aurora.application.features;

import java.io.Writer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uncertain.composite.CompositeMap;
import uncertain.event.EventModel;
import uncertain.event.RuntimeContext;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;
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
import aurora.presentation.component.TemplateRenderer;
import aurora.service.IService;
import aurora.service.ServiceContext;
import aurora.service.ServiceInstance;
import aurora.service.http.HttpServiceInstance;

public class ScreenRenderer {

    public static final String HTML_PAGE = "html-page";

    private static final String DEFAULT_LANG_CODE = "ZHS";

    /**
     * @param prtManager
     */
    public ScreenRenderer(PresentationManager prtManager,
            IObjectRegistry registry, IDatabaseFactory factory) {
        super();
        mPrtManager = prtManager;
        mRegistry = registry;
        databaseFactory = factory;

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
                mDefaultTitle = view_config.getDefaultTitle();
            }
        }
    }

    PresentationManager mPrtManager;
    HttpServiceInstance mService;
    CompositeMap mContext;
    CompositeMap mScreen;
    IDatabaseFactory databaseFactory;
    IObjectRegistry mRegistry;
    ILookupCodeProvider lookupProvider;
    IMessageProvider mMessageProvider;

    // String mLangPath = "/session/@lang";
    ApplicationConfig mApplicationConfig;
    String mDefaultPackage;
    String mDefaultTemplate;
    String mDefaultTitle = "";

    // DatabaseServiceFactory mServiceFactory;

    public int onCreateView(ProcedureRunner runner) {
        mContext = runner.getContext();
        mService = (HttpServiceInstance) ServiceInstance.getInstance(mContext);
        ScreenConfig cfg = ScreenConfig.createScreenConfig(mService
                .getServiceConfigData());
        mScreen = cfg.getViewConfig();
        if (mScreen != null) {
            mScreen.setName(HTML_PAGE);
            mScreen.setNameSpaceURI(null);
            if (mScreen.getString(TemplateRenderer.KEY_TEMPLATE) == null)
                mScreen.putString(TemplateRenderer.KEY_TEMPLATE,
                        mDefaultTemplate);
            if (mScreen.getString(TemplateRenderer.KEY_PACKAGE) == null)
                mScreen.putString(TemplateRenderer.KEY_PACKAGE, mDefaultPackage);
            if (mScreen.getString(TemplateRenderer.KEY_TITLE) != null)
                mDefaultTitle = mScreen.getString(TemplateRenderer.KEY_TITLE);
            mContext.addChild(mScreen);
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

            HttpServletResponse response = mService.getResponse();
            HttpServletRequest request = mService.getRequest();

            // create BuildSession
            response.setContentType("text/html;charset=utf-8");
            Writer out = response.getWriter();
            session = mPrtManager.createSession(out);

            session.setContextPath(request.getContextPath());

            // set localized message provider for i18n
            // TODO *** MUST BE REMOVED! ***
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

            // TODO *** MUST BE REMOVED! ***
            lookupProvider = (ILookupCodeProvider) mRegistry
                    .getInstanceOfType(ILookupCodeProvider.class);
            session.setLookupProvider(lookupProvider);

            // set theme
            Cookie[] cookies = request.getCookies();
            String appTheme = "default";
            if (cookies != null) {
                for (int i = 0; i < cookies.length; i++) {
                    Cookie cookie = cookies[i];
                    String cname = cookie.getName();
                    if ("app_theme".equals(cname)) {
                        appTheme = cookie.getValue();
                    }
                }
            }
            session.setTitle(mDefaultTitle);
            session.setTheme(appTheme);
            session.setBaseConfig(mService.getServiceConfig());
            session.setInstanceOfType(IService.class, mService);
            session.setLogger(logger);
            
            // register instance in current context
            ctx.setInstanceOfType(BuildSession.class, session);
            
            session.buildView(mService.getServiceContext().getModel(), mScreen);
            
        }else{
            session.buildViewFromBegin(mService.getServiceContext().getModel(), mScreen);
        }

        session.getWriter().flush();
        return EventModel.HANDLE_NO_SAME_SEQUENCE;
    }

    // TODO *** ABSOLUTELY FOOLISH ***
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

    // TODO *** REALLY NECESSARY??? ***
    private String translateLanguageCode(String acceptLanguage) {
        String code = DEFAULT_LANG_CODE;
        if (acceptLanguage.indexOf("zh-cn") != -1) {
            code = "ZHS";
        } else if (acceptLanguage.indexOf("en-us") != -1) {
            code = "US";
        }
        return code;
    }
}
