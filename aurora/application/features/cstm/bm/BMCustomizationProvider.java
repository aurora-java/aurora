package aurora.application.features.cstm.bm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.logging.Level;

import javax.sql.DataSource;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.core.ILifeCycle;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.IProcedureManager;
import uncertain.proc.Procedure;
import aurora.bm.BusinessModel;
import aurora.database.DBUtil;
import aurora.database.FetchDescriptor;
import aurora.database.ParsedSql;
import aurora.database.ResultSetLoader;
import aurora.database.SqlRunner;
import aurora.database.rsconsumer.CompositeMapCreator;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.database.service.SqlServiceContext;
import aurora.events.E_PrepareBusinessModel;
import aurora.service.ServiceInstance;
import aurora.service.ServiceThreadLocal;

public class BMCustomizationProvider extends AbstractLocatableObject implements E_PrepareBusinessModel, ILifeCycle,
		IBMCustomizationDataProvider {
	public static final String DEFAULT_CUSTOM_DATA = "_customization_data";
	public static final String KEY_DIMENSION_INIT_PROC = "dimension_init_proc";
	public static final String FUNCTION_ID_PATH = "${/cookie/@FUNCTION_ID/@value}";
	public static final String FUNCTION_CODE_PATH = "${/cookie/@FUNCTION_CODE/@value}";
	private IDatabaseServiceFactory databaseServiceFactory;
	private IObjectRegistry objectRegistry;
	private DataSource dataSource;
	ILogger logger;
	CompositeMap custDimensionsRecords = null;
	public BMCustomizationProvider(IDatabaseServiceFactory databaseServiceFactory,IObjectRegistry objectRegistry) {
		this.databaseServiceFactory = databaseServiceFactory;
		this.objectRegistry = objectRegistry;
	}

	@Override
	public void onPrepareBusinessModel(BusinessModel model, CompositeMap context) throws Exception {
		/* This context may be "<model-service-context BusinessModel="aurora.bm.BusinessModel@e73b917"/>",
		 * so need ServiceThreadLocal.getCurrentThreadContext(); 
		 */
		boolean customization_enabled = model.getCustomizationenabled();
		if (customization_enabled) {
			// if not a HTTP request
			CompositeMap fullContext = ServiceThreadLocal.getCurrentThreadContext();
			if (fullContext == null)
				return;
			logger.log(Level.CONFIG, fullContext.getRoot().toXML());
			String function_id = TextParser.parse(FUNCTION_ID_PATH, fullContext);
//			String function_code = TextParser.parse(FUNCTION_CODE_PATH, fullContext);
			// if not called by a Screen.
			if (function_id == null|| "".equals(function_id)) {
				return;
			}
			CompositeMap custDetailRecords  = getCustomizationData(model ,Integer.valueOf(function_id),fullContext);
			 if(custDetailRecords!=null){
				 LovBMCustomSourceCode.custom(model,custDetailRecords,objectRegistry);
			 }
		}
	}

	@Override
	public CompositeMap getCustomizationData(BusinessModel model,int function_id,CompositeMap context) {
		if (custDimensionsRecords == null || custDimensionsRecords.getChilds() == null)
			return null;
		String bm_name = model.getName();
		SqlServiceContext ssc = null;
		ResultSet rs_exists = null;
		ResultSet rs_details = null;
		CompositeMap result = new CompositeMap("result");
		try {
			ssc = databaseServiceFactory.createContextWithConnection();
			String exits_sql = "select 1   from dual  where exists  (select 1 from sys_bm_config_customization t " +
								" where t.bm_code='"+bm_name+"' and t.function_id = '"+function_id+"' and t.enable_flag='Y')";
			ParsedSql exits_stmt = createStatement(exits_sql);
			SqlRunner exits_runner = new SqlRunner(ssc, exits_stmt);
			rs_exists = exits_runner.query(null);
			if(!rs_exists.next()){
				return null;
			}

			String bm_tag = model.getTag();
			if(bm_tag!=null){
			    executeDimensionProc(bm_tag, custDimensionsRecords,context);
			}
			String dimenson_sql_template = "select d.order_num,record_id,head_id,function_id,function_code,bm_code,dimension_type,dimension_value," +
											" bm_select_value,bm_data_source,bm_where_clause,bm_order_by,bm_query_condition"	+ 
					                       " from sys_bm_config_customization t,sys_bm_config_dimension d " +
					                       " where t.dimension_type=d.dimension_code and t.enable_flag = 'Y'" +
					                       " and t.bm_code='"+bm_name+"' and t.function_id = '"+function_id + "' and t.dimension_type=";
			StringBuffer sb = new StringBuffer("");
			boolean firstRecord = true;
			for (Iterator it = custDimensionsRecords.getChildIterator(); it.hasNext();) {
				CompositeMap record = (CompositeMap) it.next();
				String dimensionCode = record.getString("dimension_code");
				String data_query_sql = record.getString("data_query_sql");
				String dimension_tag = record.getString("dimension_tag");
				if(dimension_tag != null){
					if(bm_tag == null)
						continue;
					boolean is_tag_match = bm_tag.contains(dimension_tag);
					if(!is_tag_match)
						continue;
				}
				if (firstRecord)
					firstRecord = false;
				else
					sb.append(" union all ");
				sb.append(dimenson_sql_template).append("'").append(dimensionCode).append("'");
				if (data_query_sql != null)
					sb.append(" and ").append(data_query_sql);
			}
			if (!firstRecord)
				sb.append(" order by 1 ");
			if ("".equals(sb.toString()))
				return null;
			String custDetailRecordsSql = sb.toString();
			logger.config("custDetailRecordsSql:"+custDetailRecordsSql);
			ParsedSql stmt = createStatement(custDetailRecordsSql);
			SqlRunner runner = new SqlRunner(ssc, stmt);
			rs_details = runner.query(context);
			ResultSetLoader mRsLoader = new ResultSetLoader();
			mRsLoader.setFieldNameCase(Character.LOWERCASE_LETTER);
			FetchDescriptor desc = FetchDescriptor.fetchAll();
			CompositeMapCreator compositeCreator = new CompositeMapCreator(result);
			mRsLoader.loadByResultSet(rs_details, desc, compositeCreator);
			if (result.getChilds() == null)
				return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			DBUtil.closeResultSet(rs_exists);
			DBUtil.closeResultSet(rs_details);
			if (ssc != null)
				try {
					ssc.freeConnection();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
		}
		context.putObject(DEFAULT_CUSTOM_DATA, result);
		return result;
	}
	private void executeDimensionProc(String bm_tag, CompositeMap dimensionsRecords,CompositeMap context) throws Exception {
		if (dimensionsRecords == null || dimensionsRecords.getChilds() == null)
			return;
		IProcedureManager procedureManager = (IProcedureManager) objectRegistry.getInstanceOfType(IProcedureManager.class);
		if (procedureManager == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(this,	IProcedureManager.class);
		for (Iterator it = dimensionsRecords.getChildIterator(); it.hasNext();) {
			CompositeMap dimension = (CompositeMap) it.next();
			String dimension_init_proc = dimension.getString(KEY_DIMENSION_INIT_PROC);
			String tag = dimension.getString("dimension_tag");
			if(tag==null)
			    continue;
			boolean is_tag_match = bm_tag.contains(tag);
			if (dimension_init_proc != null && is_tag_match) {
				Procedure proc = null;
				try {
					proc = procedureManager.loadProcedure(dimension_init_proc);
				} catch (Exception ex) {
					throw BuiltinExceptionFactory.createResourceLoadException(context.asLocatable(),
							dimension_init_proc, ex);
				}
				ServiceInstance svc = ServiceInstance.getInstance(context);
				svc.invoke(proc);
			}
		}
	}

	@Override
	public boolean startup() {
		if (!(databaseServiceFactory instanceof DatabaseServiceFactory)) 
			throw BuiltinExceptionFactory.createInstanceNotFoundException(this, DatabaseServiceFactory.class);
		((DatabaseServiceFactory) databaseServiceFactory).setGlobalParticipant(this);
		dataSource = (DataSource) objectRegistry.getInstanceOfType(DataSource.class);
		if(dataSource == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(this, DataSource.class);
		logger = LoggingContext.getLogger(this.getClass().getCanonicalName(), objectRegistry);
		String query_dimensions_sql = " select d.dimension_code,d.data_query_sql,d.dimension_init_proc,d.dimension_tag " +
									  " from sys_bm_config_dimension d where d.enabled_flag='Y' order by d.order_num ";
		logger.config("query_dimensions_sql:"+query_dimensions_sql);
		try {
			custDimensionsRecords = sqlQuery(query_dimensions_sql);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public void shutdown() {

	}

	private CompositeMap sqlQuery(String sql) throws Exception {
		ResultSet resultSet = null;
		SqlServiceContext sql_context = null;
		CompositeMap result = new CompositeMap("result");
		try {
			Connection conn = dataSource.getConnection();
			sql_context = SqlServiceContext.createSqlServiceContext(conn);
			ParsedSql stmt = createStatement(sql);
			SqlRunner runner = new SqlRunner(sql_context, stmt);
			resultSet = runner.query(null);
			ResultSetLoader mRsLoader = new ResultSetLoader();
			mRsLoader.setFieldNameCase(Character.LOWERCASE_LETTER);
			FetchDescriptor desc = FetchDescriptor.fetchAll();
			CompositeMapCreator compositeCreator = new CompositeMapCreator(result);
			mRsLoader.loadByResultSet(resultSet, desc, compositeCreator);
		} finally {
			DBUtil.closeResultSet(resultSet);
			if (sql_context != null)
				sql_context.freeConnection();
		}
		return result;
	}
	ParsedSql createStatement(String sql) {
		ParsedSql stmt = new ParsedSql();
		stmt.parse(sql);
		return stmt;
	}
}
