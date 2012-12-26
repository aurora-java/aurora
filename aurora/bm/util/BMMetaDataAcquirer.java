package aurora.bm.util;

import aurora.bm.Field;
import aurora.bm.IModelFactory;
import aurora.service.ServiceContext;
import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

public class BMMetaDataAcquirer extends AbstractEntry {
	String model;
	String rootPath;
	IModelFactory factory;

	public BMMetaDataAcquirer(IModelFactory factory) {
		this.factory = factory;
	}

	public void run(ProcedureRunner runner) throws Exception {
		CompositeMap context = runner.getContext();
		ServiceContext service = ServiceContext.createServiceContext(context);		
		Field[] fields = factory.getModel(TextParser.parse(this.getModel(), context))
				.getFields();
		CompositeMap fieldMap = new CompositeMap();
		CompositeMap record;
		for (Field field : fields) {
			if (field.isReferenceField()) {
				record = (CompositeMap)field.getReferredField().getObjectContext().clone();
			} else {
				record = (CompositeMap)field.getObjectContext().clone();
			}
			record.setName("record");
			fieldMap.addChild(record);
		}
		service.getModel().putObject(
				TextParser.parse(this.getRootPath(), context), fieldMap, true);
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

}
