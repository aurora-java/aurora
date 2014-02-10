package aurora.presentation.component.std;

import java.io.IOException;

import uncertain.cache.ICache;
import uncertain.cache.INamedCacheFactory;
import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import aurora.application.ApplicationConfig;
import aurora.application.ApplicationViewConfig;
import aurora.application.IApplicationConfig;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;

public class ScreenTitle implements IViewBuilder {
	
	public static final String VERSION = "$Revision$";
	private static final String SERVICE_NAME = "service_name";
	private static final String SCREEN_TITLE = "title";
	private static final String DEFAULT_CLASS = "screenTitle";
	
	private ICache mResourceCache = null;
	
	public ScreenTitle(INamedCacheFactory cf,IObjectRegistry registry) {
		ApplicationConfig mApplicationConfig = (ApplicationConfig) registry.getInstanceOfType(IApplicationConfig.class);
		if (mApplicationConfig != null) {
			ApplicationViewConfig application = mApplicationConfig.getApplicationViewConfig();
			String resourceCacheName = application.getResourceCacheName();
			if(resourceCacheName!=null){
				this.mResourceCache = cf.getNamedCache(resourceCacheName);
			}
		}
		
	}

	@Override
	public void buildView(BuildSession session, ViewContext view_context) throws IOException, ViewCreationException {
		CompositeMap model = view_context.getModel();
		CompositeMap context = model.getParent();
		String title = "";
		if(mResourceCache!=null){
			CompositeMap resMap = (CompositeMap)mResourceCache.getValue(context.getString(SERVICE_NAME));
			if(resMap!=null) title = resMap.getString(SCREEN_TITLE);
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("<span class='");
		sb.append(DEFAULT_CLASS);
		sb.append("'>");
		sb.append(session.getLocalizedPrompt(title));
		sb.append("</span>");
		session.getWriter().write(sb.toString());
		
	}

	@Override
	public String[] getBuildSteps(ViewContext context) {
		return null;
	}

}
