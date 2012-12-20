/*
 * Created on 2008-5-6
 */
package aurora.database.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.sql.DataSource;

import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;
import uncertain.event.Configuration;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.IProcedureManager;
import uncertain.proc.Procedure;
import uncertain.proc.ProcedureRunner;
import aurora.bm.AbstractSqlCreator;
import aurora.bm.BusinessModel;
import aurora.bm.DeleteSqlCreator;
import aurora.bm.IModelFactory;
import aurora.bm.InsertSqlCreator;
import aurora.bm.ModelFactory;
import aurora.bm.QuerySqlCreator;
import aurora.bm.UpdateSqlCreator;
import aurora.database.DatabaseConstant;
import aurora.database.features.AutoQueryCounter;
import aurora.database.features.CacheBasedLookUpField;
import aurora.database.features.LookUpField;
import aurora.database.features.OrderByClauseCreator;
import aurora.database.features.WhereClauseCreator;
import aurora.database.profile.IDatabaseFactory;
import aurora.events.E_PrepareBusinessModel;

public class DatabaseServiceFactory extends AbstractLocatableObject implements IDatabaseServiceFactory {

	UncertainEngine uncertainEngine;
	IModelFactory modelFactory;
	DataSource dataSource;
	IDatabaseFactory databaseFactory;
	IProcedureManager mProcedureManager;

	// Class -> Default participant instance
	Map defaultParticipantsMap = new HashMap();
	Configuration globalConfig;

	public static ILogger getLogger(CompositeMap context) {
		ILogger logger = LoggingContext.getLogger(context, DatabaseConstant.AURORA_DATABASE_LOGGING_TOPIC);
		return logger;
	}

	public DatabaseServiceFactory(UncertainEngine engine) {
		this.uncertainEngine = engine;
		init();
	}

	protected void addDefaultParticipants() {
		QuerySqlCreator query_creator = new QuerySqlCreator(getModelFactory(), getDatabaseFactory());
		setGlobalParticipant(QuerySqlCreator.class, query_creator);

		UpdateSqlCreator update_creator = new UpdateSqlCreator(getModelFactory(), getDatabaseFactory());
		setGlobalParticipant(UpdateSqlCreator.class, update_creator);

		InsertSqlCreator insert_creator = new InsertSqlCreator(getModelFactory(), getDatabaseFactory());
		setGlobalParticipant(InsertSqlCreator.class, insert_creator);

		WhereClauseCreator where_creator = new WhereClauseCreator(getDatabaseFactory());
		setGlobalParticipant(WhereClauseCreator.class, where_creator);

		AutoQueryCounter auto_query_counter = new AutoQueryCounter(getDatabaseFactory());
		setGlobalParticipant(AutoQueryCounter.class, auto_query_counter);

		DeleteSqlCreator delete_creator = new DeleteSqlCreator(getModelFactory(), getDatabaseFactory());
		setGlobalParticipant(DeleteSqlCreator.class, delete_creator);

		
//		LookUpField lookupfiled = new LookUpField(databaseFactory, uncertainEngine.getObjectRegistry());
//		setGlobalParticipant(LookUpField.class, lookupfiled);

		setGlobalParticipant(OrderByClauseCreator.class, new OrderByClauseCreator());
	}

	protected void init() {

		ILogger logger = uncertainEngine.getLogger("aurora.database");

		mProcedureManager = uncertainEngine.getProcedureManager();
		globalConfig = uncertainEngine.createConfig();
		IObjectRegistry os = uncertainEngine.getObjectRegistry();
		modelFactory = (IModelFactory) os.getInstanceOfType(IModelFactory.class);
		if (modelFactory == null) {
			ModelFactory fact = new ModelFactory(uncertainEngine.getOcManager());

			modelFactory = fact;
			os.registerInstance(IModelFactory.class, modelFactory);
		}

		dataSource = (DataSource) os.getInstanceOfType(DataSource.class);
		if (dataSource != null) {
			logger.info("Using datasource " + dataSource);
		} else {
			throw BuiltinExceptionFactory.createInstanceNotFoundException(this, DataSource.class);
		}

		IDatabaseFactory fact = (IDatabaseFactory) os.getInstanceOfType(IDatabaseFactory.class);
		if (fact != null)
			setDatabaseFactory(fact);
		else {
			throw BuiltinExceptionFactory.createInstanceNotFoundException(this, IDatabaseFactory.class);
		}

	}

	/*
	 * public void onInitialize(){ IObjectRegistry os =
	 * uncertainEngine.getObjectRegistry(); ModelFactory fact =
	 * (ModelFactory)modelFactory; ICache cache =
	 * CacheFactoryConfig.getNamedCache(os, "BusinessModel"); if(cache!=null){
	 * fact.setUseCache(true); fact.setCache(cache); } }
	 */

	public Object getGlobalParticipant(Class type) {
		return defaultParticipantsMap.get(type);
	}

	public void setGlobalParticipant(Class type, Object instance) {
		if (!defaultParticipantsMap.containsKey(type)) {
			defaultParticipantsMap.put(type, instance);
			globalConfig.addParticipant(instance);
		}
	}

	public void setGlobalParticipant(Object instance) {
		setGlobalParticipant(instance.getClass(), instance);
	}

	public SqlServiceContext createContextWithConnection() throws SQLException {
		SqlServiceContext context = createContext();
		Connection conn = null;
		conn = dataSource.getConnection();
		context.setConnection(conn);
		return context;
	}

	public SqlServiceContext createContext() {
		CompositeMap map = new CompositeMap("sql-service");
		return createContext(map);
	}

	public SqlServiceContext createContext(CompositeMap map) {
		SqlServiceContext context = SqlServiceContext.createSqlServiceContext(map);
		return context;
	}

	public ProcedureRunner loadProcedure(String class_path, CompositeMap context) {
		ProcedureRunner runner = new ProcedureRunner();
		Procedure proc = mProcedureManager.loadProcedure(class_path);
		if (proc == null)
			throw new IllegalArgumentException("Can't load procedure " + class_path);
		runner.setProcedure(proc);
		runner.setContext(context);
		return runner;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public IModelFactory getModelFactory() {
		return modelFactory;
	}

	public void setModelFactory(IModelFactory factory) {
		this.modelFactory = factory;

	}

	public void updateSqlCreator(IModelFactory factory) {
		AbstractSqlCreator instance = (AbstractSqlCreator) getGlobalParticipant(QuerySqlCreator.class);
		if (instance != null) {
			instance.setModelFactory(factory);
		}
		instance = (AbstractSqlCreator) getGlobalParticipant(UpdateSqlCreator.class);
		if (instance != null) {
			instance.setModelFactory(factory);
		}
		instance = (AbstractSqlCreator) getGlobalParticipant(DeleteSqlCreator.class);
		if (instance != null) {
			instance.setModelFactory(factory);
		}
		instance = (AbstractSqlCreator) getGlobalParticipant(InsertSqlCreator.class);
		if (instance != null) {
			instance.setModelFactory(factory);
		}
	}

	public BusinessModelService getModelService(String name) throws IOException {
		CompositeMap map = new CompositeMap("model-service-context");
		BusinessModelService bms = getModelService(name, map);
		return bms;
	}

	protected void prepareConfig(Configuration config) {
		Iterator it = defaultParticipantsMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			config.addParticipant(entry.getValue());
		}
	}

	public BusinessModelService getModelService(BusinessModel model, CompositeMap context_map) throws IOException {
		Configuration config = uncertainEngine.createConfig();
		prepareConfig(config);
		config.loadConfig(model.getObjectContext());
		BusinessModelService service = null;
		try {
			service = new BusinessModelService(this, config, model, context_map);
			config.addParticipant(service);
			config.fireEvent(E_PrepareBusinessModel.EVENT_NAME, new Object[] { model, context_map });
		} catch (Exception ex) {
			throw new RuntimeException("Error when creating business model service " + model.getName(), ex);
		}
		return service;
	}

	public BusinessModelService getModelService(CompositeMap bm_config, CompositeMap context_map) throws IOException {
		BusinessModel model = modelFactory.getModel(bm_config);
		return getModelService(model, context_map);
	}

	public BusinessModelService getModelService(String name, CompositeMap context_map) throws IOException {
		if (modelFactory == null)
			throw new IllegalStateException("ModelFactory must be set first");
		BusinessModel model = modelFactory.getModel(name);
		if (model == null)
			throw new IllegalArgumentException("Can't load business model " + name);
		return getModelService(model, context_map);
	}

	public IDatabaseFactory getDatabaseFactory() {
		return databaseFactory;
	}

	public void setDatabaseFactory(IDatabaseFactory databaseFactory) {
		this.databaseFactory = databaseFactory;
		addDefaultParticipants();
		// ParticipantManager pm = databaseFactory.getParticipantManager();
	}

	public IObjectRegistry getObjectRegistry() {
		return uncertainEngine == null ? null : uncertainEngine.getObjectRegistry();
	}

}
