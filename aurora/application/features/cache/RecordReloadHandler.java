package aurora.application.features.cache;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import uncertain.cache.ICache;
import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.composite.transform.GroupConfig;
import uncertain.composite.transform.GroupTransformer;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.IProcedureManager;
import uncertain.proc.Procedure;
import aurora.application.features.msg.IConsumer;
import aurora.application.features.msg.IMessage;
import aurora.application.features.msg.IMessageStub;
import aurora.application.features.msg.INoticerConsumer;
import aurora.database.FetchDescriptor;
import aurora.database.features.BmDmlEvent;
import aurora.database.service.BusinessModelService;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.database.service.SqlServiceContext;
import aurora.service.IServiceFactory;
import aurora.service.ServiceInvoker;

/*
 *约定：
 *1.如果配置了reloadBM或者reloadProc，那么IMessage.getProperites作为执行BM和Proc的上下文
 *2.如果没有配置reloadBM和reloadProc，那么IMessage.getProperites作为key和value的数据源 *
 */


public class RecordReloadHandler extends AbstractLocatableObject implements IEventHandler{
	
	protected String topic;
	private String operation;
	private String event;
	protected String reloadBM;
	protected String reloadProc;
	protected String groupByFields;
	
	protected ICacheProvider provider;
	protected IDatabaseServiceFactory dsFactory;
	protected ILogger logger;
	protected IProcedureManager procedureManager;
	protected IServiceFactory serviceFactory;
	
	@Override
	public void init(ICacheProvider provider, IObjectRegistry registry) {
		this.provider = provider;
		IMessageStub stub = (IMessageStub) registry.getInstanceOfType(IMessageStub.class);
		if (stub == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(this, IMessageStub.class, this.getClass().getName());
		IConsumer consumer = stub.getConsumer(topic);
		if (!(consumer instanceof INoticerConsumer))
			throw BuiltinExceptionFactory.createInstanceTypeWrongException(this.getOriginSource(), INoticerConsumer.class,
					IConsumer.class);
		((INoticerConsumer) consumer).addListener(event, this);
		dsFactory = (IDatabaseServiceFactory) registry.getInstanceOfType(IDatabaseServiceFactory.class);
		if (dsFactory == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(this, DatabaseServiceFactory.class, this.getClass().getName());
		if(operation == null){
			if(event == null)
				throw new IllegalArgumentException("event can't be null!");
			String[] parts = event.split(BmDmlEvent.SERPRATOR_CHAR);
			operation = parts[parts.length-1];
		}
		logger = LoggingContext.getLogger(this.getClass().getCanonicalName(), registry);
		procedureManager = (IProcedureManager) registry.getInstanceOfType(IProcedureManager.class);
		if (procedureManager == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(this, IProcedureManager.class, this.getClass().getName());
		serviceFactory = (IServiceFactory) registry.getInstanceOfType(IServiceFactory.class);
		if (serviceFactory == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(this, IServiceFactory.class, this.getClass().getName());
	}

	@Override
	public void setTopic(String topic) {
		this.topic = topic;

	}

	@Override
	public String getTopic() {
		return topic;
	}

	@Override
	public void notice(IMessage message) throws Exception {
		provider.writeLock();
		try{
			if(IEventHandler.OPERATIONS.delete.name().equals(operation)){
				delete(message);
			}else if(IEventHandler.OPERATIONS.update.name().equals(operation)){
				update(message);
			}else if(IEventHandler.OPERATIONS.insert.name().equals(operation)){
				insert(message);
			}else if(IEventHandler.OPERATIONS.reload.name().equals(operation)){
				reload(message);
			}
		}finally{
			provider.writeUnLock();
		}
	}
	public void delete(IMessage message) throws Exception {
		ICache cache = provider.getCache();
		if(cache == null)
			throw new IllegalArgumentException("Can't find cache instance from provier:"+provider.getCacheName());
		String messageKey = TextParser.parse(provider.getKey(), message.getProperties());
		cache.remove(messageKey);
		if(reloadBM != null){
			executeBM(message);
		}else if(reloadProc != null){
			executeProc(reloadProc,message.getProperties());
		}
	}
	private void executeBM(IMessage message) throws Exception{
		ICache cache = provider.getCache();
		if(cache == null)
			throw new IllegalArgumentException("Can't find cache instance from provier:"+provider.getCacheName());
		if(reloadBM != null){
			CompositeMap data = queryBM(reloadBM,message.getProperties());
			if(data == null)
				return;
			if(groupByFields != null){
				CompositeMap config = new CompositeMap();
				CompositeMap level1 = new CompositeMap();
				level1.put(GroupConfig.KEY_GROUP_KEY_FIELDS, groupByFields);
				level1.put(GroupConfig.KEY_RECORD_NAME, "level1");
				config.addChild(level1);
				data = GroupTransformer.transformByConfig((CompositeMap) data.clone(), config);
			}
			String type = provider.getType();
			List childs = data.getChilds();
			if(ICacheProvider.VALUE_TYPE.value.name().equals(type)){
				if(childs == null){
					String key = TextParser.parse(provider.getKey(), data);
					String value = TextParser.parse(provider.getValue(), data);
					cache.setValue(key,value);
				}else{
					for(Object child:data.getChilds()){
						CompositeMap record = (CompositeMap)child;
						String key = TextParser.parse(provider.getKey(), record);
						String value = TextParser.parse(provider.getValue(), record);
						cache.setValue(key,value);
					}
				}
			}else if(ICacheProvider.VALUE_TYPE.record.name().equals(type)){
				if(childs == null){
					String key = TextParser.parse(provider.getKey(), data);
					cache.setValue(key,data);
				}else{
					for(Object child:data.getChilds()){
						CompositeMap record = (CompositeMap)child;
						String key = TextParser.parse(provider.getKey(), record);
						cache.setValue(key,record);
					}
				}
			}else if(ICacheProvider.VALUE_TYPE.valueSet.name().equals(type)){
				if(childs == null){
					return;
				}else{
					for(Object child:data.getChilds()){
						CompositeMap record = (CompositeMap)child;
						String key = TextParser.parse(provider.getKey(), record);
						List new_values = record.getChilds();
						if(new_values == null)
							throw new IllegalArgumentException("Value type is 'valueSet', please group by the data first!");
						List<String> value_list = new LinkedList<String>();
						cache.setValue(key, value_list);
						for(Object value:new_values){
							CompositeMap newValue_record = (CompositeMap)value;
							String new_value =TextParser.parse(provider.getValue(), newValue_record);
							value_list.add(new_value);
						}
					}
				}
			}else if(ICacheProvider.VALUE_TYPE.recordSet.name().equals(type)){
				if(childs == null){
					return ;
				}else{
					for(Object child:data.getChilds()){
						CompositeMap record = (CompositeMap)child;
						String key = TextParser.parse(provider.getKey(), record);
						List new_values = record.getChilds();
						if(new_values == null)
							throw new IllegalArgumentException("Value type is 'recordSet', please group by the data first!");
						List<String> value_list = new LinkedList<String>();
						cache.setValue(key, value_list);
						value_list.addAll(new_values);
					}
				}
			}
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
	
	protected void executeProc(String procedure, CompositeMap context) {
		provider.writeLock();
		try {
			logger.log(Level.CONFIG, "load procedure:{0}", new Object[] { procedure });
			Procedure proc = null;
			try {
				proc = procedureManager.loadProcedure(procedure);
			} catch (Exception ex) {
				throw BuiltinExceptionFactory.createResourceLoadException(this, procedure, ex);
			}
			String name = "Cache." + procedure;
			if(context != null)
				ServiceInvoker.invokeProcedureWithTransaction(name, proc, serviceFactory, context);
			else{
				ServiceInvoker.invokeProcedureWithTransaction(name, proc, serviceFactory);
			}
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Error when invoking procedure " + procedure, ex);
		} finally {
			provider.writeUnLock();
		}
	}

	public void insert(IMessage message) throws Exception {
		refresh(message,IEventHandler.OPERATIONS.insert.name());
	}

	public void update(IMessage message) throws Exception {
		refresh(message,IEventHandler.OPERATIONS.update.name());
	}
	public void refresh(IMessage message,String operation) throws Exception{
		ICache cache = provider.getCache();
		CompositeMap properties = message.getProperties();
		if(cache == null)
			throw new IllegalArgumentException("Can't find cache instance from provier:"+provider.getCacheName());
		String messageKey = TextParser.parse(provider.getKey(), message.getProperties());
		if(reloadBM != null){
			if(IEventHandler.OPERATIONS.update.name().equals(operation))
				cache.remove(messageKey);
			executeBM(message);
		}else if(reloadProc != null){
			if(IEventHandler.OPERATIONS.update.name().equals(operation))
				cache.remove(messageKey);
			executeProc(reloadProc,properties);
		}
		else{
			String type = provider.getType();
			if(ICacheProvider.VALUE_TYPE.value.name().equals(type)){
				String value = TextParser.parse(provider.getValue(), properties);
				cache.setValue(messageKey, value);
			}else if(ICacheProvider.VALUE_TYPE.valueSet.name().equals(type)){
				List new_values = properties.getChilds();
				if(new_values == null)
					throw new IllegalArgumentException("Value type is 'valueSet', please group by the data first!");
				List<String> value_list = new LinkedList<String>();
				cache.setValue(messageKey, value_list);
				for(Object value:new_values){
					CompositeMap newValue_record = (CompositeMap)value;
					String new_value =TextParser.parse(provider.getValue(), newValue_record);
					value_list.add(new_value);
				}
			}else if(ICacheProvider.VALUE_TYPE.record.name().equals(type)){
				cache.setValue(messageKey, properties);
			}else if(ICacheProvider.VALUE_TYPE.recordSet.name().equals(type)){
				List new_values = properties.getChilds();
				if(new_values == null)
					throw new IllegalArgumentException("Value type is 'recordSet', please group by the data first!");
				List<String> value_list = new LinkedList<String>();
				cache.setValue(messageKey, value_list);
				value_list.addAll(new_values);
			}
			
		}
	}

	public void reload(IMessage message) throws Exception {
		provider.reload();
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getReloadBM() {
		return reloadBM;
	}

	public void setReloadBM(String reloadBM) {
		this.reloadBM = reloadBM;
	}

	public String getReloadProc() {
		return reloadProc;
	}

	public void setReloadProc(String reloadProc) {
		this.reloadProc = reloadProc;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getGroupByFields() {
		return groupByFields;
	}

	public void setGroupByFields(String groupByFields) {
		this.groupByFields = groupByFields;
	}
}
