package aurora.application.features.cstm.bm.flexfield;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.sql.DataSource;

import uncertain.composite.CompositeMap;
import uncertain.core.ILifeCycle;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectRegistry;
import aurora.bm.BmBuiltinExceptionFactory;
import aurora.bm.BusinessModel;
import aurora.bm.Field;
import aurora.bm.Reference;
import aurora.bm.Relation;
import aurora.database.DBUtil;
import aurora.database.FetchDescriptor;
import aurora.database.ParsedSql;
import aurora.database.ResultSetLoader;
import aurora.database.SqlRunner;
import aurora.database.rsconsumer.CompositeMapCreator;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.database.service.SqlServiceContext;
import aurora.service.ServiceThreadLocal;

public class BMFlexFieldProvider extends AbstractLocatableObject implements ILifeCycle, IBMFlexFieldProvider {

	public final static String FLEXFIELD_DEFAULT_DATABASE_TYPE = "VARCHAR";
	public static final String TYPE_LEFT_OUTTER_JOIN = "LEFT OUTER"; 
	
	public final static String FIELD_NAME_PK = "field_name";
	public final static String EDITOR_TYPE_PK = "editor_type";
	public final static String NUMBER_ALLOWDECIMALS_PK = "number_allowdecimals";
	public final static String COMBOBOX_VALUE_FIELD_PK = "combobox_value_field";
	public final static String COMBOBOX_DISPLAY_FIELD_PK = "combobox_display_field";
	public final static String COMBOBOX_DATASOURCE_TYPE_PK = "combobox_datasource_type";
	public final static String COMBOBOX_DATASOURCE_VALUE_PK = "combobox_datasource_value";
	public final static String LOV_BM_PK = "lov_bm";
	public final static String LOV_VALUE_FIELD_PK = "lov_value_field";
	public final static String LOV_DISPLAY_FIELD_PK = "lov_display_field";

	private IDatabaseServiceFactory databaseServiceFactory;
	private IObjectRegistry objectRegistry;
	
	private  Map<String,String> editor_datatype = new HashMap<String,String>();
	
	private DataSource dataSource;
	
	public BMFlexFieldProvider(IDatabaseServiceFactory databaseServiceFactory, IObjectRegistry objectRegistry) {
		this.databaseServiceFactory = databaseServiceFactory;
		this.objectRegistry = objectRegistry;
	}

//	@Override
	public void prePrepareBusinessModel(BusinessModel model, CompositeMap context) throws Exception {
		boolean customization_enabled = model.getCustomizationenabled();
		if (customization_enabled) {
			/*
			 * This context may be
			 * "<model-service-context BusinessModel="aurora.bm.BusinessModel@e73b917"/>", so need
			 * ServiceThreadLocal.getCurrentThreadContext();
			 */
			CompositeMap fullContext = ServiceThreadLocal.getCurrentThreadContext();
			if (fullContext == null)
				return;
			LoggingContext.getLogger(model.getObjectContext(),this.getClass().getCanonicalName()).log(Level.CONFIG, fullContext.getRoot().toXML());

			CompositeMap flexFieldRecords = getFlexFieldData(model, fullContext);
			if (flexFieldRecords != null) {
				customBM(model, flexFieldRecords,fullContext);
			}
		}
	}

	private void customBM(BusinessModel model,CompositeMap flexFieldRecords,CompositeMap context) throws Exception{
		if(model == null || flexFieldRecords == null || context == null)
			return;
		List<CompositeMap> records = flexFieldRecords.getChilds();
		if(records == null)
			return;
		ILogger contextLogger = LoggingContext.getLogger(context,this.getClass().getCanonicalName());
		
		for( Object obj:records){
			CompositeMap record =(CompositeMap)obj;
			String fieldName = record.getString(FIELD_NAME_PK);
			if(fieldName == null)
				throw BuiltinExceptionFactory.createAttributeMissing(flexFieldRecords.asLocatable(), FIELD_NAME_PK);
			String editorType =  record.getString(EDITOR_TYPE_PK);
			if(editorType == null)
				throw BuiltinExceptionFactory.createAttributeMissing(flexFieldRecords.asLocatable(), EDITOR_TYPE_PK);
			Field field = Field.createField(fieldName.toLowerCase());
			field.setDatabaseType(FLEXFIELD_DEFAULT_DATABASE_TYPE);
			
			String dataType = editor_datatype.get(editorType.toUpperCase());
			if(editorType.equals("NUMBERFIELD") && "Y".equals(record.getString(NUMBER_ALLOWDECIMALS_PK))){
				dataType = editor_datatype.get("NUMBERFIELD_ALLOWDECIMALS");
			}
			field.setDataType(dataType);
			if("COMBOBOX".equalsIgnoreCase(editorType)){
				String combobox_datasource_type = record.getString(COMBOBOX_DATASOURCE_TYPE_PK);
				if(combobox_datasource_type == null)
					throw BuiltinExceptionFactory.createAttributeMissing(flexFieldRecords.asLocatable(), COMBOBOX_DATASOURCE_TYPE_PK);
				String combobox_datasource_value = record.getString(COMBOBOX_DATASOURCE_VALUE_PK);
				if(combobox_datasource_value == null)
					throw BuiltinExceptionFactory.createAttributeMissing(flexFieldRecords.asLocatable(), COMBOBOX_DATASOURCE_VALUE_PK);
				String real_display_field_name = fieldName.toLowerCase()+"_display";
				if("LOOKUP".equalsIgnoreCase(combobox_datasource_type)){
					field.setLookUpCode(record.getString(COMBOBOX_DATASOURCE_VALUE_PK));
					field.setLookUpField(real_display_field_name);
				}else if("BM".equalsIgnoreCase(combobox_datasource_type)){
					String combobox_value_field_name = record.getString(COMBOBOX_VALUE_FIELD_PK);
					String db_display_field_name = record.getString(COMBOBOX_DISPLAY_FIELD_PK);
					addRelationRefField(model,record,combobox_datasource_value.toLowerCase(),combobox_value_field_name,db_display_field_name);
				}
			}else if("LOV".equals(editorType)){
				String lov_bm = record.getString(LOV_BM_PK);
				String lov_value_field = record.getString(LOV_VALUE_FIELD_PK);
				String db_display_field_name = record.getString(LOV_DISPLAY_FIELD_PK);
				addRelationRefField(model,record,lov_bm,lov_value_field,db_display_field_name);
			}
			model.addField(field);
			model.makeReady();
			contextLogger.config("flexField bm:"+model.getObjectContext().toXML());
		}
	}
	private void addRelationRefField(BusinessModel model,CompositeMap record,String joinModelName,String valueFieldName,String dbDisplayFieldName) throws Exception{
		BusinessModel joinModel = databaseServiceFactory.getModelService(joinModelName).getBusinessModel();
		String fieldName = record.getString(FIELD_NAME_PK);
		String real_display_field_name = fieldName.toLowerCase()+"_display";
		
		String relationName = generateRelationName(model,joinModel);
		Relation relation =Relation.createRelation(relationName);
		relation.setJoinType(TYPE_LEFT_OUTTER_JOIN,false);
		relation.setReferenceModel(joinModel.getName());
		Reference reference = Reference.createReference();
		reference.setLocalField(fieldName.toLowerCase());
//		Field[] primaryKeys = model.getPrimaryKeyFields();
//		if(primaryKeys == null)
//			throw BuiltinExceptionFactory.createAttributeMissing(joinModel.getObjectContext().asLocatable(), BusinessModel.SECTION_PRIMARY_KEY);
//		if(primaryKeys.length != 1)
//			throw new RuntimeException("bm:"+joinModel.getName()+" has more than one primaryKey.");
		reference.setForeignField(valueFieldName.toLowerCase());
		
		relation.addReference(reference);
		model.addRelation(relation);
		
		
		Field display_field = joinModel.getField(dbDisplayFieldName.toLowerCase());
		if(display_field == null)
			throw BmBuiltinExceptionFactory.createNamedFieldNotFound(dbDisplayFieldName.toLowerCase(), joinModel.getObjectContext());
		boolean multiLanguage = display_field.getMultiLanguage();
		Field ref_field = null;
		if(multiLanguage){
			ref_field = Field.createField(real_display_field_name+"_id");
			ref_field.setReferenceField(true);
			ref_field.setMultiLanguage(true);
			ref_field.setMultiLanguageDescField(real_display_field_name);
			ref_field.setRelationName(relationName);
			ref_field.setSourceField(dbDisplayFieldName.toLowerCase());
		}else{
			ref_field = Field.createField(real_display_field_name);
			ref_field.setReferenceField(true);
			ref_field.setRelationName(relationName);
			ref_field.setSourceField(dbDisplayFieldName.toLowerCase());
		}
		if(ref_field != null)
			model.addRefField(ref_field);
	}
	
	private String generateRelationName(BusinessModel mainModel,BusinessModel joinModel){
		String joinModelName = joinModel.getName().toLowerCase().replace(".", "_");
		String relationName = "re_"+joinModelName;
		while(mainModel.getRelation(relationName) != null){
			relationName = relationName+"_"+System.currentTimeMillis();
		}
		return relationName;
	}
	

	@Override
	public CompositeMap getFlexFieldData(BusinessModel model, CompositeMap context) {
		String tableName = model.getBaseTable();
		if (tableName == null || "".equals(tableName))
			return null;
		SqlServiceContext ssc = null;
		ResultSet rs_details = null;
		CompositeMap result = new CompositeMap("result");
		try {
			ssc = databaseServiceFactory.createContextWithConnection();

			String flexFieldQuerySql = "select t.table_name, f.field_name, f.editor_type, f.number_allowdecimals,f.combobox_datasource_type,"
					+ " f.combobox_datasource_value," + " f.combobox_value_field," + " f.combobox_display_field," + " f.lov_bm,"
					+ " f.lov_value_field," + " f.lov_display_field" + " from sys_business_objects t, sys_business_object_flexfields f"
					+ " where f.business_object_id = t.object_id" + " and t.enabled_flag = 'Y'" //+ " and f.enabled_flag = 'Y'"
					+ " and t.table_name='" + tableName.toUpperCase() + "'";

			LoggingContext.getLogger(model.getObjectContext(),this.getClass().getCanonicalName()).config("flexFieldQuerySql:" + flexFieldQuerySql);
			ParsedSql stmt = createStatement(flexFieldQuerySql);
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
		return result;
	}

	@Override
	public boolean startup() {
		if (!(databaseServiceFactory instanceof DatabaseServiceFactory))
			throw BuiltinExceptionFactory.createInstanceNotFoundException(this, DatabaseServiceFactory.class);
		((DatabaseServiceFactory) databaseServiceFactory).setGlobalParticipant(this);
		dataSource = (DataSource) objectRegistry.getInstanceOfType(DataSource.class);
		if (dataSource == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(this, DataSource.class);

		init();
		return true;
	}
	
	private void init(){
		editor_datatype.put("TEXTFIELD", "java.lang.String");
		editor_datatype.put("NUMBERFIELD", "java.lang.Long");
		editor_datatype.put("NUMBERFIELD_ALLOWDECIMALS", "java.lang.Double");
		editor_datatype.put("DATEPICKER", "java.util.Date");
		editor_datatype.put("COMBOBOX", "java.lang.String");
		editor_datatype.put("LOV", "java.lang.String");
	}

	@Override
	public void shutdown() {

	}

	ParsedSql createStatement(String sql) {
		ParsedSql stmt = new ParsedSql();
		stmt.parse(sql);
		return stmt;
	}
}
