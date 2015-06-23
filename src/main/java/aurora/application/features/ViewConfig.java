package aurora.application.features;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.OCManager;
import uncertain.proc.ProcedureRunner;
import aurora.service.ServiceInstance;

public class ViewConfig extends AbstractProcedureInvoker{
	
	public static final String CONFIG_NAME = "view-config";
	

	public ViewConfig(OCManager ocManager, IObjectRegistry registry) {
		super(ocManager, registry);
	}
	
	public void doInvoke( ProcedureRunner runner ) throws Exception {
        ServiceInstance svc = ServiceInstance.getInstance(runner.getContext());
        CompositeMap config = svc.getServiceConfigData().getChild(CONFIG_NAME);
        if(config!=null) super.runProcedure(config, runner);
        
    }
    
    public void postCreateView( ProcedureRunner runner ) throws Exception {
        doInvoke(runner);
    }

}
