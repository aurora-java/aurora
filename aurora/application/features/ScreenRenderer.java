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
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.ProcedureRunner;
import aurora.application.ApplicationConfig;
import aurora.application.ApplicationViewConfig;
import aurora.application.IApplicationConfig;
import aurora.application.config.ScreenConfig;
import aurora.i18n.DummyMessageProvider;
import aurora.i18n.ILocalizedMessageProvider;
import aurora.i18n.IMessageProvider;
import aurora.presentation.BuildSession;
import aurora.presentation.PresentationManager;
import aurora.presentation.component.TemplateRenderer;
import aurora.service.IService;
import aurora.service.ServiceInstance;
import aurora.service.http.HttpServiceInstance;

public class ScreenRenderer {

	public static final String HTML_PAGE = "html-page";

	private static final String DEFAULT_LANG_CODE = "ZHS";

	/**
	 * @param prtManager
	 */
	public ScreenRenderer(PresentationManager prtManager,IObjectRegistry registry) {
		super();
		mPrtManager = prtManager;
		mRegistry = registry;

		mMessageProvider = (IMessageProvider) mRegistry.getInstanceOfType(IMessageProvider.class);
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

	IObjectRegistry mRegistry;
	ILookupCodeProvider lookupProvider;
	IMessageProvider mMessageProvider;
//	String mLangPath = "/session/@lang";
	ApplicationConfig mApplicationConfig;
	String mDefaultPackage;
	String mDefaultTemplate;

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
				mScreen
						.putString(TemplateRenderer.KEY_PACKAGE,
								mDefaultPackage);
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
		HttpServletResponse response = mService.getResponse();
		HttpServletRequest request = mService.getRequest();

		// create BuildSession
		response.setContentType("text/html;charset=utf-8");
		Writer out = response.getWriter();
		BuildSession session = mPrtManager.createSession(out);

		// set localized message provider for i18n
		String language_code = getLanguageCode(runner, mService,
				mMessageProvider.getLangPath(), mMessageProvider.getDefaultLang());
		if (language_code != null) {
			ILocalizedMessageProvider lp = mMessageProvider
					.getLocalizedMessageProvider(language_code);
			session.setMessageProvider(lp);
		}
		
		lookupProvider =  (ILookupCodeProvider) mRegistry.getInstanceOfType(ILookupCodeProvider.class);
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
		session.setTheme(appTheme);
		session.setBaseConfig(mService.getServiceConfig());
		session.setInstanceOfType(IService.class, mService);
		session.setLogger(logger);
		session.buildView(mService.getServiceContext().getModel(), mScreen);
		out.flush();

		return EventModel.HANDLE_NO_SAME_SEQUENCE;

	}

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
