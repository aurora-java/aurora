package aurora.application.features.cstm.bm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import aurora.application.AuroraApplication;
import aurora.bm.BusinessModel;
import aurora.bm.DataFilter;
import aurora.bm.Field;
import aurora.bm.QueryField;
import aurora.service.ServiceThreadLocal;
import uncertain.composite.CompositeMap;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;

public class LovBMCustomSourceCode {

	public static final String KEY_RECORD_ID = "record_id";
	public static final String KEY_BM_SELECT_VALUE = "bm_select_value";
	public static final String KEY_BM_DATA_SOURCE = "bm_data_source";
	public static final String KEY_BM_WHERE_CLAUSE = "bm_where_clause";
	public static final String KEY_BM_ORDER_BY = "bm_order_by";
	public static final String KEY_BM_QUERY_CONDITION = "bm_query_condition";
	
	public static final String FIELD_SEPERATOR=",";
	
	
	public static void custom(BusinessModel bm, CompositeMap customRecords,IObjectRegistry registry){
		
		if(registry == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(null, IObjectRegistry.class, LovBMCustomSourceCode.class.getCanonicalName());
		if (bm == null || customRecords == null || customRecords.getChilds() == null) {
			return;
		}
		for(Iterator<CompositeMap> it = customRecords.getChildIterator();it.hasNext();){
			CompositeMap record = (CompositeMap)it.next();
			String bm_data_source = record.getString(KEY_BM_DATA_SOURCE);
			if(bm_data_source != null){
				bm.setBaseTable(bm_data_source);
			}
			String bm_order_by = record.getString(KEY_BM_ORDER_BY);
			if(bm_order_by != null){
				bm.setDefaultOrderby(bm_order_by);
			}
			String bm_select_value = record.getString(KEY_BM_SELECT_VALUE);
			if(bm_select_value != null){
				String[] forDisplayfields = bm_select_value.split(FIELD_SEPERATOR);
				Field[] fields = bm.getFields();
				for(int i=0;i<fields.length;i++){
					fields[i].setForDisplay(false);
				}
				for(int i=0;i<forDisplayfields.length;i++){
					String fieldName = forDisplayfields[i];
					Field field= bm.getField(fieldName);
					if(field == null){
						field = Field.createField(fieldName);
						bm.addField(field);
					}
					field.setForDisplay(true);
				}
			}
			String bm_where_clause = record.getString(KEY_BM_WHERE_CLAUSE);
			if(bm_where_clause != null){
				CompositeMap dataFilters = new CompositeMap(DataFilter.KEY_DATA_FILTERS);
				CompositeMap dataFilter = new CompositeMap("bm",AuroraApplication.AURORA_BUSINESS_MODEL_NAMESPACE,"data-filter");
				dataFilter.put(DataFilter.KEY_NAME,"cust_query");
				dataFilter.put(DataFilter.KEY_ENFORCE_OPERATIONS,"query");
				dataFilter.put(DataFilter.KEY_EXPRESSION, bm_where_clause);
				dataFilters.addChild(dataFilter);
				bm.setDataFilters(dataFilters);
			}
			String bm_query_condition = record.getString(KEY_BM_QUERY_CONDITION);
			if(bm_query_condition != null){
				String[] forQueryfields = bm_query_condition.split(FIELD_SEPERATOR);
				Field[] fields = bm.getFields();
				for(int i=0;i<fields.length;i++){
					fields[i].setForQuery(false);
				}
				QueryField[] queryFields = bm.getQueryFieldsArray();
				List<String> queryFieldNameList = new ArrayList<String>();
				if(queryFields != null){
					for(int i=0;i<queryFields.length;i++){
						//just support field,not name
						String field = queryFields[i].getField();
						if(field != null)
							queryFieldNameList.add(field);
					}
				}
				for(int i=0;i<forQueryfields.length;i++){
					String fieldName = forQueryfields[i];
					Field field= bm.getField(fieldName);
					if(field == null){
						field = Field.createField(fieldName);
						bm.addField(field);
					}
					field.setForQuery(true);
					if(!queryFieldNameList.contains(fieldName)){
						CompositeMap queryField = new CompositeMap("bm",AuroraApplication.AURORA_BUSINESS_MODEL_NAMESPACE,"query-field");
						queryField.put(QueryField.KEY_FIELD, fieldName);
						queryField.put(QueryField.KEY_QUERY_OPERATOR, "like");
						bm.addQueryField(queryField);
					}
				}
			}
		}
		CompositeMap context = ServiceThreadLocal.getCurrentThreadContext();
		if (context == null)
			LoggingContext.getLogger(context).log(Level.CONFIG, "customBM "+bm.getName()+":"+bm.getObjectContext().toXML());
	}
	
}
