package aurora.database.features;

import java.util.Iterator;
import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.IObjectRegistry;
import aurora.application.features.ILookupCodeProvider;
import aurora.bm.BusinessModel;
import aurora.bm.Field;
import aurora.bm.Relation;
import aurora.database.IResultSetConsumer;
import aurora.database.profile.IDatabaseFactory;
import aurora.service.ServiceThreadLocal;

public class CacheBasedLookUpField {

	private IDatabaseFactory factory;
	private IObjectRegistry mRegistry;
	private ILookupCodeProvider lookupProvider;
	public CacheBasedLookUpField(IDatabaseFactory factory,IObjectRegistry registry) {
		this.factory = factory;
		this.mRegistry = registry;
	}

	public void onPrepareBusinessModel(BusinessModel model) {
		lookupProvider = (ILookupCodeProvider) mRegistry.getInstanceOfType(ILookupCodeProvider.class);
		if(lookupProvider==null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(null, ILookupCodeProvider.class,
					this.getClass().getCanonicalName());
	}
	public void postFetchResultSet(BusinessModel model, IResultSetConsumer consumer) throws Exception {
		Object result = consumer.getResult();
		if (!(result instanceof CompositeMap))
			return;
		Field[] fields = model.getFields();
		if(fields == null)
			return;
		Field field = null;
		Field refield = null;
		String alias = model.getAlias();
		String idFieldName = null;
		CompositeMap dbProperties = factory.getProperties();
		CompositeMap currentContext = new CompositeMap();
		if(ServiceThreadLocal.getCurrentThreadContext()!= null)
			currentContext = ServiceThreadLocal.getCurrentThreadContext();
		String language = TextParser.parse("${" + dbProperties.getString("language_path") + "}",currentContext);
		for (int i = 0, l = fields.length; i < l; i++) {
			field = fields[i];
			if (field.isReferenceField()) {
				CompositeMap cmap = (CompositeMap) field.getReferredField().getObjectContext().clone();
				cmap.copy(field.getObjectContext());
				refield = Field.getInstance(cmap);// field.getReferredField();
				Relation relation = model.getRelation(field.getRelationName());
				alias = relation.getReferenceAlias();
				if (alias == null)
					alias = field.getRelationName();
				idFieldName = field.getSourceField();
			} else {
				refield = field;
				idFieldName = refield.getName();
			}
			String lookupfiled = refield.getLookUpField();
			String lookupcode = refield.getLookUpCode();
			if(lookupfiled == null ||lookupcode==null)
				continue;
			addCodeValueName((CompositeMap) result, lookupfiled, lookupcode,idFieldName,language, field.getExpression());
		}
	}
	private void addCodeValueName(CompositeMap data, String lookupfiled, String lookupcode,String srcFieldName,String language, String expression) throws Exception {
		// 表达式未用
		if (data == null)
			return;
		if (data.getChilds() == null) {
			addRecordCodeValueName(data,lookupfiled, lookupcode, srcFieldName, language);
			return;
		}
		for (Iterator<CompositeMap> it = data.getChildIterator(); it.hasNext();) {
			CompositeMap record = it.next();
			addRecordCodeValueName(record, lookupfiled,lookupcode,srcFieldName, language);
		}
	}

	private void addRecordCodeValueName(CompositeMap data, String lookupfiled, String lookupcode, String srcFieldName,String language) throws Exception {
		List result = lookupProvider.getLookupList(language, lookupcode);
		if(result == null)
			return;
		String code_value = data.getString(srcFieldName);
		if(code_value==null)
			return;
		for(Object obj:result){
			CompositeMap record = (CompositeMap)obj; 
			if(code_value.equals(record.getString("code_value"))){
				data.put(lookupfiled, record.getString("code_value_name"));
			}
		}
	}
}
