package aurora.presentation.component.std;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uncertain.cache.ICache;
import uncertain.cache.INamedCacheFactory;
import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import aurora.application.ApplicationConfig;
import aurora.application.ApplicationViewConfig;
import aurora.application.IApplicationConfig;
import aurora.database.FetchDescriptor;
import aurora.database.service.BusinessModelService;
import aurora.database.service.DatabaseServiceFactory;
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
	private DatabaseServiceFactory databasefactory;
	
	public ScreenTitle(INamedCacheFactory cf,IObjectRegistry registry) {
		databasefactory = (DatabaseServiceFactory) registry.getInstanceOfType(DatabaseServiceFactory.class);
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
		
		try {
			BusinessModelService businessModelService = databasefactory.getModelService("sys.sys_service",context);
			Map<String,String> map = new HashMap<String,String>();
			map.put("service_name", context.getString(SERVICE_NAME));
			CompositeMap resultMap = businessModelService.queryAsMap(map,FetchDescriptor.fetchAll());
			List list = resultMap.getChilds();
			if(list!=null && list.size()>0){
				CompositeMap record = (CompositeMap)list.get(0);
				title = record.getString("title");
			}
		} catch (Exception e) {
			throw new ViewCreationException(e);
		}
		
//		if(mResourceCache!=null){
//			CompositeMap resMap = (CompositeMap)mResourceCache.getValue(context.getString(SERVICE_NAME));
//			if(resMap!=null) title = resMap.getString(SCREEN_TITLE);
//		}
		
		StringBuilder sb = new StringBuilder();
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
