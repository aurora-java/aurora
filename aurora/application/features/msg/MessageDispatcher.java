package aurora.application.features.msg;

import uncertain.composite.CompositeMap;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.IObjectRegistry;

public class MessageDispatcher implements IMessageDispatcher{

	protected IObjectRegistry mRegistry;
	
	protected String topic;
	
	public MessageDispatcher(IObjectRegistry registry) {
		this.mRegistry = registry;
	}

	@Override
	public void send(String topic,IMessage message, CompositeMap context) throws Exception {
		 IMessageStub messageStub = (IMessageStub)mRegistry.getInstanceOfType(IMessageStub.class);
        if(messageStub == null)
        	throw BuiltinExceptionFactory.createInstanceNotFoundException(null, IMessageStub.class, this.getClass().getCanonicalName());
        IConsumer consumer = messageStub.getConsumer(topic);
        if(consumer == null)
        	throw new IllegalStateException("Can't get consumer for topic:"+topic+"from MessageStub");
        consumer.onMessage(message);
	}

}
