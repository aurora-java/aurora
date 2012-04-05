package aurora.database.features;

import uncertain.composite.CompositeMap;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.IObjectRegistry;
import aurora.application.features.msg.IMessageStub;
import aurora.application.features.msg.Message;
import aurora.bm.BusinessModel;
import aurora.bm.Field;
import aurora.database.service.BusinessModelServiceContext;

public class BmDmlEvent {
	public final static String SERPRATOR_CHAR="/";
	public final static String TOPIC = "dml_event";
	
	private IObjectRegistry mRegistry;
	public BmDmlEvent(IObjectRegistry registry){
		this.mRegistry = registry;
	}
	public void postExecuteDmlStatement(BusinessModelServiceContext bmsContext)
            throws Exception {
        BusinessModel model = bmsContext.getBusinessModel();
        String baseTable = model.getBaseTable();
        if(baseTable == null)
        	throw new IllegalArgumentException("BusinessModel："+model.getName()+" has no base table, please set it first!");
        Field[] pks = model.getPrimaryKeyFields();
        if(pks == null)
        	throw new IllegalArgumentException("BusinessModel："+model.getName()+" has no primary Key, please set it first!");
        IMessageStub messageStub = (IMessageStub)mRegistry.getInstanceOfType(IMessageStub.class);
        if(messageStub == null)
        	throw BuiltinExceptionFactory.createInstanceNotFoundException(null, IMessageStub.class, this.getClass().getCanonicalName());
        CompositeMap properties = new CompositeMap();
        CompositeMap parameters = bmsContext.getCurrentParameter();
        for(int i=0;i<pks.length;i++){
        	Field pk=pks[i];
        	properties.put(pk.getName(), parameters.get(pk.getName()));
        }
        String text =baseTable.toLowerCase()+SERPRATOR_CHAR+bmsContext.getOperation().toLowerCase();
        Message message = new Message(text,properties);
        messageStub.send(TOPIC,message,bmsContext.getObjectContext());
    }
}
