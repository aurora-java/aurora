package aurora.application.features.cstm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import uncertain.composite.CompositeMap;
import uncertain.composite.XMLOutputter;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.ILogger;
import uncertain.ocm.IObjectRegistry;
import aurora.application.AuroraApplication;
import aurora.application.sourcecode.SourceCodeUtil;
import aurora.database.DBUtil;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.database.service.SqlServiceContext;

public class ConfigCustomizationUtil {

	public static void formConfigConvertToCust(IObjectRegistry registry, String filePath, Long form_field_id) throws Exception {
		if (filePath == null)
			throw new IllegalArgumentException("parameter filePath can not be null");
		if (form_field_id < 1)
			throw new IllegalArgumentException("parameter form_field_id can not be null");
		CompositeMap fileContent = CustomSourceCode.getFileContent(registry, filePath);
		IDatabaseServiceFactory factory = (IDatabaseServiceFactory) registry.getInstanceOfType(IDatabaseServiceFactory.class);
		if (factory == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(null, IDatabaseServiceFactory.class,
					CustomSourceCode.class.getCanonicalName());
		DataSource dataSource = (DataSource) registry.getInstanceOfType(DataSource.class);
		if (dataSource == null)
			throw BuiltinExceptionFactory
					.createInstanceNotFoundException(null, DataSource.class, CustomSourceCode.class.getCanonicalName());
		// 检查事务一致性
		ILogger logger = CustomSourceCode.getLogger(registry);
		StringBuffer config_sql = new StringBuffer(
				"select s.service_name,h.dimension_type,h.dimension_value,t.cmp_id,t.name,t.prompt,"
						+ "t.editabled_flag,t.enabled_flag,t.required_flag,t.bm,t.field_id,t.row_num,t.column_num"
						+ "  from sys_dynamic_forms t, sys_dynamic_headers h,sys_service s where h.header_id = t.header_id and s.service_id = h.service_id and t.form_id = "
						+ form_field_id);
		StringBuffer delete_cust_sql = new StringBuffer("delete from sys_config_customization t where t.form_field_id = " + form_field_id);
		// 删除以前的动态配置记录
		sqlExecute(factory, delete_cust_sql.toString());
		// 新增本次记录
		CompositeMap result = CustomSourceCode.sqlQuery(dataSource, config_sql.toString());
		if (result != null) {
			List<CompositeMap> childList = result.getChilds();
			if (childList != null) {
				for (CompositeMap record : childList) {
					int flex_field_id = record.getInt("field_id", -1);
					String prompt = record.getString("prompt");
					String service_name = record.getString("service_name");
					String dimension_type = record.getString("dimension_type");
					String dimension_value = record.getString("dimension_value");
					String cmp_id = record.getString("cmp_id");
					String editabled_flag = record.getString("editabled_flag");
					String read_only = "Y".equals(editabled_flag) ? "false" : "true";
					String enabled_flag = record.getString("enabled_flag");
					String required_flag = record.getString("required_flag");
					String required = "Y".equals(required_flag) ? "true" : "false";
					String bm = record.getString("bm");
					String row_num = record.getString("row_num");
					String column_num = record.getString("column_num");
					// 是否弹性域字段
					if (flex_field_id < 0) {
						CompositeMap editor = SourceCodeUtil.searchNodeById(fileContent, cmp_id);
						if (editor == null)
							throw BuiltinExceptionFactory.createUnknownNodeWithName(fileContent.asLocatable(), "editor", "id", cmp_id);
						if (!"Y".equals(enabled_flag)) {
							deleteNode(factory, service_name, dimension_type, dimension_value, cmp_id, form_field_id);
							continue;
						}
						if (isNotNULL(prompt)) {
							setElementAttribute(factory, service_name, dimension_type, dimension_value, cmp_id, "prompt", prompt,
									form_field_id);
						}
						String dataSetID = editor.getString("bindtarget");
						String editorName = editor.getString("name");
						if (dataSetID == null || "".equals(dataSetID))
							throw BuiltinExceptionFactory.createAttributeMissing(editor.asLocatable(), "bindtarget");
						CompositeMap dataSet = SourceCodeUtil.searchNodeById(fileContent, dataSetID);
						if (dataSet == null)
							throw BuiltinExceptionFactory.createUnknownNodeWithName(fileContent.asLocatable(), "dataSet", "id", dataSetID);

						CompositeMap fields = dataSet.getChild("fields");
						if (fields == null) {
							fields = dataSet.createChild("a", AuroraApplication.AURORA_FRAMEWORK_NAMESPACE, "fields");
						}
						if (isNotNULL(enabled_flag) || isNotNULL(required_flag) || isNotNULL(bm)) {
							CompositeMap fieldNode = fields.getChildByAttrib("name", editorName);
							if (fieldNode != null) {
								if (isNotNULL(enabled_flag)) {
									setArrayElementAttribute(factory, service_name, dimension_type, dimension_value, dataSetID, "fields",
											"name", editorName, "readonly", read_only, form_field_id);
								}
								if (isNotNULL(required_flag)) {
									setArrayElementAttribute(factory, service_name, dimension_type, dimension_value, dataSetID, "fields",
											"name", editorName, "required", required, form_field_id);
								}
								if (isNotNULL(bm)) {
									setArrayElementAttribute(factory, service_name, dimension_type, dimension_value, dataSetID, "fields",
											"name", editorName, "model", bm, form_field_id);
								}
							} else {
								CompositeMap newField = new CompositeMap("a", AuroraApplication.AURORA_FRAMEWORK_NAMESPACE, "field");
								newField.put("name", editorName);

								if (isNotNULL(enabled_flag)) {
									newField.put("readonly", read_only);
								}
								if (isNotNULL(required_flag)) {
									newField.put("required", required);
								}
								if (isNotNULL(bm)) {
									newField.put("model", bm);
								}
								String newFieldContent = XMLOutputter.defaultInstance().toXML(newField, false);
								addArrayElement(factory, service_name, dimension_type, dimension_value, dataSetID, "fields", "last_child",
										newFieldContent, form_field_id);
							}
						}
					} else {

					}
				}
			}
		}
	}

	private static void setElementAttribute(IDatabaseServiceFactory databasefactory, String source_file, String dimension_type,
			String dimension_value, String id_value, String attrib_key, String attrib_value, Long form_field_id) throws SQLException {
		String insertSql = "insert into sys_config_customization(record_id,enable_flag,source_file,dimension_type,dimension_value,id_value,"
				+ "mod_type,attrib_key,attrib_value,comments,created_by,creation_date,last_updated_by,last_update_date,form_field_id)values"
				+ "(sys_config_customization_s.nextval,'Y',?,?,?,?,'set_attrib',?,?,'dynamic',0,sysdate,0,sysdate,?)";
		SqlServiceContext ssc = null;
		PreparedStatement st = null;
		try {
			ssc = databasefactory.createContextWithConnection();
			Connection conn = ssc.getConnection();
			st = conn.prepareStatement(insertSql);
			int i = 1;
			st.setString(i++, source_file);
			st.setString(i++, dimension_type);
			st.setString(i++, dimension_value);
			st.setString(i++, id_value);
			st.setString(i++, attrib_key);
			st.setString(i++, attrib_value);
			st.setInt(i++, form_field_id.intValue());
			st.execute();
		} finally {
			DBUtil.closeStatement(st);
			if (ssc != null)
				ssc.freeConnection();
		}
	}

	private static void setArrayElementAttribute(IDatabaseServiceFactory databasefactory, String source_file, String dimension_type,
			String dimension_value, String id_value, String array_name, String index_field, String index_value, String attrib_key,
			String attrib_value, long form_field_id) throws SQLException {
		String insertSql = "insert into sys_config_customization(record_id,enable_flag,source_file,dimension_type,dimension_value,id_value,"
				+ "mod_type,array_name,index_field,index_value,attrib_key,attrib_value,comments,created_by,creation_date,last_updated_by,last_update_date,form_field_id)values"
				+ "(sys_config_customization_s.nextval,'Y',?,?,?,?,'set_attrib',?,?,?,?,?,'dynamic',0,sysdate,0,sysdate,?)";
		SqlServiceContext ssc = null;
		PreparedStatement st = null;
		try {
			ssc = databasefactory.createContextWithConnection();
			Connection conn = ssc.getConnection();
			st = conn.prepareStatement(insertSql);
			int i = 1;
			st.setString(i++, source_file);
			st.setString(i++, dimension_type);
			st.setString(i++, dimension_value);
			st.setString(i++, id_value);
			st.setString(i++, array_name);
			st.setString(i++, index_field);
			st.setString(i++, index_value);
			st.setString(i++, attrib_key);
			st.setString(i++, attrib_value);
			st.setLong(i++, form_field_id);
			st.execute();
		} finally {
			DBUtil.closeStatement(st);
			if (ssc != null)
				ssc.freeConnection();
		}
	}

	private static void addArrayElement(IDatabaseServiceFactory databasefactory, String source_file, String dimension_type,
			String dimension_value, String id_value, String array_name, String position, String config_content, long form_field_id)
			throws SQLException {
		String insertSql = "insert into sys_config_customization(record_id,enable_flag,source_file,dimension_type,dimension_value,id_value,"
				+ "mod_type,array_name,position,config_content,comments,created_by,creation_date,last_updated_by,last_update_date,form_field_id)values"
				+ "(sys_config_customization_s.nextval,'Y',?,?,?,?,'insert',?,?,?,'dynamic',0,sysdate,0,sysdate,?)";
		SqlServiceContext ssc = null;
		PreparedStatement st = null;
		try {
			ssc = databasefactory.createContextWithConnection();
			Connection conn = ssc.getConnection();
			st = conn.prepareStatement(insertSql);
			int i = 1;
			st.setString(i++, source_file);
			st.setString(i++, dimension_type);
			st.setString(i++, dimension_value);
			st.setString(i++, id_value);
			st.setString(i++, array_name);
			st.setString(i++, position);
			st.setString(i++, config_content);
			st.setLong(i++, form_field_id);
			st.execute();
		} finally {
			DBUtil.closeStatement(st);
			if (ssc != null)
				ssc.freeConnection();
		}
	}

	private static void deleteNode(IDatabaseServiceFactory databasefactory, String source_file, String dimension_type,
			String dimension_value, String id_value, long form_field_id) throws SQLException {
		String insertSql = "insert into sys_config_customization(record_id,enable_flag,source_file,dimension_type,dimension_value,id_value,"
				+ "mod_type,comments,created_by,creation_date,last_updated_by,last_update_date,form_field_id)values"
				+ "(sys_config_customization_s.nextval,'Y',?,?,?,?,'delete','dynamic',0,sysdate,0,sysdate,?)";
		SqlServiceContext ssc = null;
		PreparedStatement st = null;
		try {
			ssc = databasefactory.createContextWithConnection();
			Connection conn = ssc.getConnection();
			st = conn.prepareStatement(insertSql);
			int i = 1;
			st.setString(i++, source_file);
			st.setString(i++, dimension_type);
			st.setString(i++, dimension_value);
			st.setString(i++, id_value);
			st.setLong(i++, form_field_id);
			st.execute();
		} finally {
			DBUtil.closeStatement(st);
			if (ssc != null)
				ssc.freeConnection();
		}
	}

	private static boolean isNotNULL(String content) {
		if (content == null)
			return false;
		if ("".equals(content) || "null".equals(content))
			return false;
		return true;
	}

	private static void sqlExecute(IDatabaseServiceFactory databasefactory, String sql) throws SQLException {
		SqlServiceContext ssc = databasefactory.createContextWithConnection();
		Connection conn = ssc.getConnection();
		Statement st = conn.createStatement();
		try {
			st.execute(sql);
		} finally {
			DBUtil.closeStatement(st);
			if (ssc != null)
				ssc.freeConnection();
		}
	}
}
