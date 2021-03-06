package aurora.application.features.cstm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import uncertain.composite.XMLOutputter;
import uncertain.datatype.IntegerType;
import uncertain.datatype.StringType;
import uncertain.exception.BuiltinExceptionFactory;
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
import aurora.presentation.component.std.config.GridColumnConfig;
import aurora.presentation.component.std.config.LovConfig;
import aurora.presentation.component.std.config.NumberFieldConfig;
import aurora.presentation.component.std.config.TextFieldConfig;

public class ConfigCustomizationUtil {
	
	public static void updateDynamicScreen(IObjectRegistry registry, Long header_id,String dimension_type,String dimension_value)throws Exception {
		StringBuffer update_form_sql = new StringBuffer();
		update_form_sql.append("update sys_config_customization set dimension_type= ?,dimension_value = ? where source_id in(select f.form_id from sys_dynamic_forms f where f.header_id = ?) and source_type = 'SYS_DYNAMIC_FORMS'");
		PrepareParameter[] para = new PrepareParameter[3];
		para[0] = new PrepareParameter(new StringType(), dimension_type);
		para[1] = new PrepareParameter(new StringType(), dimension_value);
		para[2] = new PrepareParameter(new IntegerType(), header_id);
		CustomSourceCode.sqlExecuteWithParas(registry, update_form_sql.toString(), para);
		
		StringBuffer update_grid_sql = new StringBuffer();
		update_grid_sql.append("update sys_config_customization set dimension_type= ?, dimension_value = ? where source_id in (select g.grid_id from sys_dynamic_grids g where g.header_id = ?) and source_type = 'SYS_DYNAMIC_GRIDS'");
		CustomSourceCode.sqlExecuteWithParas(registry, update_grid_sql.toString(), para);
		
		StringBuffer update_reorder_sql = new StringBuffer();
		update_reorder_sql.append("update sys_config_customization set dimension_type= ?, dimension_value = ? where source_id in (select g.cmp_id from sys_dynamic_grids g where g.header_id = ?) and source_type = 'SYS_DYNAMIC_GRIDS_CMP_ID'");
		CustomSourceCode.sqlExecuteWithParas(registry, update_reorder_sql.toString(), para);
	}
	
	
	public static void deleteDynamicScreen(IObjectRegistry registry, Long header_id)throws Exception {
		StringBuffer form_sql = new StringBuffer();
		form_sql.append("select f.form_id from sys_dynamic_forms f where f.header_id= ?");
		PrepareParameter[] para = new PrepareParameter[1];
		para[0] = new PrepareParameter(new IntegerType(), header_id);
		CompositeMap formResult = CustomSourceCode.sqlQueryWithParas(registry, form_sql.toString(), para);
		List<CompositeMap> formList = formResult.getChilds();
		if (formList != null) {
			String delete_form_sql = "delete from sys_dynamic_forms where header_id= ? ";
			String delete_form_cust_sql = "delete from sys_config_customization where upper(source_type) = 'SYS_DYNAMIC_FORMS' and source_id= ? ";
			for (CompositeMap formRecord : formList) {
				String form_id = formRecord.getString("form_id");
				PrepareParameter[] p = new PrepareParameter[1];
				p[0] = new PrepareParameter(new StringType(), form_id);				
				CustomSourceCode.sqlExecuteWithParas(registry, delete_form_cust_sql, p);
			}
			CustomSourceCode.sqlExecuteWithParas(registry, delete_form_sql, para);
		}
		
		StringBuffer grid_sql = new StringBuffer();
		grid_sql.append("select g.grid_id,g.cmp_id from sys_dynamic_grids g where g.header_id= ?");
		CompositeMap gridResult = CustomSourceCode.sqlQueryWithParas(registry, grid_sql.toString(), para);
		String cmp_id = null;
		List<CompositeMap> gridList = gridResult.getChilds();
		if (gridList != null) {
			String delete_grid_sql = "delete from sys_dynamic_grids where header_id= ? ";
			String delete_grid_cust_sql = "delete from sys_config_customization where upper(source_type) = 'SYS_DYNAMIC_GRIDS' and source_id= ? ";
			for (CompositeMap gridRecord : gridList) {
				String grid_id = gridRecord.getString("grid_id");
				cmp_id = gridRecord.getString("cmp_id"); 
				
				PrepareParameter[] parameters = new PrepareParameter[1];
				parameters[0] = new PrepareParameter(new StringType(), grid_id);
				CustomSourceCode.sqlExecuteWithParas(registry, delete_grid_cust_sql, parameters);
			}
			if(cmp_id != null) {
				String delete_reorder_sql = "delete from sys_config_customization where upper(source_type) = 'SYS_DYNAMIC_GRIDS_CMP_ID' and source_id= ? ";
				PrepareParameter[] parameters = new PrepareParameter[1];
				parameters[0] = new PrepareParameter(new StringType(), cmp_id);
				CustomSourceCode.sqlExecuteWithParas(registry, delete_reorder_sql, parameters);
			}
			CustomSourceCode.sqlExecuteWithParas(registry, delete_grid_sql, para);
		}
		
	}
	
	public static void deleteBusinessObject(IObjectRegistry registry, Long object_id)throws Exception {
		StringBuffer form_sql = new StringBuffer();
		form_sql.append("select f.form_id,fl.field_id from sys_dynamic_forms f,sys_business_object_flexfields fl where f.field_id=fl.field_id and fl.business_object_id= ?");
		PrepareParameter[] formPara = new PrepareParameter[1];
		formPara[0] = new PrepareParameter(new IntegerType(), object_id);
		CompositeMap formResult = CustomSourceCode.sqlQueryWithParas(registry, form_sql.toString(), formPara);
		List<CompositeMap> formList = formResult.getChilds();
		if (formList != null) {
			String delete_form_sql = "delete from sys_dynamic_forms where field_id= ? ";
			String delete_form_cust_sql = "delete from sys_config_customization where upper(source_type) = 'SYS_DYNAMIC_FORMS' and source_id= ? ";
			for (CompositeMap formRecord : formList) {
				Long field_id = formRecord.getLong("field_id");
				String form_id = formRecord.getString("form_id");
				PrepareParameter[] p1 = new PrepareParameter[1];
				p1[0] = new PrepareParameter(new IntegerType(), field_id);
				CustomSourceCode.sqlExecuteWithParas(registry, delete_form_sql, p1);
				PrepareParameter[] p2 = new PrepareParameter[1];
				p2[0] = new PrepareParameter(new StringType(), form_id);				
				CustomSourceCode.sqlExecuteWithParas(registry, delete_form_cust_sql, p2);
			}
		}
		
		
		StringBuffer grid_sql = new StringBuffer();
		grid_sql.append("select g.grid_id,g.cmp_id,fl.field_id from sys_dynamic_grids g,sys_business_object_flexfields fl where g.field_id=fl.field_id and fl.business_object_id= ?");
		CompositeMap gridResult = CustomSourceCode.sqlQueryWithParas(registry, grid_sql.toString(), formPara);
		String cmp_id = null;
		List<CompositeMap> gridList = gridResult.getChilds();
		if (gridList != null) {
			String delete_grid_sql = "delete from sys_dynamic_grids where field_id= ? ";
			String delete_grid_cust_sql = "delete from sys_config_customization where upper(source_type) = 'SYS_DYNAMIC_GRIDS' and source_id= ? ";
			for (CompositeMap gridRecord : gridList) {
				Long field_id = gridRecord.getLong("field_id");
				String grid_id = gridRecord.getString("grid_id");
				cmp_id = gridRecord.getString("cmp_id"); 
				PrepareParameter[] p1 = new PrepareParameter[1];
				p1[0] = new PrepareParameter(new IntegerType(), field_id);
				CustomSourceCode.sqlExecuteWithParas(registry, delete_grid_sql, p1);
				
				PrepareParameter[] p2 = new PrepareParameter[1];
				p2[0] = new PrepareParameter(new StringType(), grid_id);
				CustomSourceCode.sqlExecuteWithParas(registry, delete_grid_cust_sql, p2);
			}
			if(cmp_id != null) {
				String delete_reorder_sql = "delete from sys_config_customization where upper(source_type) = 'SYS_DYNAMIC_GRIDS_CMP_ID' and source_id= ? ";
				PrepareParameter[] parameters = new PrepareParameter[1];
				parameters[0] = new PrepareParameter(new StringType(), cmp_id);
				CustomSourceCode.sqlExecuteWithParas(registry, delete_reorder_sql, parameters);
			}
			
		}
	}
	
	
	
	public static void formConfigConvertToCust(IObjectRegistry registry, Long form_id) throws Exception {
		StringBuffer query_sql = new StringBuffer();
		query_sql.append("select s.service_name,f.header_id,f.container_id from sys_dynamic_forms f,sys_dynamic_headers h,sys_service s where f.header_id=h.header_id and h.service_id=s.service_id and f.form_id = ?");
		PrepareParameter[] queryPara = new PrepareParameter[1];
		queryPara[0] = new PrepareParameter(new IntegerType(), form_id);
		CompositeMap queryResult = CustomSourceCode.sqlQueryWithParas(registry, query_sql.toString(), queryPara);
		List<CompositeMap> formList = queryResult.getChilds();
		if (formList != null) {
			for (CompositeMap formRecord : formList) {
				String service_name = formRecord.getString("service_name");	
				String container_id = formRecord.getString("container_id");	
				Long header_id = formRecord.getLong("header_id");
				formConfigConvertToCust(registry,service_name,header_id,container_id);
			}
		}
	}

	public static void formConfigConvertToCust(IObjectRegistry registry, String filePath, Long headId, String containerId) throws Exception {

		if (headId < 1)
			throw new IllegalArgumentException("parameter headId can not be null");
		
		StringBuffer query_sql = new StringBuffer();
		query_sql.append("select form_id from sys_dynamic_forms where header_id = ? and container_id = ? order by sequence asc");
		PrepareParameter[] queryPara = new PrepareParameter[2];
		queryPara[0] = new PrepareParameter(new IntegerType(), headId);
		queryPara[1] = new PrepareParameter(new StringType(), containerId);
		CompositeMap queryResult = CustomSourceCode.sqlQueryWithParas(registry, query_sql.toString(), queryPara);		
		List<CompositeMap> formList = queryResult.getChilds();
		if (formList != null) {
			for (CompositeMap formRecord : formList) {
				Long form_field_id = formRecord.getLong("form_id");				
				String source_type = "SYS_DYNAMIC_FORMS";
				String source_id = String.valueOf(form_field_id);
				
				// 删除以前的动态配置记录
				PrepareParameter[] parameters = new PrepareParameter[1];
				parameters[0] = new PrepareParameter(new StringType(), source_id);
				StringBuffer delete_cust_sql = new StringBuffer(
						"delete from sys_config_customization t where t.source_id = ? and upper(t.source_type)='" + source_type + "' ");
				CustomSourceCode.sqlExecuteWithParas(registry, delete_cust_sql.toString(), parameters);
				
				// 新增本次记录
				StringBuffer config_sql = new StringBuffer();
				config_sql.append(" select s.service_name,h.customization_header_id,h.dimension_code,h.dimension_value,t.cmp_id,t.name,t.prompt, ");
				config_sql.append(" 	   t.editabled_flag,t.enabled_flag,t.required_flag,t.bm,t.field_id, ");
				config_sql.append(" 	   t.row_num,t.column_num,t.bind_target,t.container_id, ");
				config_sql.append("        f.field_name,f.field_description,f.editor_type,nvl(t.width,f.width) width,f.height,");
				config_sql.append("        f.string_length,f.string_format,f.string_case,");
				config_sql.append("        f.number_allowdecimals,f.number_decimalprecision,f.number_allownegative,f.number_allowformat,f.number_allowpad,");
				config_sql.append("        f.datepicker_format,f.datepicker_size,");
				config_sql.append("        f.combobox_value_field,f.combobox_display_field,f.combobox_mapping,f.combobox_datasource_type,f.combobox_datasource_value,");
				config_sql.append("        f.lov_height,f.lov_height,f.lov_width,f.lov_grid_height,f.lov_title,f.lov_bm,f.lov_value_field,f.lov_display_field,f.lov_mapping,f.lov_labelwidth ");		
				config_sql.append("   from sys_dynamic_forms t, sys_dynamic_headers h,sys_service s, sys_business_object_flexfields f ");
				config_sql.append("  where h.header_id = t.header_id and s.service_id = h.service_id and t.field_id = f.field_id(+) and t.form_id = ? ");
				parameters[0] = new PrepareParameter(new IntegerType(), form_field_id);
				CompositeMap result = CustomSourceCode.sqlQueryWithParas(registry, config_sql.toString(), parameters);
				if (result != null) {
					List<CompositeMap> childList = result.getChilds();
					if (childList != null) {
						for (CompositeMap record : childList) {
							if (filePath == null || "".equals(filePath))
								filePath = record.getString("service_name");
							CompositeMap fileContent = CustomSourceCode.getFileContent(registry, filePath);
							
							int flex_field_id = record.getInt("field_id", -1);
							String prompt = record.getString("prompt");
							String customization_header_id = record.getString("customization_header_id");
							String width = record.getString("width");
							String service_name = record.getString("service_name");
							String dimension_type = record.getString("dimension_code");
							String dimension_value = record.getString("dimension_value");
							String cmp_id = record.getString("cmp_id");
							String editabled_flag = record.getString("editabled_flag");
							String read_only = "Y".equals(editabled_flag) ? "false" : "true";
							String enabled_flag = record.getString("enabled_flag");
							String required_flag = record.getString("required_flag");
							String required = "Y".equals(required_flag) ? "true" : "false";
							String bm = record.getString("bm");
							String bindTarget = record.getString("bind_target");
//							String containerId = record.getString("container_id");
							// 是否弹性域字段
							if (flex_field_id < 0) {
								CompositeMap editor = SourceCodeUtil.searchNodeById(fileContent, cmp_id);
								if (editor == null)
									throw BuiltinExceptionFactory.createUnknownNodeWithName(fileContent.asLocatable(), "editor", "id", cmp_id);
								if ("N".equals(enabled_flag)) {
									deleteNode(registry, customization_header_id,service_name, dimension_type, dimension_value, cmp_id, source_type, source_id);
									continue;
								}
								if (isNotNULL(prompt)) {
									setElementAttribute(registry,customization_header_id, service_name, dimension_type, dimension_value, cmp_id, "prompt", prompt,source_type, source_id);
								}
								if (isNotNULL(width)) {
									setElementAttribute(registry,customization_header_id, service_name, dimension_type, dimension_value, cmp_id, "width", width,source_type, source_id);
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
											setArrayElementAttribute(registry,customization_header_id,service_name, dimension_type, dimension_value, dataSetID, "fields",
													"name", editorName, "readonly", read_only, source_type, source_id);
										}
										if (isNotNULL(required_flag)) {
											setArrayElementAttribute(registry,customization_header_id, service_name, dimension_type, dimension_value, dataSetID, "fields",
													"name", editorName, "required", required, source_type, source_id);
										}
										if (isNotNULL(bm)) {
											setArrayElementAttribute(registry,customization_header_id, service_name, dimension_type, dimension_value, dataSetID, "fields",
													"name", editorName, "model", bm, source_type, source_id);
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
										addArrayElement(registry, customization_header_id,service_name, dimension_type, dimension_value, dataSetID, "fields", "last_child",
												newFieldContent, source_type, source_id);
									}
								}
							} else {
								String editorType = record.getString("editor_type");
								CompositeMap fieldObject = null;
								if ("TEXTFIELD".equalsIgnoreCase(editorType)) {
									fieldObject = createTextField(registry, customization_header_id,service_name, dimension_type, dimension_value, form_field_id,bindTarget, record, source_type);
								} else if ("NUMBERFIELD".equalsIgnoreCase(editorType)) {
									fieldObject = createNumberField(registry, customization_header_id,service_name, dimension_type, dimension_value, form_field_id,bindTarget, record, source_type);
								} else if ("DATEPICKER".equalsIgnoreCase(editorType)) {
									fieldObject = createDatePicker(registry, customization_header_id,service_name, dimension_type, dimension_value, form_field_id,bindTarget, record, source_type);
								} else if ("COMBOBOX".equalsIgnoreCase(editorType)) {
									fieldObject = createComboBox(registry, customization_header_id,service_name, dimension_type, dimension_value, form_field_id,bindTarget, record, source_type);
								} else if ("LOV".equalsIgnoreCase(editorType)) {
									fieldObject = createLov(registry, customization_header_id,service_name, dimension_type, dimension_value, form_field_id, bindTarget,record, source_type);
								}
								if (fieldObject != null)
									addArrayElement(registry, customization_header_id,service_name, dimension_type, dimension_value, containerId, "", "last_child",
											fieldObject.toXML(), source_type, source_id);
							}
						}
					}
				}
			}
		}
		
		
		
	}

	public static void gridConfigConvertToCust(IObjectRegistry registry, Long grid_field_id) throws Exception {
		if (grid_field_id < 1)
			throw new IllegalArgumentException("parameter grid_field_id can not be null");

		String source_type = "SYS_DYNAMIC_GRIDS";
		String source_id = String.valueOf(grid_field_id);

		// 删除以前的动态配置记录
		StringBuffer delete_cust_sql = new StringBuffer(
				"delete from sys_config_customization t where t.source_id = ? and upper(t.source_type)='" + source_type + "' ");
		PrepareParameter[] parameters = new PrepareParameter[1];
		parameters[0] = new PrepareParameter(new StringType(), source_id);
		CustomSourceCode.sqlExecuteWithParas(registry, delete_cust_sql.toString(), parameters);

		// 新增本次记录
		StringBuffer config_sql = new StringBuffer();
		config_sql.append(" select s.service_name,h.customization_header_id,h.dimension_code,h.dimension_value, ");
		config_sql.append("        t.cmp_id,t.name,t.prompt,nvl(t.width,f.width) width, ");
		config_sql.append("        t.align,t.locked_flag,t.hidden_flag,t.editabled_flag,");
		config_sql.append("        t.sequence,t.required_flag,t.field_id, ");
		config_sql.append("        f.field_name,f.field_description,f.editor_type,f.height,");
		config_sql.append("        f.string_length,f.string_format,f.string_case,");
		config_sql.append("        f.number_allowdecimals,f.number_decimalprecision,f.number_allownegative,f.number_allowformat,f.number_allowpad,");
		config_sql.append("        f.datepicker_format,f.datepicker_size,");
		config_sql.append("        f.combobox_value_field,f.combobox_display_field,f.combobox_mapping,f.combobox_datasource_type,f.combobox_datasource_value,");
		config_sql.append("        f.lov_height,f.lov_height,f.lov_width,f.lov_grid_height,f.lov_title,f.lov_bm,f.lov_value_field,f.lov_display_field,f.lov_mapping,f.lov_labelwidth ");
		config_sql.append("   from sys_dynamic_grids t, sys_dynamic_headers h, sys_service s,sys_business_object_flexfields f  ");
		config_sql.append("  where h.header_id = t.header_id ");
		config_sql.append("        and s.service_id = h.service_id ");
		config_sql.append("        and t.field_id = f.field_id(+) ");
		config_sql.append("        and t.grid_id = ? ");
		parameters[0] = new PrepareParameter(new IntegerType(), grid_field_id);
		CompositeMap result = CustomSourceCode.sqlQueryWithParas(registry, config_sql.toString(), parameters);
		if (result != null) {
			List<CompositeMap> childList = result.getChilds();
			if (childList != null) {
				for (CompositeMap record : childList) {
					String service_name = record.getString("service_name");
					String filePath = service_name;
					String customization_header_id = record.getString("customization_header_id");
					String dimension_type = record.getString("dimension_code");
					String dimension_value = record.getString("dimension_value");
					String column_name = record.getString("name");
					String cmp_id = record.getString("cmp_id");
					String prompt = record.getString("prompt");
					String width = record.getString("width");
					String align = record.getString("align");
					String editabled_flag = record.getString("editabled_flag");
					String locked_flag = record.getString("locked_flag");
					String lock = "Y".equals(locked_flag) ? "true" : "false";
					String hidden_flag = record.getString("hidden_flag");

					String required_flag = record.getString("required_flag");
					String required = "Y".equals(required_flag) ? "true" : "false";

					CompositeMap fileContent = CustomSourceCode.getFileContent(registry, filePath);
					int flex_field_id = record.getInt("field_id", -1);

					CompositeMap grid = SourceCodeUtil.searchNodeById(fileContent, cmp_id);
					if (grid == null)
						throw BuiltinExceptionFactory.createUnknownNodeWithName(fileContent.asLocatable(), "grid", "id", cmp_id);
					String dataSetID = grid.getString("bindtarget");
					if (dataSetID == null || "".equals(dataSetID))
						throw BuiltinExceptionFactory.createAttributeMissing(grid.asLocatable(), "bindtarget");
					// 是否弹性域字段
					if (flex_field_id < 0) {
						if ("Y".equals(hidden_flag)) {
							deleteArrayElement(registry, customization_header_id,service_name, dimension_type, dimension_value, cmp_id, "columns", "name",
									column_name, source_type, source_id);
							continue;
						}
						if (isNotNULL(prompt)) {
							setArrayElementAttribute(registry, customization_header_id,service_name, dimension_type, dimension_value, cmp_id, "columns", "name",
									column_name, "prompt", source_type, prompt, source_id);
						}
						if (isNotNULL(width)) {
							setArrayElementAttribute(registry, customization_header_id,service_name, dimension_type, dimension_value, cmp_id, "columns", "name",
									column_name, "width", width, source_type, source_id);
						}
						if (isNotNULL(align)) {
							setArrayElementAttribute(registry, customization_header_id,service_name, dimension_type, dimension_value, cmp_id, "columns", "name",
									column_name, "align", align, source_type, source_id);
						}
						if (isNotNULL(locked_flag)) {
							setArrayElementAttribute(registry, customization_header_id,service_name, dimension_type, dimension_value, cmp_id, "columns", "name",
									column_name, "lock", lock, source_type, source_id);
						}
						if (isNotNULL(required_flag)) {
							
							CompositeMap dataSet = SourceCodeUtil.searchNodeById(fileContent, dataSetID);
							if (dataSet == null)
								throw BuiltinExceptionFactory.createUnknownNodeWithName(fileContent.asLocatable(), "dataSet", "id",
										dataSetID);

							CompositeMap fields = dataSet.getChild("fields");
							if (fields == null) {
								fields = dataSet.createChild("a", AuroraApplication.AURORA_FRAMEWORK_NAMESPACE, "fields");
							}
							CompositeMap fieldNode = fields.getChildByAttrib("name", column_name);
							if (fieldNode != null) {
								if (isNotNULL(required_flag)) {
									setArrayElementAttribute(registry, customization_header_id,service_name, dimension_type, dimension_value, dataSetID, "fields",
											"name", column_name, "required", required, source_type, source_id);
								}
							} else {
								CompositeMap newField = new CompositeMap("a", AuroraApplication.AURORA_FRAMEWORK_NAMESPACE, "field");
								newField.put("name", column_name);

								if (isNotNULL(required_flag)) {
									newField.put("required", required);
								}
								String newFieldContent = XMLOutputter.defaultInstance().toXML(newField, false);
								addArrayElement(registry, customization_header_id,service_name, dimension_type, dimension_value, dataSetID, "fields", "last_child",
										newFieldContent, source_type, source_id);
							}
						}
					} else {
						String editorType = record.getString("editor_type");
						CompositeMap fieldObject = null;
						if ("TEXTFIELD".equalsIgnoreCase(editorType)) {
							fieldObject = createTextField(registry, customization_header_id,service_name, dimension_type, dimension_value, grid_field_id,dataSetID, record, source_type);
						} else if ("NUMBERFIELD".equalsIgnoreCase(editorType)) {
							fieldObject = createNumberField(registry, customization_header_id,service_name, dimension_type, dimension_value, grid_field_id,dataSetID, record, source_type);
						} else if ("DATEPICKER".equalsIgnoreCase(editorType)) {
							fieldObject = createDatePicker(registry, customization_header_id,service_name, dimension_type, dimension_value, grid_field_id,dataSetID, record, source_type);
						} else if ("COMBOBOX".equalsIgnoreCase(editorType)) {
							fieldObject = createComboBox(registry, customization_header_id,service_name, dimension_type, dimension_value, grid_field_id,dataSetID, record, source_type);
						} else if ("LOV".equalsIgnoreCase(editorType)) {
							fieldObject = createLov(registry, customization_header_id,service_name, dimension_type, dimension_value, grid_field_id, dataSetID,record, source_type);
						}
						
						
						if (fieldObject != null) {
							GridColumnConfig gcc = GridColumnConfig.getInstance();
							FieldConfig fc = FieldConfig.getInstance(fieldObject);
							gcc.setName(fc.getName());
							gcc.setPrompt(fc.getPrompt());
							gcc.setWidth(fc.getWidth());
							if("DATEPICKER".equalsIgnoreCase(editorType))
								gcc.setRenderer("Aurora.formatDate");
							if("NUMBERFIELD".equalsIgnoreCase(editorType)) {
								String allowformat = record.getString("number_allowformat");
								if ("Y".equalsIgnoreCase(allowformat)) {
									Integer decimalprecision = record.getInt("number_decimalprecision");
									if(decimalprecision==null || decimalprecision==2){
										gcc.setRenderer("Aurora.formatMoney");
									}else {
										gcc.setRenderer("Aurora.formatNumber");
									}					
								}
							}
							
							
							String editor = null;
							if("Y".equals(editabled_flag)){
								editor = IDGenerator.getInstance().generate();
								gcc.setEditor(editor);	
							}
							if (isNotNULL(locked_flag)) gcc.setLock("Y".equals(locked_flag));
							if (isNotNULL(align)) gcc.setAlign(align);
							addArrayElement(registry, customization_header_id,service_name, dimension_type, dimension_value, cmp_id, "columns", "last_child",gcc.getObjectContext().toXML(), source_type, source_id);
							if("Y".equals(editabled_flag)){
								fieldObject.put("name", null);
								fieldObject.put("bindtarget", null);
								fieldObject.put("id", editor);
								fieldObject.put("prompt", null);
								addArrayElement(registry, customization_header_id,service_name, dimension_type, dimension_value, cmp_id, "editors", "last_child",fieldObject.toXML(), source_type, source_id);
							}
						}
					}
				}
			}
		}
	}

	public static void gridConfigConvertToCustReOrder(IObjectRegistry registry, Long grid_header_id, String cmp_id) throws Exception {
		if (grid_header_id < 1)
			throw new IllegalArgumentException("parameter grid_header_id can not be null");

		String source_type = "SYS_DYNAMIC_GRIDS_CMP_ID";
		String source_id = cmp_id;

		StringBuffer config_sql = new StringBuffer();
		config_sql.append(" select s.service_name, ");
		config_sql.append("        h.customization_header_id, ");
		config_sql.append("        h.dimension_code, ");
		config_sql.append("        h.dimension_value, ");
		config_sql.append("        (select wmsys.wm_concat(g.column_name) ");
		config_sql.append("   		from (select t.name||decode(o.editor_type, 'LOV', '_display', 'COMBOBOX', '_display', '') column_name");
		config_sql.append("  			  from sys_dynamic_grids t,sys_business_object_flexfields o");
		config_sql.append("        		  where t.cmp_id = ? ");
		config_sql.append("        		    and t.header_id = ? ");
		config_sql.append("        		    and t.sequence is not null ");
		config_sql.append("        		    and t.field_id = o.field_id(+)");
		config_sql.append("       		  order by t.sequence) g) column_names ");
		config_sql.append("   from sys_dynamic_headers h, sys_service s");
		config_sql.append("  where s.service_id = h.service_id ");
		config_sql.append("    and h.header_id = ?");

		PrepareParameter[] parameters = new PrepareParameter[3];
		parameters[0] = new PrepareParameter(new StringType(), cmp_id);
		parameters[1] = new PrepareParameter(new IntegerType(), grid_header_id);
		parameters[2] = new PrepareParameter(new IntegerType(), grid_header_id);
		// 新增本次记录
		CompositeMap result = CustomSourceCode.sqlQueryWithParas(registry, config_sql.toString(), parameters);
		if (result != null) {
			List<CompositeMap> childList = result.getChilds();
			if (childList != null) {
				if (childList.size() > 1)
					throw new IllegalArgumentException(" find more than one record with parameter:'grid_header_id'=" + grid_header_id
							+ " cmp_id:" + cmp_id);
				CompositeMap record = childList.get(0);
				String column_names = record.getString("column_names");
				if(!isNotNULL(column_names))
					return;
				String customization_header_id = record.getString("customization_header_id");
				String service_name = record.getString("service_name");
				String dimension_type = record.getString("dimension_code");
				String dimension_value = record.getString("dimension_value");
				StringBuffer delete_cust_sql = new StringBuffer(
						"delete from sys_config_customization t where t.source_id = ? and upper(t.source_type)='" + source_type
								+ "' and source_file = ?");
				parameters = new PrepareParameter[2];
				parameters[0] = new PrepareParameter(new StringType(), cmp_id);
				parameters[1] = new PrepareParameter(new StringType(), service_name);
				// 删除以前的动态配置记录
				CustomSourceCode.sqlExecuteWithParas(registry, delete_cust_sql.toString(), parameters);

				reOrder(registry,customization_header_id, service_name, dimension_type, dimension_value, cmp_id, "columns","name",column_names, source_type, source_id);

			}
		}
	}

	private static void initDataSetField(IObjectRegistry registry, DataSetFieldConfig dsfc, String customization_header_id,String service_name, String dimension_type,
			String dimension_value, Long form_field_id, String bindTarget, CompositeMap field, String source_type) throws SQLException {
		String editabled_flag = field.getString("editabled_flag");
		String required_flag = field.getString("required_flag");
		if (isNotNULL(editabled_flag) || isNotNULL(required_flag)) {
			if (dsfc == null) {
				dsfc = DataSetFieldConfig.getInstance();
				dsfc.setName(field.getString("field_name"));
			}
			dsfc.setReadOnly("N".equalsIgnoreCase(editabled_flag));
			dsfc.setRequired("Y".equalsIgnoreCase(required_flag));
			addArrayElement(registry, customization_header_id,service_name, dimension_type, dimension_value, bindTarget, "fields", "last_child", dsfc
					.getObjectContext().toXML(), source_type, String.valueOf(form_field_id));
		}
	}

	private static void initEditorPropertity(FieldConfig cfg, String name, String bindTarget, CompositeMap field) {
		cfg.setName(name);
		cfg.setBindTarget(bindTarget);
		cfg.setPrompt(field.getString("prompt"));

		Integer width = field.getInt("width");
		if (width != null)
			cfg.setWidth(width);
		Integer height = field.getInt("height");
		if (height != null)
			cfg.setHeight(height);
	}

	/**
	 * 动态创建TextField
	 * 
	 * @throws SQLException
	 */
	private static CompositeMap createTextField(IObjectRegistry registry, String customization_header_id,String service_name, String dimension_type,
			String dimension_value, Long form_field_id, String bindTarget, CompositeMap field, String source_type) throws SQLException {
		TextFieldConfig ttf = TextFieldConfig.getInstance();
		initEditorPropertity(ttf, field.getString("field_name"),bindTarget, field);
		initDataSetField(registry, null, customization_header_id,service_name, dimension_type, dimension_value, form_field_id, bindTarget, field, source_type);
		Integer stringLeng = field.getInt("string_length");
		String strCase = field.getString("string_case");
		if (stringLeng != null)
			ttf.setMaxLength(stringLeng);
		if (strCase != null)
			ttf.setTypeCase(strCase);
		return ttf.getObjectContext();
	}

	/**
	 * 动态创建NumberField
	 * 
	 * @throws SQLException
	 */
	private static CompositeMap createNumberField(IObjectRegistry registry, String customization_header_id,String service_name, String dimension_type,
			String dimension_value, Long form_field_id, String bindTarget, CompositeMap field, String source_type) throws SQLException {
		NumberFieldConfig nf = NumberFieldConfig.getInstance();
		initEditorPropertity(nf, field.getString("field_name"),bindTarget, field);
		initDataSetField(registry, null, customization_header_id,service_name, dimension_type, dimension_value, form_field_id, bindTarget, field, source_type);
		String allowdecimals = field.getString("number_allowdecimals");
		if (allowdecimals != null) {
			boolean isAllowdecimals = "Y".equalsIgnoreCase(allowdecimals);
			nf.setAllowDecimals(isAllowdecimals == true);
			if (isAllowdecimals) {
				Integer decimalprecision = field.getInt("number_decimalprecision");
				if (decimalprecision != null)
					nf.setDecimalPrecision(decimalprecision);
			}
		}
		String allownegative = field.getString("number_allownegative");
		if (allownegative != null)
			nf.setAllowNegative("Y".equalsIgnoreCase(allownegative));
		String allowformat = field.getString("number_allowformat");
		if (allowformat != null)
			nf.setAllowFormat("Y".equalsIgnoreCase(allowformat));
		String allowpad = field.getString("number_allowpad");
		if (allowpad != null)
			nf.setAllowPad("Y".equalsIgnoreCase(allowpad));
		return nf.getObjectContext();
	}

	/**
	 * 动态创建DatePicker
	 * 
	 * @throws SQLException
	 */
	private static CompositeMap createDatePicker(IObjectRegistry registry, String customization_header_id,String service_name, String dimension_type,
			String dimension_value, Long form_field_id, String bindTarget, CompositeMap field,String source_type) throws SQLException {
		DatePickerConfig dpf = DatePickerConfig.getInstance();
		initEditorPropertity(dpf, field.getString("field_name"), bindTarget, field);
		DataSetFieldConfig dsfc = DataSetFieldConfig.getInstance();
		dsfc.setName(field.getString("field_name"));
		dsfc.setDataType("date");
		initDataSetField(registry, dsfc, customization_header_id,service_name, dimension_type, dimension_value, form_field_id, bindTarget, field,source_type);
		String format = field.getString("datepicker_format");
		if (format != null)
			dpf.setFormat(format);
		Integer size = field.getInt("datepicker_size");
		if (size != null)
			dpf.setViewSize(size);
		return dpf.getObjectContext();
	}

	/**
	 * 动态创建Lov
	 */
	private static CompositeMap createLov(IObjectRegistry registry, String customization_header_id,String service_name, String dimension_type, String dimension_value,
			Long form_field_id, String bindTarget, CompositeMap field, String source_type) throws SQLException, JSONException {
		LovConfig lc = LovConfig.getInstance();
		String name = field.getString("field_name");
		String display = name + "_display";
		initEditorPropertity(lc,display, bindTarget, field);

		DataSetFieldConfig dsfc = DataSetFieldConfig.getInstance();
		dsfc.setName(display);
		Integer lovHeight = field.getInt("lov_height");
		if (lovHeight != null)
			dsfc.setLovHeight(lovHeight);
		Integer lovWidth = field.getInt("lov_width");
		if (lovWidth != null)
			dsfc.setLovWidth(lovWidth);
		Integer gridHeight = field.getInt("lov_grid_height");
		if (gridHeight != null)
			dsfc.setLovGridHeight(gridHeight);
		dsfc.setTitle(field.getString("lov_title", ""));
		dsfc.setLovService(field.getString("lov_bm"));

		String valueField = field.getString("lov_value_field");
		addMapping(dsfc, valueField, name);

		String displayField = field.getString("lov_display_field");
		addMapping(dsfc, displayField, display);

		String mapping = field.getString("lov_mapping");
		if (mapping != null) {
			JSONArray array = new JSONArray(mapping);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = (JSONObject) array.get(i);
				CompositeMap map = new CompositeMap("map");
				map.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
				map.put("from", obj.get("from"));
				map.put("to", obj.get("to"));
				dsfc.addMap(map);
			}
		}
		initDataSetField(registry, dsfc, customization_header_id,service_name, dimension_type, dimension_value, form_field_id, bindTarget, field, source_type);
		return lc.getObjectContext();
	}

	private static void addMapping(DataSetFieldConfig dsfc, String from, String to) {
		CompositeMap map = new CompositeMap("map");
		map.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
		map.put("from", from);
		map.put("to", to);
		dsfc.addMap(map);
	}

	/**
	 * 动态创建Combobox
	 * 
	 * @throws SQLException
	 * @throws JSONException
	 */
	private static CompositeMap createComboBox(IObjectRegistry registry,String customization_header_id, String service_name, String dimension_type,
			String dimension_value, Long form_field_id, String bindTarget, CompositeMap field, String source_type) throws SQLException,
			JSONException {
		ComboBoxConfig cbc = ComboBoxConfig.getInstance();
		String name = field.getString("field_name");
		String display = name + "_display";
		initEditorPropertity(cbc, display, bindTarget, field);

		String dataType = field.getString("combobox_datasource_type");
		String id = IDGenerator.getInstance().generate();
		DataSetConfig dsc = DataSetConfig.getInstance();
		dsc.setId(id);
		dsc.setLoadData(true);
		if ("LOOKUP".equalsIgnoreCase(dataType)) {
			dsc.setLookupCode(field.getString("combobox_datasource_value"));
		} else {
			dsc.setModel(field.getString("combobox_datasource_value"));
		}
		addArrayElement(registry, customization_header_id,service_name, dimension_type, dimension_value, bindTarget, "", "before", dsc.getObjectContext().toXML(),source_type, String.valueOf(form_field_id));

		DataSetFieldConfig dsfc = DataSetFieldConfig.getInstance();
		dsfc.setName(display);
		dsfc.setOptions(id);
		String valueField = field.getString("combobox_value_field");
		addMapping(dsfc, valueField, name);

		String displayField = field.getString("combobox_display_field");
		addMapping(dsfc, displayField, display);

		dsfc.setValueField(field.getString("combobox_value_field"));
		dsfc.setDisplayField(field.getString("combobox_display_field"));
		String mapping = field.getString("combobox_mapping");
		if (mapping != null) {
			JSONArray array = new JSONArray(mapping);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = (JSONObject) array.get(i);
				CompositeMap map = new CompositeMap("map");
				map.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
				map.put("from", obj.get("from"));
				map.put("to", obj.get("to"));
				dsfc.addMap(map);
			}
		}
		initDataSetField(registry, dsfc, customization_header_id,service_name, dimension_type, dimension_value, form_field_id, bindTarget, field, source_type);
		return cbc.getObjectContext();
	}

	private static void setElementAttribute(IObjectRegistry registry, String header_id,String source_file, String dimension_type, String dimension_value,
			String id_value, String attrib_key, String attrib_value, String source_type, String source_id) throws SQLException {
		String insertSql = "insert into sys_config_customization(record_id,head_id,enable_flag,source_file,dimension_type,dimension_value,id_value,"
				+ "mod_type,attrib_key,attrib_value,comments,created_by,creation_date,last_updated_by,last_update_date,source_type,source_id)values"
				+ "(sys_config_customization_s.nextval,?,'Y',?,?,?,?,'set_attrib',?,?,'dynamic',0,sysdate,0,sysdate,?,?)";
		PreparedStatement st = null;
		try {
			Connection conn = CustomSourceCode.getContextConnection(registry);
			st = conn.prepareStatement(insertSql);
			int i = 1;
			st.setString(i++, header_id);
			st.setString(i++, source_file);
			st.setString(i++, dimension_type);
			st.setString(i++, dimension_value);
			st.setString(i++, id_value);
			st.setString(i++, attrib_key);
			st.setString(i++, attrib_value);
			st.setString(i++, source_type);
			st.setString(i++, source_id);
			st.execute();
		} finally {
			DBUtil.closeStatement(st);
		}
	}

	private static void setArrayElementAttribute(IObjectRegistry registry, String header_id,String source_file, String dimension_type,
			String dimension_value, String id_value, String array_name, String index_field, String index_value, String attrib_key,
			String attrib_value, String source_type, String source_id) throws SQLException {
		String insertSql = "insert into sys_config_customization(record_id,head_id,enable_flag,source_file,dimension_type,dimension_value,id_value,"
				+ "mod_type,array_name,index_field,index_value,attrib_key,attrib_value,comments,created_by,creation_date,last_updated_by,last_update_date,source_type,source_id)values"
				+ "(sys_config_customization_s.nextval,?,'Y',?,?,?,?,'set_attrib',?,?,?,?,?,'dynamic',0,sysdate,0,sysdate,?,?)";
		PreparedStatement st = null;
		try {
			Connection conn = CustomSourceCode.getContextConnection(registry);
			st = conn.prepareStatement(insertSql);
			int i = 1;
			st.setString(i++, header_id);
			st.setString(i++, source_file);
			st.setString(i++, dimension_type);
			st.setString(i++, dimension_value);
			st.setString(i++, id_value);
			st.setString(i++, array_name);
			st.setString(i++, index_field);
			st.setString(i++, index_value);
			st.setString(i++, attrib_key);
			st.setString(i++, attrib_value);
			st.setString(i++, source_type);
			st.setString(i++, source_id);
			st.execute();
		} finally {
			DBUtil.closeStatement(st);
		}
	}

	private static void addArrayElement(IObjectRegistry registry, String header_id, String source_file, String dimension_type, String dimension_value,
			String id_value, String array_name, String position, String config_content, String source_type, String source_id)
			throws SQLException {
		String insertSql = "insert into sys_config_customization(record_id,head_id,enable_flag,source_file,dimension_type,dimension_value,id_value,"
				+ "mod_type,array_name,position,config_content,comments,created_by,creation_date,last_updated_by,last_update_date,source_type,source_id)values"
				+ "(sys_config_customization_s.nextval,?,'Y',?,?,?,?,'insert',?,?,?,'dynamic',0,sysdate,0,sysdate,?,?)";
		PreparedStatement st = null;
		try {
			Connection conn = CustomSourceCode.getContextConnection(registry);
			st = conn.prepareStatement(insertSql);
			int i = 1;
			st.setString(i++, header_id);
			st.setString(i++, source_file);
			st.setString(i++, dimension_type);
			st.setString(i++, dimension_value);
			st.setString(i++, id_value);
			st.setString(i++, array_name);
			st.setString(i++, position);
			st.setString(i++, config_content);
			st.setString(i++, source_type);
			st.setString(i++, source_id);
			st.execute();
		} finally {
			DBUtil.closeStatement(st);
		}
	}

	private static void deleteNode(IObjectRegistry registry, String header_id,String source_file, String dimension_type, String dimension_value,
			String id_value, String source_type, String source_id) throws SQLException {
		String insertSql = "insert into sys_config_customization(record_id,head_id,enable_flag,source_file,dimension_type,dimension_value,id_value,"
				+ "mod_type,comments,created_by,creation_date,last_updated_by,last_update_date,source_type,source_id)values"
				+ "(sys_config_customization_s.nextval,?,'Y',?,?,?,?,'delete','dynamic',0,sysdate,0,sysdate,?,?)";
		PreparedStatement st = null;
		try {
			Connection conn = CustomSourceCode.getContextConnection(registry);
			st = conn.prepareStatement(insertSql);
			int i = 1;
			st.setString(i++, header_id);
			st.setString(i++, source_file);
			st.setString(i++, dimension_type);
			st.setString(i++, dimension_value);
			st.setString(i++, id_value);
			st.setString(i++, source_type);
			st.setString(i++, source_id);
			st.execute();
		} finally {
			DBUtil.closeStatement(st);
		}
	}

	private static void deleteArrayElement(IObjectRegistry registry, String header_id,String source_file, String dimension_type, String dimension_value,
			String id_value, String array_name, String index_field, String index_value, String source_type, String source_id)
			throws SQLException {
		String insertSql = "insert into sys_config_customization(record_id,head_id,enable_flag,source_file,dimension_type,dimension_value,id_value,"
				+ "mod_type,array_name,index_field,index_value,comments,created_by,creation_date,last_updated_by,last_update_date,source_type,source_id)values"
				+ "(sys_config_customization_s.nextval,?,'Y',?,?,?,?,'delete',?,?,?,'dynamic',0,sysdate,0,sysdate,?,?)";
		PreparedStatement st = null;
		try {
			Connection conn = CustomSourceCode.getContextConnection(registry);
			st = conn.prepareStatement(insertSql);
			int i = 1;
			st.setString(i++, header_id);
			st.setString(i++, source_file);
			st.setString(i++, dimension_type);
			st.setString(i++, dimension_value);
			st.setString(i++, id_value);
			st.setString(i++, array_name);
			st.setString(i++, index_field);
			st.setString(i++, index_value);
			st.setString(i++, source_type);
			st.setString(i++, source_id);
			st.execute();
		} finally {
			DBUtil.closeStatement(st);
		}
	}

	private static void reOrder(IObjectRegistry registry, String header_id,String source_file, String dimension_type, String dimension_value,
			String id_value, String array_name,String index_field,String fields_order, String source_type, String source_id) throws SQLException {
		String insertSql = "insert into sys_config_customization(record_id,head_id,enable_flag,source_file,dimension_type,dimension_value,id_value,"
				+ "mod_type,array_name,index_field,fields_order,comments,created_by,creation_date,last_updated_by,last_update_date,source_type,source_id)values"
				+ "(sys_config_customization_s.nextval,?,'Y',?,?,?,?,'re_order',?,?,?,'dynamic',0,sysdate,0,sysdate,?,?)";
		PreparedStatement st = null;
		try {
			Connection conn = CustomSourceCode.getContextConnection(registry);
			st = conn.prepareStatement(insertSql);
			int i = 1;
			st.setString(i++, header_id);
			st.setString(i++, source_file);
			st.setString(i++, dimension_type);
			st.setString(i++, dimension_value);
			st.setString(i++, id_value);
			st.setString(i++, array_name);
			st.setString(i++, index_field);
			st.setString(i++, fields_order);
			st.setString(i++, source_type);
			st.setString(i++, source_id);
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
}
