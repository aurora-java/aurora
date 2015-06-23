package aurora.database.rsconsumer;

import java.util.List;
import java.util.logging.Level;

import uncertain.composite.CompositeMap;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.IProcedureManager;
import uncertain.proc.Procedure;
import uncertain.proc.ProcedureConfigManager;
import uncertain.proc.ProcedureRunner;
import aurora.database.IResultSetConsumer;
import aurora.database.service.SqlServiceContext;
import aurora.service.ServiceThreadLocal;

public class ModelActionConsumerWrapper extends AbstractLocatableObject implements IResultSetConsumer{

	protected CompositeMap        currentRecord;
	
	private CompositeMap proc_config;
	
	
	private ProcedureRunner runner;
	private ILogger logger;
	private SqlServiceContext svcContext;
	private CompositeMap old_current_param;
	CompositeMap context;
	private boolean useTransactionManager;
	
	private IProcedureManager procedureManager;
	private long rownum;
	
	public ModelActionConsumerWrapper(IProcedureManager procedureManager,IObjectRegistry registry){
		this.procedureManager = procedureManager;
		logger = LoggingContext.getLogger(this.getClass().getCanonicalName(), registry);
	}
	@Override
	public void begin(String root_name) {
		rownum = 0;
		logger.info("begin execute");
		try{
			context = ServiceThreadLocal.getCurrentThreadContext();
			if(context == null)
				context = new CompositeMap();
			runner = createProcedureRunner(context,proc_config);
			runner.setSaveStackTrace(true);
			svcContext = SqlServiceContext.createSqlServiceContext(context);        
			old_current_param = svcContext.getCurrentParameter();
		}
		catch(Exception e){
			logger.log(Level.SEVERE,"",e);
		}
	}

	@Override
	public void newRow(String row_name) {
		rownum++;
		currentRecord = new CompositeMap(row_name);
	}

	@Override
	public void loadField(String name, Object value) {
		currentRecord.put(name, value);
	}

	@Override
	public void endRow() {
		try {
			logger.config("rownum :"+rownum+" currentRecord:"+currentRecord.toXML());
			runModelAction(currentRecord);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void end() {
		svcContext.setCurrentParameter(old_current_param);
		logger.info("finished execute,count :"+rownum);
	}

	@Override
	public void setRecordCount(long count) {
		logger.config("count :"+count);
	}

	@Override
	public Object getResult() {
		return null;
	}
	public void runModelAction(CompositeMap record) throws Exception{
        svcContext.setCurrentParameter(record);
        runner.setSaveStackTrace(true);
		runner.run();
		runner.checkAndThrow();
	}
	public ProcedureRunner createProcedureRunner(CompositeMap context,CompositeMap proc_config) {
		ProcedureRunner runner = new ProcedureRunner();
		runner.setContext(context);
		Procedure proc = null;
		proc = procedureManager.createProcedure(proc_config);
		runner.setProcedure(proc);
		return runner;
	}
	public void beginConfigure(CompositeMap config) {
		 @SuppressWarnings("unchecked")
		List<CompositeMap> childs = config.getChilds();
		 if(childs == null || childs.isEmpty())
			 throw BuiltinExceptionFactory.createNodeMissing(this, "ModelAction");
		 proc_config = ProcedureConfigManager.createConfigNode("procedure");
		 proc_config.addChilds(config.getChilds());
		 logger.config("proc_config:"+proc_config.toXML());
	}
	public boolean getUseTransactionManager() {
		return useTransactionManager;
	}
	public void setUseTransactionManager(boolean useTransactionManager) {
		this.useTransactionManager = useTransactionManager;
		ServiceThreadLocal.setUseTransactionManager(Boolean.valueOf(useTransactionManager));
	}
}
