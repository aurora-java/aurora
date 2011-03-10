/*
 * Created on 2008-6-11
 */
package aurora.database.actions;

import java.io.File;
import java.util.Iterator;

import uncertain.composite.CompositeMap;
import uncertain.proc.ProcedureRunner;
import aurora.application.features.ExcelFactoryImpl;
import aurora.database.FetchDescriptor;
import aurora.database.actions.config.ActionConfigManager;
import aurora.database.service.BusinessModelService;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.ServiceOption;

public class DataImport extends AbstractModelAction {

	public DataImport(DatabaseServiceFactory svcFactory) {
		super(svcFactory);
	}

	public void run(ProcedureRunner runner) throws Exception {

		CompositeMap cm = ExcelFactoryImpl.extractionExcel(new File(
				"C:\\yuchai\\so.xls"));
		CompositeMap data = ExcelFactoryImpl.transactionModel(cm, "data");

		runner.getContext().addChild(data);
//		String object_name = "expm.dataimport";
//		BusinessModelService service = this.getServiceFactory()
//				.getModelService(object_name);
//		System.out.println(service.getSql("Execute"));
//		if (data.getChilds() != null) {
//			Iterator it = data.getChildIterator();
//			while (it.hasNext()) {
//				CompositeMap child = (CompositeMap) it.next();
//				service.execute(child);
//			}
//		}

	}

}
