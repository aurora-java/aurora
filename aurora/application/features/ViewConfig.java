package aurora.application.features;

import aurora.service.ServiceInstance;
import uncertain.composite.CompositeMap;
import uncertain.event.Configuration;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.OCManager;
import uncertain.proc.IFeature;
import uncertain.proc.ProcedureRunner;

public class ViewConfig extends AbstractProcedureInvoker{
	
	private static final String CONFIG_NAME = "view-config";

	public ViewConfig(OCManager ocManager, IObjectRegistry registry) {
		super(ocManager, registry);
	}
	
	public void doInvoke( ProcedureRunner runner ) throws Exception {
        ServiceInstance svc = ServiceInstance.getInstance(runner.getContext());
        CompositeMap config = svc.getServiceConfigData().getChild(CONFIG_NAME);
        if(config!=null)
            super.runProcedure(config, runner);   
//        CompositeMap context = runner.getContext();
//        System.out.println(config.toXML()); 
    }
    
    public void postCreateView( ProcedureRunner runner ) throws Exception {
        doInvoke(runner);
    }
}
