package aurora.application.features.cache;

import java.util.logging.Level;

import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.IProcedureManager;
import aurora.application.features.msg.IConsumer;
import aurora.application.features.msg.IMessage;
import aurora.application.features.msg.IMessageStub;
import aurora.application.features.msg.INoticerConsumer;
import aurora.database.features.BmDmlEvent;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.service.IServiceFactory;

/*
 *约定：
 *1.如果配置了reloadBM或者reloadProc，那么IMessage.getProperites作为执行BM和Proc的上下文
 *2.如果没有配置reloadBM和reloadProc，那么IMessage.getProperites作为key和value的数据源 *
 */


public class EntityReloadHandler extends RecordReloadHandler{
	
	private String operations;
	private String entity;

	@Override
	public void init(ICacheProvider provider, IObjectRegistry registry) {
		this.provider = provider;
		if(entity == null)
			throw BuiltinExceptionFactory.createAttributeMissing(this, "entity");
		IMessageStub stub = (IMessageStub) registry.getInstanceOfType(IMessageStub.class);
		if (stub == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(this, IMessageStub.class, this.getClass().getName());
		IConsumer consumer = stub.getConsumer(topic);
		if (!(consumer instanceof INoticerConsumer))
			throw BuiltinExceptionFactory.createInstanceTypeWrongException(this.getOriginSource(), INoticerConsumer.class,
					IConsumer.class);
		dsFactory = (IDatabaseServiceFactory) registry.getInstanceOfType(IDatabaseServiceFactory.class);
		if (dsFactory == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(this, DatabaseServiceFactory.class, this.getClass().getName());
		if(operations == null){
				StringBuffer sb = new StringBuffer("");
				for (int j = 0; j < OPERATIONS.values().length; j++) {
					if (j > 0)
						sb.append(SERPRATOR_CHAR);
					sb.append(OPERATIONS.values()[j].name());
				}
				setOperations(sb.toString());
		}
		for (String op : getOperations().split(SERPRATOR_CHAR)) {
			String message = getEntity().toLowerCase() + BmDmlEvent.SERPRATOR_CHAR + op;
			((INoticerConsumer) consumer).addListener(message, this);
		}
		logger = LoggingContext.getLogger(this.getClass().getCanonicalName(), registry);
		procedureManager = (IProcedureManager) registry.getInstanceOfType(IProcedureManager.class);
		if (procedureManager == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(this, IProcedureManager.class, this.getClass().getName());
		serviceFactory = (IServiceFactory) registry.getInstanceOfType(IServiceFactory.class);
		if (serviceFactory == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(this, IServiceFactory.class, this.getClass().getName());
	}

	public String getOperations() {
		return operations;
	}

	public void setOperations(String operations) {
		this.operations = operations;
	}
	@Override
	public void onMessage(IMessage message){
		provider.writeLock();
		try{
			if(message == null)
				throw new IllegalArgumentException(" message can't be null!");
			String text = message.getText();
			if(text == null)
				throw new IllegalArgumentException(" Can't get text form message!");
			String[] parts = text.split(BmDmlEvent.SERPRATOR_CHAR);
			String operation = parts[parts.length-1];
			if(IEventHandler.OPERATIONS.delete.name().equals(operation)){
				delete(message);
			}else if(IEventHandler.OPERATIONS.update.name().equals(operation)){
				update(message);
			}else if(IEventHandler.OPERATIONS.insert.name().equals(operation)){
				insert(message);
			}else if(IEventHandler.OPERATIONS.reload.name().equals(operation)){
				reload(message);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "handle message exception", e);
		}finally{
			provider.writeUnLock();
		}
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}
	
}
