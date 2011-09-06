/*
 * Created on 2011-9-4 上午10:54:09
 * $Id$
 */
package aurora.application.features.cstm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import uncertain.composite.CompositeMap;
import uncertain.core.IGlobalInstance;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.IObjectRegistry;
import aurora.database.DBUtil;
import aurora.database.FetchDescriptor;
import aurora.database.ParsedSql;
import aurora.database.ResultSetLoader;
import aurora.database.SqlRunner;
import aurora.database.rsconsumer.CompositeMapCreator;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.SqlServiceContext;

public class CustomizationDataProvider implements ICustomizationDataProvider, IGlobalInstance {
	IObjectRegistry registry;

	public CustomizationDataProvider(IObjectRegistry registry) {
		this.registry = registry;
	}

	public CompositeMap getCustomizationData(String service_name, CompositeMap context) {
		if (registry == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException((new CompositeMap()).asLocatable(),
					IObjectRegistry.class, CustomizationDataProvider.class.getCanonicalName());
		DatabaseServiceFactory databasefactory = (DatabaseServiceFactory) registry
				.getInstanceOfType(DatabaseServiceFactory.class);
		SqlServiceContext ssc = null;
		PreparedStatement ps_dim_header_sql = null;
		ResultSet rs_header = null;
		ResultSet rs_details = null;
		CompositeMap result = new CompositeMap("result");
		try {
			ssc = databasefactory.createContextWithConnection();
			Connection conn = ssc.getConnection();
			String query_dimensions = "select d.dimension_code,d.data_query_sql from sys_config_dimension d where d.enabled_flag='Y' order by d.order_num";
			String dimenson_sql_template = "select t.record_id,t.head_id,t.source_file,t.dimension_type,t.dimension_value,d.order_num,t.mod_type,"
					+ "t.id_value,t.array_name,t.index_field,t.index_value,t.xpath,t.position,t.config_content,t.attrib_key,t.attrib_value,t.fields_order,t.comments"
					+ " from sys_config_customization t,sys_config_dimension d where t.dimension_type=d.dimension_code and t.enable_flag = 'Y' and t.source_file='"
					+ service_name
					+ "' and t.dimension_type=";
			StringBuffer sb = new StringBuffer("");
			ps_dim_header_sql = conn.prepareStatement(query_dimensions);
			rs_header = ps_dim_header_sql.executeQuery();
			if (rs_header == null)
				return null;
			boolean firstRecord = true;
			while (rs_header.next()) {
				String dimensionCode = rs_header.getString(1);
				String data_query_sql = rs_header.getString(2);
				if (firstRecord)
					firstRecord = false;
				else
					sb.append(" union ");
				sb.append(dimenson_sql_template).append("'").append(dimensionCode).append("'");
				if (data_query_sql != null)
					sb.append(" and ").append(data_query_sql);
			}
			if(!firstRecord)
				sb.append(" order by 6,7,8,9,10 ");
			if ("".equals(sb.toString()))
				return null;
			SqlServiceContext sql_context = SqlServiceContext.createSqlServiceContext(context);
			if(sql_context.getConnection() == null){
				sql_context = SqlServiceContext.createSqlServiceContext(conn);
			}
			ParsedSql stmt = createStatement(sb.toString());
			SqlRunner runner = new SqlRunner(sql_context, stmt);
			rs_details = runner.query(context);
			ResultSetLoader mRsLoader = new ResultSetLoader();
			mRsLoader.setFieldNameCase(Character.LOWERCASE_LETTER);
			FetchDescriptor desc = FetchDescriptor.fetchAll();
			CompositeMapCreator compositeCreator = new CompositeMapCreator(result);
			mRsLoader.loadByResultSet(rs_details, desc, compositeCreator);
			if(result.getChilds() == null)
				return null;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			DBUtil.closeResultSet(rs_header);
			DBUtil.closeStatement(ps_dim_header_sql);
			DBUtil.closeResultSet(rs_details);
			if (ssc != null)
				try {
					ssc.freeConnection();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
		}
		return result;
	}

	ParsedSql createStatement(String sql) {
		ParsedSql stmt = new ParsedSql();
		stmt.parse(sql);
		return stmt;
	}

}
