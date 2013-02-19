package aurora.application.features.msg;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import uncertain.core.ILifeCycle;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.LoggingContext;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectRegistry;

public class AuroraClientInstance extends AbstractLocatableObject implements ILifeCycle,IMessageStub {
	/**
	 * 配置样本
		<msg:Aurora-client-instance xmlns:msg="aurora.application.features.msg">
		
			    <messageHandlers>
			        <msg:DefaultMessageHandler name="refreshPriviledge" procedure="init.load_priviledge_check_data"/>
			        <msg:DefaultMessageHandler name="refreshService" procedure="init.load_system_service"/>
			    </messageHandlers>
				
			    <consumers>
			        <msg:consumer topic="application_foundation">
			            <events>
			                <msg:event handler="refreshPriviledge" message="priviledge_setting_change"/>
			                <msg:event handler="refreshService" message="service_config_change"/>
			            </events>
			        </msg:consumer>
					<msg:DefaultNoticeConsumer topic="dml_event"/>
			    </consumers>
				
		</msg:Aurora-client-instance>
	 * 
	 */
	public static final String PLUGIN = "aurora.application.features.msg";
	private IMessageHandler[] mMessageHandlers;
	private IConsumer[] consumers;
	private String url;
	
	private IObjectRegistry registry;
	private Map<String,IMessageHandler> handlersMap = new HashMap<String,IMessageHandler>();
	private IMessageDispatcher messageDispatcher;
	private Map<String,IConsumer> consumerMap;
	private int status = STOP_STATUS;
	private Logger logger;
	
	private Thread initConsumersThread;
	
	public AuroraClientInstance(IObjectRegistry registry) {
		this.registry = registry;
		messageDispatcher = new MessageDispatcher(registry);
	}
	
	public boolean startup() {
		if(status == STARTING_STATUS || status == STARTED_STATUS)
			return true;
		status = STARTING_STATUS;
		logger = Logger.getLogger(PLUGIN);
		if(url == null){
			BuiltinExceptionFactory.createOneAttributeMissing(this, "url");
		}
		initConsumersThread = new Thread(){
			public void run(){
				if(consumers != null){
					for(int i= 0;i<consumers.length;i++){
						try {
							consumers[i].init(AuroraClientInstance.this);
						} catch (Exception e) {
							logger.log(Level.SEVERE,"init jms consumers failed!",e);
							throw new RuntimeException(e);
						}
					}
				}
				status = STARTED_STATUS;
				LoggingContext.getLogger(PLUGIN, registry).log(Level.INFO,"start jms client successful!");
			}
		};
		initConsumersThread.start();
		
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				try {
					onShutdown();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		registry.registerInstance(IMessageStub.class, this);
		return true;
	}
	public void onShutdown() throws Exception{
		if(initConsumersThread != null)
			initConsumersThread.interrupt();
		if(consumers != null){
			for(int i= 0;i<consumers.length;i++){
				consumers[i].onShutdown();
			}
		}
	}
	public IMessageHandler getMessageHandler(String name){
		return (IMessageHandler)handlersMap.get(name);
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public IMessageHandler[] getMessageHandlers() {
		return mMessageHandlers;
	}
	public void setMessageHandlers(IMessageHandler[] messageHandlers) {
		this.mMessageHandlers = messageHandlers;
		for(int i= 0;i<messageHandlers.length;i++){
			handlersMap.put(messageHandlers[i].getName(), messageHandlers[i]);
		}
	}
	public IConsumer[] getConsumers() {
		return consumers;
	}
	public void setConsumers(IConsumer[] consumers) {
		this.consumers = consumers;
		if(consumers != null){
			consumerMap = new HashMap<String,IConsumer>();
			if(consumers != null){
				for(int i= 0;i<consumers.length;i++){
					consumerMap.put(consumers[i].getTopic(), consumers[i]);
				}
			}
		}
	}
	public IConsumer getConsumer(String topic) {
		return consumerMap.get(topic);
	}
	
	public void shutdown() {
		try {
			onShutdown();
		} catch (Exception e) {
			logger.log(Level.SEVERE,"shutdown aurora message instance failed!",e);
		}
	}

	public IMessageDispatcher getDispatcher() {
		return messageDispatcher;
	}

	@Override
	public boolean isStarted() {
		return status == STARTED_STATUS;
	}
}
