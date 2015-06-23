/*
 * Created on 2011-9-4 上午10:54:09
 * $Id$
 */
package aurora.application.features.cstm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import javax.sql.DataSource;

import uncertain.composite.CompositeMap;
import uncertain.core.IGlobalInstance;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.IProcedureManager;
import uncertain.proc.Procedure;
import aurora.database.DBUtil;
import aurora.database.FetchDescriptor;
import aurora.database.ParsedSql;
import aurora.database.ResultSetLoader;
import aurora.database.SqlRunner;
import aurora.database.rsconsumer.CompositeMapCreator;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.SqlServiceContext;
import aurora.service.ServiceInstance;

public class CustomizationDataProvider extends AbstractLocatableObject implements ICustomizationDataProvider, IGlobalInstance {

	public static final String DEFAULT_CUSTOM_DATA = "_customization_data";
	public static final String KEY_DIMENSION_INIT_PROC = "dimension_init_proc";
	IObjectRegistry registry;
	CompositeMap dimensions = null;
	boolean    defaultCustomizationEnabled = true;

	public CustomizationDataProvider(IObjectRegistry registry) {
		this.registry = registry;
	}

	// Framework function
	public void onInitialize() throws Exception {
		if (registry == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException((new CompositeMap()).asLocatable(),
					IObjectRegistry.class, CustomizationDataProvider.class.getCanonicalName());
		dimensions = new CompositeMap("result");
		DataSource ds = (DataSource) registry.getInstanceOfType(DataSource.class);
		ResultSet rs_header = null;
		SqlServiceContext sql_context = null;
		try {
			Connection conn = ds.getConnection();
			String query_dimensions = "select d.dimension_code,d.data_query_sql,d.dimension_init_proc,d.dimension_tag from sys_config_dimension d where d.enabled_flag='Y' order by d.order_num";
			sql_context = SqlServiceContext.createSqlServiceContext(conn);
			ParsedSql stmt = createStatement(query_dimensions);
			SqlRunner runner = new SqlRunner(sql_context, stmt);
			rs_header = runner.query(null);
			ResultSetLoader mRsLoader = new ResultSetLoader();
			mRsLoader.setFieldNameCase(Character.LOWERCASE_LETTER);
			FetchDescriptor desc = FetchDescriptor.fetchAll();
			CompositeMapCreator compositeCreator = new CompositeMapCreator(dimensions);
			mRsLoader.loadByResultSet(rs_header, desc, compositeCreator);
		} finally {
			DBUtil.closeResultSet(rs_header);
			if (sql_context != null)
				try {
					sql_context.freeConnection();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
		}
	}

	public CompositeMap getCustomizationData(String service_name, CompositeMap context) {
		if (registry == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException((new CompositeMap()).asLocatable(),
					IObjectRegistry.class, CustomizationDataProvider.class.getCanonicalName());
		if (dimensions == null || dimensions.getChilds() == null)
			return null;

		DatabaseServiceFactory databasefactory = (DatabaseServiceFactory) registry
				.getInstanceOfType(DatabaseServiceFactory.class);
		SqlServiceContext ssc = null;
		ResultSet rs_exists = null;
		ResultSet rs_details = null;
		CompositeMap result = new CompositeMap("result");
		try {
			ssc = databasefactory.createContextWithConnection();
			String exits_sql = "select 1   from dual  where exists  (select 1 from sys_config_customization t where t.source_file = '"+service_name+"' and t.enable_flag='Y')";
			ParsedSql exits_stmt = createStatement(exits_sql);
			SqlRunner exits_runner = new SqlRunner(ssc, exits_stmt);
			rs_exists = exits_runner.query(null);
			if(!rs_exists.next()){
				return null;
			}
			
			ServiceInstance svc = ServiceInstance.getInstance(context);
			String tag = svc.getServiceConfigData().getString("tag");
			if(tag!=null){
			    executeDimensionProc(tag, dimensions, service_name, registry, context);
			}
			String dimenson_sql_template = "select t.record_id,t.head_id,t.source_file,t.dimension_type,t.dimension_value,d.order_num,t.mod_type,"
					+ "t.id_value,t.array_name,t.index_field,t.index_value,t.xpath,t.position,t.config_content,t.attrib_key,t.attrib_value,t.fields_order,t.comments,d.dimension_init_proc"
					+ " from sys_config_customization t,sys_config_dimension d where t.dimension_type=d.dimension_code and t.enable_flag = 'Y' and t.source_file='"
					+ service_name + "' and t.dimension_type=";
			StringBuffer sb = new StringBuffer("");
			boolean firstRecord = true;
			for (Iterator it = dimensions.getChildIterator(); it.hasNext();) {
				CompositeMap record = (CompositeMap) it.next();
				String dimensionCode = record.getString("dimension_code");
				String data_query_sql = record.getString("data_query_sql");
				if (firstRecord)
					firstRecord = false;
				else
					sb.append(" union all ");
				sb.append(dimenson_sql_template).append("'").append(dimensionCode).append("'");
				if (data_query_sql != null)
					sb.append(" and ").append(data_query_sql);
			}
			if (!firstRecord)
				sb.append(" order by 1,6,7,8,9,10 ");
			if ("".equals(sb.toString()))
				return null;
			ParsedSql stmt = createStatement(sb.toString());
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

	ParsedSql createStatement(String sql) {
		ParsedSql stmt = new ParsedSql();
		stmt.parse(sql);
		return stmt;
	}

	public void executeDimensionProc(String service_tag, CompositeMap dimensions, String service_name, IObjectRegistry registry,
			CompositeMap context) throws Exception {
		if (dimensions == null || dimensions.getChilds() == null)
			return;
		if (registry == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(this,	IObjectRegistry.class);
		IProcedureManager procedureManager = (IProcedureManager) registry.getInstanceOfType(IProcedureManager.class);
		if (procedureManager == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(this,	IProcedureManager.class);
		for (Iterator it = dimensions.getChildIterator(); it.hasNext();) {
			CompositeMap dimension = (CompositeMap) it.next();
			String dimension_init_proc = dimension.getString(KEY_DIMENSION_INIT_PROC);
			String tag = dimension.getString("dimension_tag");
			if(tag==null)
			    continue;
			boolean is_tag_match = service_tag.contains(tag);
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

    public boolean getDefaultCustomizationEnabled() {
        return defaultCustomizationEnabled;
    }

    public void setDefaultCustomizationEnabled(boolean defaultCustomizationEnabled) {
        this.defaultCustomizationEnabled = defaultCustomizationEnabled;
    }
}
