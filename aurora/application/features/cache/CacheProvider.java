package aurora.application.features.cache;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import uncertain.cache.ConcurrentCache;
import uncertain.cache.ICache;
import uncertain.cache.ICacheProvider;
import uncertain.cache.INamedCacheFactory;
import uncertain.cache.IReadWriteLockable;
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
import aurora.database.FetchDescriptor;
import aurora.database.service.BusinessModelService;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.database.service.SqlServiceContext;
import aurora.service.IServiceFactory;
import aurora.service.ServiceInvoker;

public class CacheProvider extends AbstractLocatableObject implements ICacheProvider, IMessageListener, ILifeCycle {

	protected String cacheName;
	protected String loadProc;
	protected String refreshProc;
	protected String loadBM;
	protected String refreshBM;
	protected boolean loadOnStartup = true;
	protected IEventHandler[] eventHandlers;
	protected String value = "${@value}";
	protected String type = "value";
	protected String key = "${@key}";
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

	public CacheProvider(IObjectRegistry registry, INamedCacheFactory cacheFactory) {
		this.mRegistry = registry;
		this.mCacheFactory = cacheFactory;
	}

	public void onInitialize() throws Exception {
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
		//
		if (reloadMessage == null) {
			reloadMessage = cacheName + "_reload";
		}
		IMessageStub stub = (IMessageStub) mRegistry.getInstanceOfType(IMessageStub.class);
		if (stub == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(this, IMessageStub.class, this.getClass().getName());
		IConsumer consumer = stub.getConsumer(reloadTopic);
		if(consumer == null){
			if(!stub.isStarted()){
				throw new IllegalStateException("MessageStub is not started, please check the configuration.");
			}else{
				throw new IllegalStateException("MessageStub does not define the reloadTopic '"+reloadTopic+"', please check the configuration.");
			}
		}
		if (!(consumer instanceof INoticerConsumer))
			throw BuiltinExceptionFactory.createInstanceTypeWrongException(this.getOriginSource(), INoticerConsumer.class, IConsumer.class);
		((INoticerConsumer) consumer).addListener(reloadMessage, this);
		CacheProviderRegistry.put(cacheName, this);
		if (eventHandlers != null) {
			for (int i = 0; i < eventHandlers.length; i++) {
				eventHandlers[i].init(this, mRegistry);
			}
		}
		initResourcePath();
		if (loadOnStartup)
			initCacheData();
		inited = true;
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
		writeLock();
		try {
			if (isLoadByBM()) {
				executeBM(new CompositeMap());
			} else {
				executeProc(loadProc, null);
			}
		} finally {
			writeUnLock();
		}
	}

	protected void executeBM(CompositeMap context) throws Exception {
		if (loadBM != null) {
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
			List childs = data.getChilds();
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
					throw new IllegalArgumentException("Value type is 'valueSet', please group by the data first!");
				} else {
					for (Object child : data.getChilds()) {
						CompositeMap record = (CompositeMap) child;
						String key = TextParser.parse(getKey(), record);
						List new_values = record.getChilds();
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
					throw new IllegalArgumentException("Value type is 'valueSet', please group by the data first!");
				} else {
					for (Object child : data.getChilds()) {
						CompositeMap record = (CompositeMap) child;
						String key = TextParser.parse(getKey(), record);
						List new_values = record.getChilds();
						if (new_values == null)
							throw new IllegalArgumentException("Value type is 'valueSet', please group by the data first!");
						List<String> value_list = new LinkedList<String>();
						cache.setValue(key, value_list);
						value_list.addAll(new_values);
					}
				}
			}
		}
	}

	protected void executeProc(String procedure, CompositeMap context) {
		writeLock();
		try {
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
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Error when invoking procedure " + procedure, ex);
		} finally {
			writeUnLock();
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

	public CompositeMap queryBM(CompositeMap queryMap) throws Exception {
		return queryBM(getRefreshBM(), queryMap);
	}

	public void setName(String name) {
		this.cacheName = name;

	}

	public void reload() throws Exception {
		writeLock();
		try {
			inited = false;
			cache.clear();
			loadOnStartup = true;
			initCacheData();
			setLastReloadDate(new Date());
		} finally {
			writeUnLock();
		}
	}

	public Object getValue(CompositeMap parameter) {
		if (!inited)
			try {
				initCacheData();
			} catch (Exception e) {
				throw new RuntimeException("init cache:" + getCacheName() + " failed!");
			}
		readLock();
		try {
			String keyValue = TextParser.parse(key, parameter);
			return cache.getValue(keyValue);
		} finally {
			readUnLock();
		}
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

	public void writeLock() {
		if (!isConcurrent)
			return;
		((IReadWriteLockable) cache).writeLock().lock();
	}

	public void writeUnLock() {
		if (!isConcurrent)
			return;
		((IReadWriteLockable) cache).writeLock().unlock();
	}

	public void readLock() {
		if (!isConcurrent)
			return;
		((IReadWriteLockable) cache).readLock().lock();
	}

	public void readUnLock() {
		if (!isConcurrent)
			return;
		((IReadWriteLockable) cache).readLock().unlock();
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

	@Override
	public void onMessage(IMessage message) {
		try {
			if (reloadMessage.equals(message.getText()))
				reload();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "reload failed!", e);
		}
	}

	@Override
	public boolean startup() {
		cache = mCacheFactory.getNamedCache(cacheName);
		if (cache == null)
			throw new GeneralException("uncertain.cache.named_cache_not_found", new Object[] { cache }, this);
		if (isConcurrent) {
			cache = new ConcurrentCache(cache);
			mCacheFactory.setNamedCache(cacheName, cache);
		}
		return true;
	}

	@Override
	public void shutdown() {

	}

}
