/*
 * Created on 2009-9-7 下午02:01:20
 * Author: Zhou Fan
 */
package aurora.application.features;

import uncertain.composite.CompositeMap;
import uncertain.event.Configuration;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.ISingleton;
import uncertain.ocm.OCManager;
import uncertain.proc.IFeature;
import uncertain.proc.ProcedureRunner;
import aurora.service.ServiceInstance;

public class InitProcedureInvoker extends AbstractProcedureInvoker implements IFeature {
    
    String proc_element_name;

    public InitProcedureInvoker( OCManager ocManager, IObjectRegistry registry ) {
        super(ocManager, registry);
    }
    
    public int attachTo(CompositeMap config, Configuration procConfig ){
        proc_element_name = config.getName();
        //return IFeature.NORMAL;
        return IFeature.NO_CHILD_CONFIG;
    }
    
    public void doInvoke( ProcedureRunner runner ) throws Exception {
        ServiceInstance svc = ServiceInstance.getInstance(runner.getContext());
        CompositeMap config = svc.getServiceConfigData().getChild(proc_element_name);
        if(config!=null)
            super.runProcedure(config, runner);        
    }
    
    public void onCreateModel( ProcedureRunner runner ) throws Exception {
        doInvoke(runner);
    }
    
    public void onInvokeService( ProcedureRunner runner ) throws Exception {
        doInvoke(runner);
    }
}
