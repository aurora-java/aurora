package aurora.database.features;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import aurora.application.features.ILookupCodeProvider;
import aurora.bm.BusinessModel;
import aurora.bm.Field;
import aurora.database.profile.IDatabaseFactory;
import aurora.database.service.SqlServiceContext;
import aurora.i18n.IMessageProvider;

public class LookUpField {

	private IDatabaseFactory factory;
	private IObjectRegistry mRegistry;

	public LookUpField(IDatabaseFactory factory,IObjectRegistry registry) {
		this.factory = factory;
		this.mRegistry = registry;
	}

	public void onPrepareBusinessModel(BusinessModel model) {
		ILookupCodeProvider lookupProvider = (ILookupCodeProvider) mRegistry.getInstanceOfType(ILookupCodeProvider.class);
//		String type = lookupProvider.getLookupType();
//		if("sql".equalsIgnoreCase(type)){
			Field[] fields = model.getFields();
			List lookupfields = new ArrayList();
			if (fields != null) {
				int len = fields.length;
				for (int i = 0; i < len; i++) {
					Field field = fields[i];
					String lookupfiled = field.getLookUpField();
					String lookupcode = field.getLookUpCode();
					if (lookupfiled != null && lookupcode != null) {
						lookupfields.add(field);
					}
				}
				Iterator it = lookupfields.iterator();
				while (it.hasNext()) {
					Field field = (Field) it.next();
					String lookupfiled = field.getLookUpField();
					String name = field.getName();
					String lookupcode = field.getLookUpCode();
					String alias = model.getAlias();
					String value = alias != null ? alias + "." + name : name;
					boolean h = false;
					if (lookupfiled != null && lookupcode != null) {
						for (int i = 0; i < len; i++) {
							Field f = fields[i];
							if (lookupfiled.equals(f.getName())) {
								h = true;
								String express = f.getExpression();
								if (express == null) {
									f.setExpression(createExpression(lookupcode,value));
								}
								break;
							}
						}
						if (!h) {
							Field lfield = Field.createField(field.getLookUpField());
							lfield.setPrompt(field.getPrompt());
							lfield.setRequired(field.getRequired());
							lfield.setForInsert(false);
							lfield.setForUpdate(false);
							lfield.setExpression(createExpression(lookupcode,value));
							model.addField(lfield);
						}
					}
				}
			}
//		}
	}

	public void postFetchResultSet(SqlServiceContext context,BusinessModel model) throws Exception {
		ILookupCodeProvider lookupProvider = (ILookupCodeProvider) mRegistry.getInstanceOfType(ILookupCodeProvider.class);
		String type = lookupProvider.getLookupType();
		if("cache".equalsIgnoreCase(type)){
			
		}
	}

	private String createExpression(String code, String name) {
		
		String sqltemplate = null, sql = null;
		CompositeMap properties = this.factory.getProperties();
		if (properties != null) {
			CompositeMap lookupmap = properties.getChild("lookup-code");
			if (lookupmap != null)
				sqltemplate = lookupmap.getString("lookupsql");
		}
		if (sqltemplate != null) {
			MessageFormat mf = new MessageFormat(sqltemplate);
			sql = mf.format(new Object[] { code, name });
		}
		return sql != null ? sql
				: "(select code_value_name from sys_code_values_v where code = '"
						+ code + "' and code_value=to_char(" + name + "))";
	}
}
