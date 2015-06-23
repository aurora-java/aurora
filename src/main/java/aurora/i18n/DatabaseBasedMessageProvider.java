package aurora.i18n;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.core.IGlobalInstance;
import uncertain.ocm.IObjectRegistry;
import aurora.database.FetchDescriptor;
import aurora.database.service.BusinessModelService;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.database.service.SqlServiceContext;

/**
 * 
 * @version $Id: DatabaseBasedMessageProvider.java v 1.0 2010-6-10 上午10:15:07 IBM Exp $
 * @author <a href="mailto:njq.niu@hand-china.com">vincent</a>
 */
public class DatabaseBasedMessageProvider implements IMessageProvider,IGlobalInstance {
	
	private IDatabaseServiceFactory factory;
	//private IObjectRegistry registry;
	
	private boolean inited = false;
	
	private HashMap cache = new HashMap();
	
	/*
	public DatabaseBasedMessageProvider(IObjectRegistry registry) {
		super();
		this.registry = registry;
	}
	*/
	
	public DatabaseBasedMessageProvider(IDatabaseServiceFactory fact) {
	    this.factory = fact;
	}
	
	private String descModel;

	private String langPath = "";
	
	private String defaultLang = "";
	

	public String getDescModel() {
		return descModel;
	}

	public void setDescModel(String descModel) {
		this.descModel = descModel;
	}

	public String getLangPath() {
		return langPath;
	}

	public void setLangPath(String langPath) {
		this.langPath = langPath;
	}

	public String getDefaultLang() {
		return defaultLang;
	}

	public void setDefaultLang(String defaultLang) {
		this.defaultLang = defaultLang;
	}
	
	public void invalid() throws Exception{		
		inited = false;
		cache = new HashMap();
		init();
	}
	
	private void init() throws Exception{
		if(!inited){
			SqlServiceContext context = factory.createContextWithConnection();
			try {
				BusinessModelService service = factory.getModelService(getDescModel(),context.getObjectContext());
				CompositeMap resultMap = service.queryAsMap(new HashMap(), FetchDescriptor.fetchAll());
				cacheMessage(resultMap);
				inited = true;
			} finally {
                if (context != null)
                    context.freeConnection();
            }
		}
	}
	
	public void reload() throws Exception{
		this.invalid();
		this.init();
	}
	private void cacheMessage(CompositeMap map){
		if(map !=null){
			List list = map.getChildsNotNull();
			Iterator it = list.iterator();
			while(it.hasNext()){
				CompositeMap message = (CompositeMap)it.next();
				String language = message.getString("language");
				ILocalizedMessageProvider localMessageProvider = (ILocalizedMessageProvider)cache.get(language);
				if(localMessageProvider == null){
					localMessageProvider = new DefaultLocalizedMessageProvider();
					cache.put(language, localMessageProvider);
				}
				String code = message.getString("prompt_code");
				String description = message.getString("description");
				localMessageProvider.putMessage(code, description);
			}
		}
	}

	public void onInitialize() throws Exception {
		//factory = (DatabaseServiceFactory)registry.getInstanceOfType(DatabaseServiceFactory.class);
		init();
	}

	public ILocalizedMessageProvider getLocalizedMessageProvider(String language_code) {
		return (ILocalizedMessageProvider)cache.get(language_code);
	}

	public String getMessage(String language_code, String message_code) {
		ILocalizedMessageProvider localMessageProvider = (ILocalizedMessageProvider)cache.get(language_code);
		if(localMessageProvider == null) return message_code;
		return localMessageProvider.getMessage(message_code);
	}

	public String getMessage(String language_code, String message_code, Object[] params) {
		ILocalizedMessageProvider localMessageProvider = (ILocalizedMessageProvider)cache.get(language_code);
		if(localMessageProvider == null) return message_code;
		return localMessageProvider.getMessage(message_code, params);
	}

}
