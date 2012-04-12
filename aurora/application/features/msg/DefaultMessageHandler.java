package aurora.application.features.msg;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import uncertain.composite.CompositeMap;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.IProcedureManager;
import uncertain.proc.Procedure;
import aurora.service.IServiceFactory;
import aurora.service.ServiceInvoker;

public class DefaultMessageHandler extends AbstractLocatableObject implements
		IMessageHandler {

	private IObjectRegistry registry;
	
	private String name;
	private String procedure;
	
	private IProcedureManager procedureManager;
	private IServiceFactory serviceFactory;

	public DefaultMessageHandler(IObjectRegistry registry) {
		this.registry = registry;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;

	}

	@SuppressWarnings("unchecked")
	public void onMessage(IMessage message) {
		ILogger logger = LoggingContext.getLogger(this.getClass().getPackage().getName(), registry);

		logger.log(Level.INFO, "accepted a new message！");

		if (procedureManager == null) {
			this.procedureManager = (IProcedureManager) registry
					.getInstanceOfType(IProcedureManager.class);
			if (procedureManager == null)
				throw BuiltinExceptionFactory.createInstanceNotFoundException(
						this, IProcedureManager.class, this.getClass()
								.getName());

			this.serviceFactory = (IServiceFactory) registry
					.getInstanceOfType(IServiceFactory.class);
			if (serviceFactory == null)
				throw BuiltinExceptionFactory.createInstanceNotFoundException(
						this, IServiceFactory.class, this.getClass().getName());
		}

		if (procedure == null) {
			throw BuiltinExceptionFactory.createAttributeMissing(this,
					"procedure");
		}

		if (message == null) {
			logger.log(Level.WARNING, "message is null");
			return;
		}
			CompositeMap context = new CompositeMap();

			try {
				CompositeMap properties = message.getProperties();
				if(properties != null && !properties.isEmpty()){
					Set entrySet = properties.entrySet();
					for(Iterator<Entry> it = entrySet.iterator();it.hasNext();){
						Entry entry = it.next();
						// 将解析后的部分放入service的数据容器
						context.putObject("/parameter/message/@" + entry.getKey(),entry.getValue(), true);
					}
				}
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error when handle message properties ", e);
			}
			

			try {
				logger.log(Level.CONFIG, "receive message text:{0}",
						new Object[] { message.getText() });
				logger.log(Level.CONFIG, "load procedure:{0}",
						new Object[] { procedure });
				Procedure proc = null;
				try {
					proc = procedureManager.loadProcedure(procedure);
				} catch (Exception ex) {
					throw BuiltinExceptionFactory.createResourceLoadException(
							this, procedure, ex);
				}
				String name = "MSG." + procedure;
				ServiceInvoker.invokeProcedureWithTransaction(name, proc,
						serviceFactory, context);

			} catch (Exception ex) {
				logger.log(Level.SEVERE, "Error when invoking procedure "
						+ procedure, ex);
			} finally {
				context.clear();
			}
	}

	public String getProcedure() {
		return procedure;
	}

	public void setProcedure(String procedure) {
		this.procedure = procedure;
	}
}
