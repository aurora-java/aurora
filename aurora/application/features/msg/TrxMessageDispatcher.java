package aurora.application.features.msg;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import uncertain.composite.CompositeMap;
import uncertain.core.ILifeCycle;
import uncertain.event.Configuration;
import uncertain.event.EventModel;
import uncertain.event.IParticipantManager;
import uncertain.ocm.IObjectRegistry;
import aurora.events.E_TransactionCommit;
import aurora.events.E_TransactionRollBack;
import aurora.service.IService;
import aurora.service.ServiceContext;
import aurora.service.ServiceInstance;
import aurora.service.http.AbstractFacadeServlet;

public class TrxMessageDispatcher implements E_TransactionCommit,E_TransactionRollBack,IMessageDispatcher{
	
	protected IObjectRegistry mRegistry;
	
	protected String topic;
	
	protected IMessageDispatcher dispatcher;
	
	private ConcurrentHashMap<IService, Queue<IMessage>> serviceMap = new ConcurrentHashMap<IService, Queue<IMessage>>();
	
	public TrxMessageDispatcher(IObjectRegistry registry) {
		this.mRegistry = registry;
		this.dispatcher = createMessageDispatcher();
		startup();
	}
	protected IMessageDispatcher createMessageDispatcher(){
		return new MessageDispatcher(mRegistry);
	}

	public int onTransactionRollBack(IService service) {
		Queue<IMessage> list = serviceMap.get(service);
		if(list == null || list.isEmpty())
			return EventModel.HANDLE_NORMAL;
		else{
			serviceMap.remove(service);
		}
		return EventModel.HANDLE_NORMAL;
		
	}

	public int onTransactionCommit(IService service) {
		Queue<IMessage> list = serviceMap.get(service);
		if (list == null || list.isEmpty())
			return EventModel.HANDLE_NORMAL;
		for (IMessage mo : list) {
			try {
				CompositeMap context = new CompositeMap();
				ServiceContext serviceContext = service.getServiceContext();
				if (serviceContext != null) {
					context = serviceContext.getObjectContext();
				}
				dispatcher.setTopic(topic);
				dispatcher.send(mo, context);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		serviceMap.remove(service);
		return EventModel.HANDLE_NORMAL;
	}
	public boolean startup() {
		IParticipantManager pm = (IParticipantManager)mRegistry.getInstanceOfType(IParticipantManager.class);
		if(pm!=null){
			List commitListenerList = pm.getParticipantList(AbstractFacadeServlet.TRANSATION_COMMIT_CONFIG);
			commitListenerList.add(this);
			List rollbakcListenerList = pm.getParticipantList(AbstractFacadeServlet.TRANSATION_ROLLBACK_CONFIG);
			rollbakcListenerList.add(this);
		}
		return true;
	}
	public void shutdown() {
		if (serviceMap != null)
			serviceMap.clear();
	}
	@Override
	public String getTopic() {
		return topic;
	}
	@Override
	public void setTopic(String topic) {
		this.topic = topic;
	}
	@Override
	public void send(IMessage message, CompositeMap context) throws Exception {
		if(context == null)
			throw new IllegalArgumentException("Context can't be null");
		IService service = ServiceInstance.getInstance(context);
		if(service == null)
			throw new IllegalArgumentException("Can't get Service from context:"+context.toXML());
		Queue<IMessage> queue = serviceMap.get(service);
		if(queue == null){
			queue = new ConcurrentLinkedQueue<IMessage>();
			serviceMap.put(service, queue);
		}
		queue.add(message);
	}
}
