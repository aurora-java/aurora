package aurora.application.action;

import aurora.events.E_DetectProcedure;
import aurora.service.IService;
import aurora.service.ServiceController;
import aurora.service.controller.ControllerProcedures;
import uncertain.event.EventModel;
import uncertain.proc.ProcedureRunner;

public class FileUpload implements E_DetectProcedure{
	
	
	public void onDoUpload(ProcedureRunner runner){
		System.out.println("onDoUpload ...");		
	}

	public int onDetectProcedure(IService service) throws Exception {
		ServiceController controller = ServiceController.createServiceController(service.getServiceContext().getObjectContext());
		controller.setProcedureName(ControllerProcedures.UPLOAD_SERVICE);
		return EventModel.HANDLE_NORMAL;
	}
	
}
