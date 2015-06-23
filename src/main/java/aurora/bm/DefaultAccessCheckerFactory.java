/*
 * Created on 2010-12-10 上午11:15:10
 * $Id$
 */
package aurora.bm;

/**
 * Creates BM Access checker by perform database query
 */
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import uncertain.composite.CompositeMap;
import uncertain.core.IGlobalInstance;
import uncertain.ocm.IObjectRegistry;
import aurora.database.FetchDescriptor;
import aurora.database.service.BusinessModelService;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.SqlServiceContext;
import aurora.service.ServiceThreadLocal;

public class DefaultAccessCheckerFactory implements IBusinessModelAccessCheckerFactory, IGlobalInstance {
	private DatabaseServiceFactory factory;
	/**
	 * @param serviceFactory
	 */
	public DefaultAccessCheckerFactory(DatabaseServiceFactory serviceFactory) {
		super();
		mServiceFactory = serviceFactory;
	}

	DatabaseServiceFactory mServiceFactory;
	String mCheckServiceName;
	BusinessModel mCheckServiceModel;
	IObjectRegistry registry;

	private String optionField;
	private String valueField;
	private String valueFlag;
	private String[] optionFieldarryay;
	private String[] valueFieldarryay;

	public String getValueField() {
		return valueField;
	}

	public void setValueField(String valueField) {
		this.valueField = valueField;
		this.valueFieldarryay = valueField.split(",");
	}

	public String getValueFlag() {
		return valueFlag;
	}

	public void setValueFlag(String valueFlag) {
		this.valueFlag = valueFlag;
	}

	public String getOptionField() {
		return optionField;
	}

	public void setOptionField(String optionField) {
		this.optionField = optionField;
		this.optionFieldarryay = optionField.split(",");
	}

	public IBusinessModelAccessChecker getChecker(String model_name,
			CompositeMap session_context) throws Exception {
		Set hs = new HashSet();
		SqlServiceContext context = factory.createContextWithConnection();
		try{
	//		CompositeMap context = ServiceThreadLocal.getCurrentThreadContext();
			BusinessModelService service = mServiceFactory.getModelService(getCheckServiceName(),context.getObjectContext());
			session_context.put("bm_name", model_name);
			CompositeMap resultMap = service.queryAsMap(session_context,FetchDescriptor.fetchAll());
			if (resultMap != null) {
				Iterator it = resultMap.getChildIterator();
				if(it==null)
					return new DefaultAccessChecker(hs);
				while (it.hasNext()) {
					CompositeMap record = (CompositeMap) it.next();
					for (int i = 0, l = optionFieldarryay.length; i < l; i++) {
						if (record.getString(optionFieldarryay[i]).equals(
								getValueFlag())) {
							hs.add(valueFieldarryay[i]);
						}
					}
				}
			}
			return new DefaultAccessChecker(hs);
		} finally {
            if (context != null)
                context.freeConnection();
        }
		
	}

	public String getCheckServiceName() {
		return mCheckServiceName;
	}

	public void setCheckServiceName(String checkServiceName) {
		mCheckServiceName = checkServiceName;
	}

	public void onInitialize() throws Exception {
		factory = (DatabaseServiceFactory)registry.getInstanceOfType(DatabaseServiceFactory.class);
		mServiceFactory = (DatabaseServiceFactory) registry
				.getInstanceOfType(DatabaseServiceFactory.class);
	}

	public DefaultAccessCheckerFactory(IObjectRegistry registry) {
		this.registry = registry;

	}
}
