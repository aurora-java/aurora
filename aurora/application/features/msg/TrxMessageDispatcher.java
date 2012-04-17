package aurora.application.features.msg;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import uncertain.composite.CompositeMap;
import uncertain.event.EventModel;
import uncertain.event.RuntimeContext;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.IObjectRegistry;
import aurora.events.E_TransactionCommit;
import aurora.events.E_TransactionRollBack;
import aurora.service.ServiceThreadLocal;

public class TrxMessageDispatcher implements E_TransactionCommit,E_TransactionRollBack,IMessageDispatcher{
	
	protected IObjectRegistry mRegistry;
	
	protected String topic;
	
	private Queue<MessageEntity> msgQueue = new ConcurrentLinkedQueue<MessageEntity>();
	
	public TrxMessageDispatcher(IObjectRegistry registry) {
		this.mRegistry = registry;
		CompositeMap context = ServiceThreadLocal.getCurrentThreadContext();
		RuntimeContext.getInstance(context).setInstanceOfType(IMessageDispatcher.class, this);
	}
	protected IMessageDispatcher createMessageDispatcher(){
		 IMessageStub messageStub = (IMessageStub)mRegistry.getInstanceOfType(IMessageStub.class);
	        if(messageStub == null)
	        	throw BuiltinExceptionFactory.createInstanceNotFoundException(null, IMessageStub.class, this.getClass().getCanonicalName());
	     return messageStub.getDispatcher();
	}

	public int onTransactionRollBack() {
		msgQueue.clear();
		return EventModel.HANDLE_NORMAL;
		
	}

	public int onTransactionCommit() {
		if (msgQueue == null || msgQueue.isEmpty())
			return EventModel.HANDLE_NORMAL;
		IMessageDispatcher dispatcher = createMessageDispatcher();
		for (MessageEntity me : msgQueue) {
			try {
				dispatcher.send(me.getTopic(),me.getMessage(), me.getContext());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		msgQueue.clear();
		return EventModel.HANDLE_NORMAL;
	}


	@Override
	public void send(String topic,IMessage message, CompositeMap context) throws Exception {
		MessageEntity me = new MessageEntity(topic, message, context);
		msgQueue.add(me);
	}
	class MessageEntity {
		String topic;
		IMessage message;
		CompositeMap context;
		
		public MessageEntity(String topic, IMessage message, CompositeMap context) {
			super();
			this.topic = topic;
			this.message = message;
			this.context = context;
		}
		
		public String getTopic() {
			return topic;
		}
		public void setTopic(String topic) {
			this.topic = topic;
		}
		public IMessage getMessage() {
			return message;
		}
		public void setMessage(IMessage message) {
			this.message = message;
		}
		public CompositeMap getContext() {
			return context;
		}
		public void setContext(CompositeMap context) {
			this.context = context;
		}
		
	}
	
}
