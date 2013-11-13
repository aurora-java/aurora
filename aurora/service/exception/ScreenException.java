package aurora.service.exception;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.ocm.ISingleton;
import uncertain.proc.ProcedureRunner;
import aurora.service.ServiceContext;
import aurora.service.ServiceInstance;
import aurora.service.http.HttpServiceInstance;

public class ScreenException implements ISingleton {

	public void onCreateErrorResponse(ProcedureRunner runner) throws Exception {
		Throwable thr = runner.getException();
        if(thr == null) return;
        ServiceContext   context = (ServiceContext)DynamicObject.cast(runner.getContext(), ServiceContext.class);
        CompositeMap cm = context.getObjectContext();
		HttpServiceInstance svc = (HttpServiceInstance) ServiceInstance.getInstance(cm);
		CompositeMap error = context.getError();
        if(error != null) {
        	String error_message = error.getString("message");
        	String url = "/error.screen?msg="+error_message;
		    if(url!=null){
		        String uri = svc.getRequest().getRequestURI();
		        if(uri.indexOf(url)<0) {
		        		svc.getRequest().getRequestDispatcher(url).forward(svc.getRequest(), svc.getResponse());
		        }
		    }        	
        }else {
        	throw (Exception)thr;
        }
	}
}
