package aurora.application.features.cache;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import uncertain.cache.ICache;
import uncertain.cache.INamedCacheFactory;
import uncertain.cache.ITransactionCache;
import uncertain.cache.TransactionCache;
import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.composite.transform.GroupConfig;
import uncertain.composite.transform.GroupTransformer;
import uncertain.core.ILifeCycle;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.exception.GeneralException;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.IProcedureManager;
import uncertain.proc.Procedure;
import aurora.application.features.msg.IConsumer;
import aurora.application.features.msg.IMessage;
import aurora.application.features.msg.IMessageListener;
import aurora.application.features.msg.IMessageStub;
import aurora.application.features.msg.INoticerConsumer;
import aurora.bm.BusinessModel;
import aurora.bm.ICachedDataProvider;
import aurora.bm.IModelFactory;
import aurora.database.FetchDescriptor;
import aurora.database.IResultSetConsumer;
import aurora.database.rsconsumer.CacheWriter;
import aurora.database.service.BusinessModelService;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.database.service.SqlServiceContext;
import aurora.service.IServiceFactory;
import aurora.service.ServiceInvoker;

public class CacheProvider extends AbstractLocatableObject implements ICacheProvider, IMessageListener, ILifeCycle {

	protected String cacheName;
	protected String loadProc;
	protected String refreshProc;
	// 'baseBM' usually used by ICachedDataProvider for cache join
	protected String baseBM;
	protected String loadBM;
	protected String refreshBM;
	protected boolean loadOnStartup = true;
	protected IEventHandler[] eventHandlers;
	protected String value = "${@value}";
	protected String type = "value";
	protected String key;
	protected boolean isConcurrent = true;
	protected String groupByFields;
	protected String cacheDesc;
	protected String reloadTopic = "dml_event";
	protected String reloadMessage;
	protected Date lastReloadDate;

	protected boolean inited = false;
	protected ILogger logger;
	protected IProcedureManager procedureManager;
	protected IObjectRegistry mRegistry;
	protected IServiceFactory serviceFactory;
	protected IDatabaseServiceFactory dsFactory;
	protected ICache cache;
	protected INamedCacheFactory mCacheFactory;
	protected IMessageStub messageStub;
	protected boolean shutdown = false;

	protected Timer reloadTimer;
	protected Object reloadLock = new Object();

	private ICachedDataProvider mCacheDataProvider;
	private IModelFactory mModelFactory;
	private Boolean enableResultSetConsumer = null;

	public CacheProvider(IObjectRegistry registry, INamedCacheFactory cacheFactory, ICachedDataProvider cacheDataProvider,
			IModelFactory modelFactory) {
		this.mRegistry = registry;
		this.mCacheFactory = cacheFactory;
		this.mCacheDataProvider = cacheDataProvider;
		this.mModelFactory = modelFactory;
	}

	public void initialize() {
		if (cacheName == null)
			throw BuiltinExceptionFactory.createAttributeMissing(this, "cacheName");
		if (key == null)
			throw BuiltinExceptionFactory.createAttributeMissing(this, "key");
		logger = LoggingContext.getLogger(this.getClass().getCanonicalName(), mRegistry);
		procedureManager = (IProcedureManager) mRegistry.getInstanceOfType(IProcedureManager.class);
		if (procedureManager == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(this, IProcedureManager.class, this.getClass().getName());
		serviceFactory = (IServiceFactory) mRegistry.getInstanceOfType(IServiceFactory.class);
		if (serviceFactory == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(this, IServiceFactory.class, this.getClass().getName());
		dsFactory = (IDatabaseServiceFactory) mRegistry.getInstanceOfType(IDatabaseServiceFactory.class);
		if (dsFactory == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(this, IDatabaseServiceFactory.class, this.getClass().getName());
		if (reloadMessage == null) {
			reloadMessage = cacheName + "_reload";
		}
		IMessageStub stub = (IMessageStub) mRegistry.getInstanceOfType(IMessageStub.class);

		if (eventHandlers != null && stub == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(this, IMessageStub.class, this.getClass().getName());
		if (stub != null) {
			if (!stub.isStarted())
				logger.warning("JMS MessageStub is not started, please check the configuration.");
			IConsumer consumer = stub.getConsumer(reloadTopic);
			if (consumer == null) {
				throw new IllegalStateException("MessageStub does not define the reloadTopic '" + reloadTopic
						+ "', please check the configuration.");
			}
			if (!(consumer instanceof INoticerConsumer))
				throw BuiltinExceptionFactory.createInstanceTypeWrongException(this.getOriginSource(), INoticerConsumer.class,
						IConsumer.class);
			((INoticerConsumer) consumer).addListener(reloadMessage, this);
		}
		if (eventHandlers != null) {
			for (int i = 0; i < eventHandlers.length; i++) {
				eventHandlers[i].init(this, mRegistry);
			}
		}
		initResourcePath();
		if (loadOnStartup)
			initCacheDataWithTrx();
		initReloadTimer();
		inited = true;
		CacheProviderRegistry.put(cacheName, this);
	}

	protected void initReloadTimer() {
		reloadTimer = new Timer(getCacheName() + "_reload_timer");
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				while (!shutdown) {
					synchronized (reloadLock) {
						try {
							reloadLock.wait();
						} catch (InterruptedException e) {
						}
						if (!shutdown)
							reloadWithTrx();
					}
				}
			}
		};
		reloadTimer.schedule(timerTask, 0);
	}
	 

	private void initResourcePath() {
		if (refreshBM != null) {
			if (loadBM == null && loadProc == null) {
				loadBM = refreshBM;
			}
		}
		if (loadBM != null) {
			if (refreshBM == null && refreshProc == null) {
				refreshBM = loadBM;
			}
		}
		if (refreshProc != null) {
			if (loadProc == null && loadBM == null) {
				loadProc = refreshProc;
			}
		}
		if (loadProc != null) {
			if (refreshBM == null && refreshProc == null) {
				refreshProc = loadProc;
			}
		}
		if (baseBM != null) {
			if (loadBM == null)
				loadBM = baseBM;
			if (refreshBM == null)
				refreshBM = baseBM;
		}
		// it maybe no need to be refresh,so no need to check 'refreshBM == null
		// && refreshProc == null';
		if (refreshBM != null && refreshProc != null) {
			throw BuiltinExceptionFactory.createConflictAttributesExcepiton(this, "refreshBM,refreshProc");
		}
		if (loadProc != null && loadBM != null) {
			throw BuiltinExceptionFactory.createConflictAttributesExcepiton(this, "loadProc,loadBM");
		}
		if (loadProc == null && loadBM == null) {
			throw BuiltinExceptionFactory.createOneAttributeMissing(this, "loadProc,loadBM");
		}
	}

	protected void initCacheData() throws Exception {
		if (isLoadByBM()) {
			executeBM(new CompositeMap());
		} else {
			executeProc(loadProc, null);
		}
	}
	private void initCacheDataWithTrx() {
		beginCacheTransaction();
		try {
			initCacheData();
			commitCache();
		} catch (Exception ex) {
			rollbackCache();
			throw new RuntimeException(ex);
		}
	}

	protected void executeBM(CompositeMap context) throws Exception {
		if (loadBM == null)
			return;
		IResultSetConsumer resutlSetConsumer = getResultSetConsumer();
		if (resutlSetConsumer != null) {
			queryBMWithConsumer(loadBM, resutlSetConsumer, context);
			return;
		}
		CompositeMap data = queryBM(loadBM, context);
		if (data == null)
			return;
		if (groupByFields != null) {
			CompositeMap config = new CompositeMap();
			CompositeMap level1 = new CompositeMap();
			level1.put(GroupConfig.KEY_GROUP_KEY_FIELDS, groupByFields);
			level1.put(GroupConfig.KEY_RECORD_NAME, "level1");
			config.addChild(level1);
			data = GroupTransformer.transformByConfig((CompositeMap) data.clone(), config);
		}
		String type = getType();
		@SuppressWarnings("unchecked")
		List<CompositeMap> childs = data.getChilds();
		if (ICacheProvider.VALUE_TYPE.value.name().equals(type)) {
			if (childs == null) {
				String key = TextParser.parse(getKey(), data);
				String value = TextParser.parse(getValue(), data);
				cache.setValue(key, value);
			} else {
				for (Object child : data.getChilds()) {
					CompositeMap record = (CompositeMap) child;
					String key = TextParser.parse(getKey(), record);
					String value = TextParser.parse(getValue(), record);
					cache.setValue(key, value);
				}
			}
		} else if (ICacheProvider.VALUE_TYPE.record.name().equals(type)) {
			if (childs == null) {
				String key = TextParser.parse(getKey(), data);
				cache.setValue(key, data);
			} else {
				for (Object child : data.getChilds()) {
					CompositeMap record = (CompositeMap) child;
					String key = TextParser.parse(getKey(), record);
					cache.setValue(key, record);
				}
			}
		} else if (ICacheProvider.VALUE_TYPE.valueSet.name().equals(type)) {
			if (childs == null) {
				return;
			} else {
				for (Object child : data.getChilds()) {
					CompositeMap record = (CompositeMap) child;
					String key = TextParser.parse(getKey(), record);
					@SuppressWarnings("unchecked")
					List<Object> new_values = record.getChilds();
					if (new_values == null)
						throw new IllegalArgumentException("Value type is 'valueSet', please group by the data first!");
					List<String> value_list = new LinkedList<String>();
					cache.setValue(key, value_list);
					for (Object value : new_values) {
						CompositeMap newValue_record = (CompositeMap) value;
						String new_value = TextParser.parse(getValue(), newValue_record);
						value_list.add(new_value);
					}
				}
			}
		} else if (ICacheProvider.VALUE_TYPE.recordSet.name().equals(type)) {
			if (childs == null) {
				return;
			} else {
				for (Object child : data.getChilds()) {
					CompositeMap record = (CompositeMap) child;
					String key = TextParser.parse(getKey(), record);
					@SuppressWarnings("unchecked")
					List<CompositeMap> new_values = record.getChilds();
					if (new_values == null)
						throw new IllegalArgumentException("Value type is 'recordSet', please group by the data first!");
					List<CompositeMap> value_list = new LinkedList<CompositeMap>();
					cache.setValue(key, value_list);
					value_list.addAll(new_values);
				}
			}
		}

	}

	private IResultSetConsumer getResultSetConsumer() {
		if (!enableResultSetConsumer)
			return null;
		if (groupByFields != null)
			throw BuiltinExceptionFactory.createConflictAttributesExcepiton(this, "enableResultSetConsumer,groupByFields");
		CacheWriter cw = new CacheWriter(mCacheFactory);
		cw.setCacheName(cacheName);
		cw.setRecordKey(key);
		return cw;
	}

	protected void executeProc(String procedure, CompositeMap context) throws Exception {
		logger.log(Level.CONFIG, "load procedure:{0}", new Object[] { procedure });
		Procedure proc = null;
		try {
			proc = procedureManager.loadProcedure(procedure);
		} catch (Exception ex) {
			throw BuiltinExceptionFactory.createResourceLoadException(this, procedure, ex);
		}
		String name = "Cache." + procedure;
		if (context != null)
			ServiceInvoker.invokeProcedureWithTransaction(name, proc, serviceFactory, context);
		else {
			ServiceInvoker.invokeProcedureWithTransaction(name, proc, serviceFactory);
		}
	}

	public CompositeMap queryBM(String bm_name, CompositeMap queryMap) throws Exception {
		SqlServiceContext sqlContext = dsFactory.createContextWithConnection();
		try {
			CompositeMap context = sqlContext.getObjectContext();
			if (context == null)
				context = new CompositeMap();
			BusinessModelService service = dsFactory.getModelService(bm_name, context);
			CompositeMap resultMap = service.queryAsMap(queryMap, FetchDescriptor.fetchAll());
			return resultMap;
		} finally {
			if (sqlContext != null)
				sqlContext.freeConnection();
		}
	}

	public void queryBMWithConsumer(String bm_name, IResultSetConsumer consumer, CompositeMap queryMap) throws Exception {
		SqlServiceContext sqlContext = dsFactory.createContextWithConnection();
		try {
			CompositeMap context = sqlContext.getObjectContext();
			if (context == null)
				context = new CompositeMap();
			BusinessModelService service = dsFactory.getModelService(bm_name, context);
			service.query(queryMap, consumer, FetchDescriptor.fetchAll());
		} finally {
			if (sqlContext != null)
				sqlContext.freeConnection();
		}
	}

	public CompositeMap queryBM(CompositeMap queryMap) throws Exception {
		return queryBM(getRefreshBM(), queryMap);
	}

	public void setName(String name) {
		this.cacheName = name;

	}

	public void reloadWithTrx() {
		beginCacheTransaction();
		try {
			reload();
			commitCache();
		} catch (Exception e) {
			rollbackCache();
			logger.log(Level.SEVERE, "", e);
		}
	}
	
	public void reload() {
		try {
			cache.clear();
			initCacheData();
			setLastReloadDate(new Date());
		} catch (Exception e) {
			logger.log(Level.SEVERE, "", e);
		}
	}

	public Object getValue(CompositeMap parameter) {
		if (!inited)
			try {
				initCacheDataWithTrx();
			} catch (Exception e) {
				throw new RuntimeException("init cache:" + getCacheName() + " failed!");
			}
		String keyValue = TextParser.parse(key, parameter);
		return cache.getValue(keyValue);
	}

	public String getRefreshBM() {
		return refreshBM;
	}

	public void setRefreshBM(String refreshBM) {
		this.refreshBM = refreshBM;
	}

	public String getCacheKey() {
		return key;
	}

	public void setCacheKey(String key) {
		this.key = key;
	}

	public boolean getLoadOnStart() {
		return loadOnStartup;
	}

	public void setLoadOnStart(boolean loadOnStart) {
		this.loadOnStartup = loadOnStart;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public IEventHandler[] getEventHandlers() {
		return eventHandlers;
	}

	public void setEventHandlers(IEventHandler[] eventHandlers) {
		this.eventHandlers = eventHandlers;
	}

	public String getLoadProc() {
		return loadProc;
	}

	public void setLoadProc(String loadProc) {
		this.loadProc = loadProc;
	}

	public String getRefreshProc() {
		return refreshProc;
	}

	public void setRefreshProc(String refreshProc) {
		this.refreshProc = refreshProc;
	}

	public String getBaseBM() {
		return baseBM;
	}

	public void setBaseBM(String baseBM) {
		this.baseBM = baseBM;
	}

	public String getLoadBM() {
		return loadBM;
	}

	public void setLoadBM(String loadBM) {
		this.loadBM = loadBM;
	}

	protected boolean isLoadByBM() {
		return loadBM != null;
	}

	protected boolean isRefreshByBM() {
		return refreshBM != null;
	}

	public boolean isConcurrent() {
		return isConcurrent;
	}

	public void setConcurrent(boolean isConcurrent) {
		this.isConcurrent = isConcurrent;
	}

	public boolean getConcurrent() {
		return isConcurrent;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCacheName() {
		return cacheName;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	public void removeKey(String key) {
		cache.remove(key);
	}

	@Override
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public ICache getCache() {
		return cache;
	}

	public String getGroupByFields() {
		return groupByFields;
	}

	public void setGroupByFields(String groupByFields) {
		this.groupByFields = groupByFields;
	}

	@Override
	public String getCacheDesc() {
		return cacheDesc;
	}

	public void setCacheDesc(String cacheDesc) {
		this.cacheDesc = cacheDesc;
	}

	@Override
	public String getReloadTopic() {
		return reloadTopic;

	}

	public void setReloadTopic(String reloadTopic) {
		this.reloadTopic = reloadTopic;
	}

	@Override
	public String getReloadMessage() {
		return reloadMessage;
	}

	public void setReloadMessage(String reloadMessage) {
		this.reloadMessage = reloadMessage;
	}

	@Override
	public Date getLastReloadDate() {
		return lastReloadDate;
	}

	public void setLastReloadDate(Date date) {
		this.lastReloadDate = date;
	}

	public boolean getEnableResultSetConsumer() {
		return enableResultSetConsumer;
	}

	public void setEnableResultSetConsumer(boolean enableResultSetConsumer) {
		this.enableResultSetConsumer = enableResultSetConsumer;
	}

	public void beginCacheTransaction() {
		if (isITransactionCache(cache))
			((ITransactionCache) cache).beginTransaction();
	}

	public void commitCache() {
		if (isITransactionCache(cache))
			((ITransactionCache) cache).commit();
	}

	public void rollbackCache() {
		if (isITransactionCache(cache))
			((ITransactionCache) cache).rollback();
	}

	private boolean isITransactionCache(ICache cache) {
		if (cache != null && cache instanceof ITransactionCache)
			return true;
		return false;
	}

	@Override
	public void onMessage(IMessage message) {
		try {
			if (reloadMessage.equals(message.getText())) {
				synchronized (reloadLock) {
					reloadLock.notify();
				}
			}
			// reload();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "reload failed!", e);
		}
	}

	@Override
	public boolean startup() {
		if (baseBM != null) {
			setDefaultConfigForBaseBM();
		}
		if (enableResultSetConsumer == null)
			enableResultSetConsumer = false;
		if (key == null)
			key = "${@key}";
		cache = mCacheFactory.getNamedCache(cacheName);
		if (cache == null)
			throw new GeneralException("uncertain.cache.named_cache_not_found", new Object[] { cache }, this);
		if (isConcurrent) {
			cache = new TransactionCache(cache);
			mCacheFactory.setNamedCache(cacheName, cache);
		}
		return true;
	}

	private void setDefaultConfigForBaseBM() {
		BusinessModel model;
		try {
			model = mModelFactory.getModel(baseBM);
		} catch (IOException e) {
			throw BuiltinExceptionFactory.createResourceLoadException(this, baseBM, e);
		}
		if (key == null) {
			key = mCacheDataProvider.getCacheKey(model);
		}else{
			if(!key.contains(baseBM))
				key = baseBM+"."+key;
		}
		if (cacheName == null) {
			cacheName = mCacheDataProvider.getCacheName(model);
		}
		if (cacheDesc == null)
			cacheDesc = cacheName;
		setType(ICacheProvider.VALUE_TYPE.record.name());
		if (enableResultSetConsumer == null)
			enableResultSetConsumer = true;
		if (eventHandlers == null) {
			EntityReloadHandler entityReloadHandler = new EntityReloadHandler();
			entityReloadHandler.setEntity(model.getBaseTable());
			entityReloadHandler.setReloadBM(baseBM);
			entityReloadHandler.setTopic(getReloadTopic());
			setEventHandlers(new IEventHandler[] { entityReloadHandler });
		}
	}

	@Override
	public void shutdown() {
		shutdown = true;
		CacheProviderRegistry.remove(cacheName);
		synchronized (reloadLock) {
			reloadLock.notify();
		}
		if (reloadTimer != null)
			reloadTimer.cancel();
		if(cache != null)
			cache.clear();
	}

}
