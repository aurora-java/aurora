package aurora.application.features.cache;

import java.util.logging.Level;

import aurora.application.features.msg.IConsumer;
import aurora.application.features.msg.IMessage;
import aurora.application.features.msg.IMessageStub;
import aurora.application.features.msg.INoticerConsumer;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectRegistry;

public class FullReloadHandler extends AbstractLocatableObject implements IEventHandler {

	private String topic;
	private String event;
	private ICacheProvider provider;

	private ILogger logger;

	@Override
	public void onMessage(IMessage message) {
		try {
			provider.reload();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "reload failed!", e);
		}

	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	@Override
	public void init(ICacheProvider provider, IObjectRegistry registry) {
		this.provider = provider;
		IMessageStub stub = (IMessageStub) registry.getInstanceOfType(IMessageStub.class);
		if (stub == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(this, IMessageStub.class, this.getClass().getName());
		IConsumer consumer = stub.getConsumer(topic);
		if (!(consumer instanceof INoticerConsumer))
			throw BuiltinExceptionFactory.createInstanceTypeWrongException(this.getOriginSource(), INoticerConsumer.class, IConsumer.class);
		((INoticerConsumer) consumer).addListener(event, this);
		logger = LoggingContext.getLogger(this.getClass().getPackage().getName(), registry);
	}

	@Override
	public void setTopic(String topic) {
		this.topic = topic;
	}

	@Override
	public String getTopic() {
		return topic;
	}

}
