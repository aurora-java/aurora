package aurora.database.features;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.core.ConfigurationError;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.IObjectRegistry;
import aurora.bm.BusinessModel;
import aurora.bm.Field;
import aurora.database.IResultSetConsumer;
import aurora.database.profile.IDatabaseFactory;
import aurora.service.ServiceThreadLocal;

public class CacheBasedMultiLanguageDisplay {
	private ICacheBasedMultiLanguageProvider cacheProvider;
	private CompositeMap dbProperties;

	public CacheBasedMultiLanguageDisplay(IObjectRegistry objectRegistry, IDatabaseFactory databaseFactory) throws IOException {
		cacheProvider = (ICacheBasedMultiLanguageProvider) objectRegistry.getInstanceOfType(ICacheBasedMultiLanguageProvider.class);
		if (cacheProvider == null) 
			throw BuiltinExceptionFactory.createInstanceNotFoundException(null, ICacheBasedMultiLanguageProvider.class,
					CacheBasedMultiLanguageDisplay.class.getCanonicalName());
		dbProperties = databaseFactory.getProperties();
		if (dbProperties == null)
			throw new ConfigurationError("Database Properties undifined");
	}

	public void onPrepareBusinessModel(BusinessModel model) {
		Field[] fields = model.getFields();
		Field field = null;
		Field refield = null;
		String multiLanguageDescField = null;
		for (int i = 0, l = fields.length; i < l; i++) {
			field = fields[i];
			if (field.isReferenceField()) {
				CompositeMap cmap = (CompositeMap) field.getReferredField().getObjectContext().clone();
				cmap.copy(field.getObjectContext());
				refield = Field.getInstance(cmap);// field.getReferredField();
			} else {
				refield = field;
			}
			if (refield.getMultiLanguage()) {
				multiLanguageDescField = refield.getMultiLanguageDescField();
				for (int j = 0; j < l; j++) {
					Field f = fields[j];
					if (f.getName().equalsIgnoreCase(multiLanguageDescField)) {
						if (!f.isExpression()) {
							/*
							 * TODO query和select的区别							 * 
							 */
							f.setForQuery(false);
							f.setForSelect(false);
							break;
						}
					}
				}
			}
		}
		model.makeReady();
	}

	public void postFetchResultSet(BusinessModel model, IResultSetConsumer consumer) throws SQLException {
		if (consumer == null)
			return;
		Object result = consumer.getResult();
		if (!(result instanceof CompositeMap))
			return;
		Field[] fields = model.getFields();
		Field field = null;
		Field refield = null;
		String idFieldName = null;
		String multiLanguageDescField = null;
		String language = TextParser.parse("${" + dbProperties.getString("language_path") + "}",
				ServiceThreadLocal.getCurrentThreadContext());
		for (int i = 0, l = fields.length; i < l; i++) {
			field = fields[i];
			idFieldName = field.getName();
			if (field.isReferenceField()) {
				CompositeMap cmap = (CompositeMap) field.getReferredField().getObjectContext().clone();
				cmap.copy(field.getObjectContext());
				refield = Field.getInstance(cmap);// field.getReferredField();
//				idFieldName = field.getSourceField();
			} else {
				refield = field;
			}
			if (refield.getMultiLanguage()) {
				multiLanguageDescField = refield.getMultiLanguageDescField();
				addDescription((CompositeMap) result, multiLanguageDescField, idFieldName, language, field.getExpression());
			}
		}
	}

	private void addDescription(CompositeMap data, String descriptionFieldName, String desp_id_column, String language, String expression) {
		// 表达式未用
		if (data == null)
			return;
		if (data.getChilds() == null) {
			addRecordDescription(data, descriptionFieldName, desp_id_column, language);
			return;
		}
		for (Iterator<CompositeMap> it = data.getChildIterator(); it.hasNext();) {
			CompositeMap record = it.next();
			addRecordDescription(record, descriptionFieldName, desp_id_column, language);
		}
	}

	private void addRecordDescription(CompositeMap data, String descriptionFieldName, String desp_id_column, String language) {
		Integer description_id = data.getInt(desp_id_column);
		if (description_id == null)
			return;
		data.put(descriptionFieldName, cacheProvider.getDescription(String.valueOf(description_id), language));
	}
}
