package aurora.application.features.cstm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import uncertain.composite.XMLOutputter;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.ILogger;
import uncertain.ocm.IObjectRegistry;
import aurora.application.AuroraApplication;
import aurora.application.sourcecode.SourceCodeUtil;
import aurora.database.DBUtil;
import aurora.presentation.component.std.IDGenerator;
import aurora.presentation.component.std.config.ComboBoxConfig;
import aurora.presentation.component.std.config.DataSetConfig;
import aurora.presentation.component.std.config.DataSetFieldConfig;
import aurora.presentation.component.std.config.DatePickerConfig;
import aurora.presentation.component.std.config.FieldConfig;
import aurora.presentation.component.std.config.LovConfig;
import aurora.presentation.component.std.config.NumberFieldConfig;
import aurora.presentation.component.std.config.TextFieldConfig;

public class ConfigCustomizationUtil {

	public static void formConfigConvertToCust(IObjectRegistry registry, String filePath, Long form_field_id) throws Exception {
		if (form_field_id < 1)
			throw new IllegalArgumentException("parameter form_field_id can not be null");
		

		ILogger logger = CustomSourceCode.getLogger(registry);
		StringBuffer config_sql = new StringBuffer(
				"select s.service_name,h.dimension_type,h.dimension_value,t.cmp_id,t.name,t.prompt,"
						+ "t.editabled_flag,t.enabled_flag,t.required_flag,t.bm,t.field_id,t.row_num,t.column_num,t.bind_target,t.container_id"
						+ "  from sys_dynamic_forms t, sys_dynamic_headers h,sys_service s where h.header_id = t.header_id and s.service_id = h.service_id and t.form_id = "
						+ form_field_id);
		StringBuffer delete_cust_sql = new StringBuffer("delete from sys_config_customization t where t.form_field_id = " + form_field_id);
		// 删除以前的动态配置记录
		sqlExecute(registry, delete_cust_sql.toString());
		// 新增本次记录
		CompositeMap result = CustomSourceCode.sqlQuery(registry, config_sql.toString());
				
		if (result != null) {
			List<CompositeMap> childList = result.getChilds();
			if (childList != null) {
				for (CompositeMap record : childList) {
					if(filePath == null || "".equals(filePath)) filePath = record.getString("service_name");
					CompositeMap fileContent = CustomSourceCode.getFileContent(registry, filePath);
					
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
					String bindTarget = record.getString("bind_target");
					String containerId = record.getString("container_id");
					// 是否弹性域字段
					if (flex_field_id < 0) {
						CompositeMap editor = SourceCodeUtil.searchNodeById(fileContent, cmp_id);
						if (editor == null)
							throw BuiltinExceptionFactory.createUnknownNodeWithName(fileContent.asLocatable(), "editor", "id", cmp_id);
						if ("N".equals(enabled_flag)) {
							deleteNode(registry, service_name, dimension_type, dimension_value, cmp_id, form_field_id);
							continue;
						}
						if (isNotNULL(prompt)) {
							setElementAttribute(registry, service_name, dimension_type, dimension_value, cmp_id, "prompt", prompt,form_field_id);
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
						if (isNotNULL(editabled_flag) || isNotNULL(required_flag) || isNotNULL(bm)) {
							CompositeMap fieldNode = fields.getChildByAttrib("name", editorName);
							if (fieldNode != null) {
								if (isNotNULL(editabled_flag)) {
									setArrayElementAttribute(registry, service_name, dimension_type, dimension_value, dataSetID, "fields",
											"name", editorName, "readonly", read_only, form_field_id);
								}
								if (isNotNULL(required_flag)) {
									setArrayElementAttribute(registry, service_name, dimension_type, dimension_value, dataSetID, "fields",
											"name", editorName, "required", required, form_field_id);
								}
								if (isNotNULL(bm)) {
									setArrayElementAttribute(registry, service_name, dimension_type, dimension_value, dataSetID, "fields",
											"name", editorName, "model", bm, form_field_id);
								}
							} else {
								CompositeMap newField = new CompositeMap("a", AuroraApplication.AURORA_FRAMEWORK_NAMESPACE, "field");
								newField.put("name", editorName);

								if (isNotNULL(editabled_flag)) {
									newField.put("readonly", read_only);
								}
								if (isNotNULL(required_flag)) {
									newField.put("required", required);
								}
								if (isNotNULL(bm)) {
									newField.put("model", bm);
								}
								String newFieldContent = XMLOutputter.defaultInstance().toXML(newField, false);
								addArrayElement(registry, service_name, dimension_type, dimension_value, dataSetID, "fields", "last_child",
										newFieldContent, form_field_id);
							}
						}
					} else {
						StringBuffer field_sql = new StringBuffer("select ");
						field_sql.append("field_name,field_description,editor_type,width,height,")
								 .append("string_length,string_format,string_case,")
								 .append("number_allowdecimals,number_decimalprecision,number_allownegative,number_allowformat,number_allowpad,")
								 .append("datepicker_format,datepicker_size,")
								 .append("combobox_value_field,combobox_display_field,combobox_mapping,combobox_datasource_type,combobox_datasource_value,")
								 .append("lov_height,lov_height,lov_width,lov_grid_height,lov_title,lov_bm,lov_value_field,lov_display_field,lov_mapping,lov_labelwidth ")
								 .append("from sys_business_object_flexfields where field_id =")
								 .append(flex_field_id);
						CompositeMap fields = CustomSourceCode.sqlQuery(registry, field_sql.toString());
						if(fields!=null && fields.getChilds() != null){
							CompositeMap field = (CompositeMap)fields.getChilds().get(0);
							field.putString("field_description",prompt);
							field.putString("editabled_flag",editabled_flag);
							field.putString("required_flag",required_flag);
							
							String editorType = field.getString("editor_type");
							CompositeMap fieldObject = null;							
							if("TEXTFIELD".equalsIgnoreCase(editorType)){
								fieldObject = createTextField(registry,service_name,dimension_type,dimension_value,form_field_id,bindTarget,field);
							}else if("NUMBERFIELD".equalsIgnoreCase(editorType)){
								fieldObject = createNumberField(registry,service_name,dimension_type,dimension_value,form_field_id,bindTarget,field);
							}else if("DATEPICKER".equalsIgnoreCase(editorType)){
								fieldObject = createDatePicker(registry,service_name,dimension_type,dimension_value,form_field_id,bindTarget,field);
							}else if("COMBOBOX".equalsIgnoreCase(editorType)){
								fieldObject = createComboBox(registry,service_name,dimension_type,dimension_value,form_field_id,bindTarget,field);
							}else if("LOV".equalsIgnoreCase(editorType)){
								fieldObject = createLov(registry,service_name,dimension_type,dimension_value,form_field_id,bindTarget,field);
							}
							
							if(fieldObject != null)
							addArrayElement(registry, service_name, dimension_type, dimension_value, containerId, "", "last_child",fieldObject.toXML(), form_field_id);
						}
					}
				}
			}
		}
	}
	
	private static void initDataSetField(IObjectRegistry registry,DataSetFieldConfig dsfc,String service_name, String dimension_type, String dimension_value, Long form_field_id,String bindTarget,CompositeMap field) throws SQLException{
		String editabled_flag = field.getString("editabled_flag");
		String required_flag = field.getString("required_flag");
		if(isNotNULL(editabled_flag) || isNotNULL(required_flag)){
			if(dsfc == null){
				dsfc = DataSetFieldConfig.getInstance();
				dsfc.setName(field.getString("field_name"));
			}
			dsfc.setReadOnly("N".equalsIgnoreCase(editabled_flag));
			dsfc.setRequired("Y".equalsIgnoreCase(required_flag));
			addArrayElement(registry, service_name, dimension_type, dimension_value, bindTarget, "fields", "last_child", dsfc.getObjectContext().toXML(), form_field_id);
		}
	}
	
	private static void initEditorPropertity(FieldConfig cfg, String bindTarget,CompositeMap field){
		cfg.setName(field.getString("field_name"));
		cfg.setBindTarget(bindTarget);
		cfg.setPrompt(field.getString("field_description"));
		
		Integer width = field.getInt("width");
		if(width!=null)cfg.setWidth(width);
		Integer height = field.getInt("height");
		if(height!=null)cfg.setHeight(height);
	}
	
	/**
	 * 动态创建TextField
	 * @throws SQLException 
	 */
	private static CompositeMap createTextField(IObjectRegistry registry,String service_name, String dimension_type, String dimension_value, Long form_field_id,String bindTarget,CompositeMap field) throws SQLException{
		TextFieldConfig ttf = TextFieldConfig.getInstance();
		initEditorPropertity(ttf,bindTarget,field);
		initDataSetField(registry,null,service_name, dimension_type, dimension_value, form_field_id,bindTarget,field);
		Integer stringLeng = field.getInt("string_length");
		String strCase = field.getString("string_case");
		if(stringLeng != null)ttf.setMaxLength(stringLeng);
		if(strCase != null)ttf.setTypeCase(strCase);
		return ttf.getObjectContext();
	}
	
	/**
	 * 动态创建NumberField
	 * @throws SQLException 
	 */
	private static CompositeMap createNumberField(IObjectRegistry registry,String service_name, String dimension_type, String dimension_value, Long form_field_id,String bindTarget,CompositeMap field) throws SQLException{
		NumberFieldConfig nf = NumberFieldConfig.getInstance();
		initEditorPropertity(nf,bindTarget,field);
		initDataSetField(registry,null,service_name, dimension_type, dimension_value, form_field_id,bindTarget,field);
		String allowdecimals = field.getString("number_allowdecimals");
		if(allowdecimals != null) {
			boolean isAllowdecimals = "Y".equalsIgnoreCase(allowdecimals);
			nf.setAllowDecimals(isAllowdecimals == true);
			if(isAllowdecimals) {
				Integer decimalprecision = field.getInt("number_decimalprecision");
				if(decimalprecision != null) nf.setDecimalPrecision(decimalprecision);
			}
		}
		String allownegative = field.getString("number_allownegative");
		if(allownegative != null) nf.setAllowNegative("Y".equalsIgnoreCase(allownegative));
		String allowformat = field.getString("number_allowformat");
		if(allowformat != null) nf.setAllowFormat("Y".equalsIgnoreCase(allowformat));
		String allowpad = field.getString("number_allowpad");
		if(allowpad != null) nf.setAllowPad("Y".equalsIgnoreCase(allowpad));
		return nf.getObjectContext();
	}
	
	/**
	 * 动态创建DatePicker
	 * @throws SQLException 
	 */
	private static CompositeMap createDatePicker(IObjectRegistry registry,String service_name, String dimension_type, String dimension_value, Long form_field_id,String bindTarget,CompositeMap field) throws SQLException{
		DatePickerConfig dpf = DatePickerConfig.getInstance();
		initEditorPropertity(dpf,bindTarget,field);
		initDataSetField(registry,null,service_name, dimension_type, dimension_value, form_field_id,bindTarget,field);
		String format = field.getString("datepicker_format");
		if(format != null)dpf.setFormat(format);
		Integer size  = field.getInt("datepicker_size");
		if(size != null)dpf.setViewSize(size);
		return dpf.getObjectContext();
	}
	
	/**
	 * 动态创建Lov
	 */
	private static CompositeMap createLov(IObjectRegistry registry,String service_name, String dimension_type, String dimension_value, Long form_field_id,String bindTarget,CompositeMap field) throws SQLException, JSONException{
		LovConfig lc = LovConfig.getInstance();
		initEditorPropertity(lc,bindTarget,field);
		
		DataSetFieldConfig dsfc = DataSetFieldConfig.getInstance();
		String name = field.getString("field_name");
		String display = name + "_display";
		lc.setName(display);
		dsfc.setName(display);
		Integer lovHeight = field.getInt("lov_height");
		if(lovHeight != null) dsfc.setLovHeight(lovHeight);
		Integer lovWidth = field.getInt("lov_width");
		if(lovWidth != null) dsfc.setLovWidth(lovWidth);
		Integer gridHeight = field.getInt("lov_grid_height");
		if(gridHeight != null) dsfc.setLovGridHeight(gridHeight);
		dsfc.setTitle(field.getString("lov_title",""));
		dsfc.setLovService(field.getString("lov_bm"));
		
		String valueField = field.getString("lov_value_field");
		addMapping(dsfc,valueField,name);
		
		String displayField = field.getString("lov_display_field");
		addMapping(dsfc,displayField,display);
		
		String mapping = field.getString("lov_mapping");
		if(mapping!= null){
			JSONArray array = new JSONArray(mapping);
			for(int i=0;i<array.length();i++){
				JSONObject obj = (JSONObject)array.get(i);
				CompositeMap map = new CompositeMap("map");
				map.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
				map.put("from", obj.get("from"));
				map.put("to", obj.get("to"));
				dsfc.addMap(map);
			}
		}
		initDataSetField(registry,dsfc,service_name, dimension_type, dimension_value, form_field_id,bindTarget,field);
		return lc.getObjectContext();
	}
	
	
	private static void addMapping(DataSetFieldConfig dsfc, String from, String to){
		CompositeMap map = new CompositeMap("map");
		map.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
		map.put("from", from);
		map.put("to", to);
		dsfc.addMap(map);
	}
	
	/**
	 * 动态创建Combobox
	 * @throws SQLException 
	 * @throws JSONException 
	 */
	private static CompositeMap createComboBox(IObjectRegistry registry,String service_name, String dimension_type, String dimension_value, Long form_field_id,String bindTarget,CompositeMap field) throws SQLException, JSONException{
		ComboBoxConfig cbc = ComboBoxConfig.getInstance();
		initEditorPropertity(cbc,bindTarget,field);
		String name = field.getString("field_name");
		String display = name + "_display";
		cbc.setName(display);
		
		String dataType = field.getString("combobox_datasource_type");
		String id = IDGenerator.getInstance().generate();
		DataSetConfig dsc = DataSetConfig.getInstance();
		dsc.setId(id);
		dsc.setLoadData(true);
		if("LOOKUP".equalsIgnoreCase(dataType)){
			dsc.setLookupCode(field.getString("combobox_datasource_value"));
		}else {	
			dsc.setModel(field.getString("combobox_datasource_value"));
		}
		addArrayElement(registry, service_name, dimension_type, dimension_value, bindTarget, "", "before",dsc.getObjectContext().toXML(), form_field_id);
		
		DataSetFieldConfig dsfc = DataSetFieldConfig.getInstance();
		dsfc.setName(display);
		dsfc.setOptions(id);
		String valueField = field.getString("combobox_value_field");
		addMapping(dsfc,valueField,name);
		
		String displayField = field.getString("combobox_display_field");
		addMapping(dsfc,displayField,display);
		
		
		dsfc.setValueField(field.getString("combobox_value_field"));
		dsfc.setDisplayField(field.getString("combobox_display_field"));
		String mapping = field.getString("combobox_mapping");
		if(mapping!= null){
			JSONArray array = new JSONArray(mapping);
			for(int i=0;i<array.length();i++){
				JSONObject obj = (JSONObject)array.get(i);
				CompositeMap map = new CompositeMap("map");
				map.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
				map.put("from", obj.get("from"));
				map.put("to", obj.get("to"));
				dsfc.addMap(map);
			}
		}
		initDataSetField(registry,dsfc,service_name, dimension_type, dimension_value, form_field_id,bindTarget,field);
		return cbc.getObjectContext();
	}	
	

	private static void setElementAttribute(IObjectRegistry registry, String source_file, String dimension_type, String dimension_value,
			String id_value, String attrib_key, String attrib_value, Long form_field_id) throws SQLException {
		String insertSql = "insert into sys_config_customization(record_id,enable_flag,source_file,dimension_type,dimension_value,id_value,"
				+ "mod_type,attrib_key,attrib_value,comments,created_by,creation_date,last_updated_by,last_update_date,form_field_id)values"
				+ "(sys_config_customization_s.nextval,'Y',?,?,?,?,'set_attrib',?,?,'dynamic',0,sysdate,0,sysdate,?)";
		PreparedStatement st = null;
		try {
			Connection conn = CustomSourceCode.getContextConnection(registry);
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
		}
	}

	private static void setArrayElementAttribute(IObjectRegistry registry, String source_file, String dimension_type,
			String dimension_value, String id_value, String array_name, String index_field, String index_value, String attrib_key,
			String attrib_value, long form_field_id) throws SQLException {
		String insertSql = "insert into sys_config_customization(record_id,enable_flag,source_file,dimension_type,dimension_value,id_value,"
				+ "mod_type,array_name,index_field,index_value,attrib_key,attrib_value,comments,created_by,creation_date,last_updated_by,last_update_date,form_field_id)values"
				+ "(sys_config_customization_s.nextval,'Y',?,?,?,?,'set_attrib',?,?,?,?,?,'dynamic',0,sysdate,0,sysdate,?)";
		PreparedStatement st = null;
		try {
			Connection conn = CustomSourceCode.getContextConnection(registry);
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
		}
	}

	private static void addArrayElement(IObjectRegistry registry, String source_file, String dimension_type, String dimension_value,
			String id_value, String array_name, String position, String config_content, long form_field_id) throws SQLException {
		String insertSql = "insert into sys_config_customization(record_id,enable_flag,source_file,dimension_type,dimension_value,id_value,"
				+ "mod_type,array_name,position,config_content,comments,created_by,creation_date,last_updated_by,last_update_date,form_field_id)values"
				+ "(sys_config_customization_s.nextval,'Y',?,?,?,?,'insert',?,?,?,'dynamic',0,sysdate,0,sysdate,?)";
		PreparedStatement st = null;
		try {
			Connection conn = CustomSourceCode.getContextConnection(registry);
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
		}
	}

	private static void deleteNode(IObjectRegistry registry, String source_file, String dimension_type, String dimension_value,
			String id_value, long form_field_id) throws SQLException {
		String insertSql = "insert into sys_config_customization(record_id,enable_flag,source_file,dimension_type,dimension_value,id_value,"
				+ "mod_type,comments,created_by,creation_date,last_updated_by,last_update_date,form_field_id)values"
				+ "(sys_config_customization_s.nextval,'Y',?,?,?,?,'delete','dynamic',0,sysdate,0,sysdate,?)";
		PreparedStatement st = null;
		try {
			Connection conn = CustomSourceCode.getContextConnection(registry);
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
		}
	}

	private static boolean isNotNULL(String content) {
		if (content == null)
			return false;
		if ("".equals(content) || "null".equals(content))
			return false;
		return true;
	}

	private static void sqlExecute(IObjectRegistry registry, String sql) throws SQLException {
		Connection conn = CustomSourceCode.getContextConnection(registry);
		Statement st = conn.createStatement();
		try {
			st.execute(sql);
		} finally {
			DBUtil.closeStatement(st);
		}
	}
}
