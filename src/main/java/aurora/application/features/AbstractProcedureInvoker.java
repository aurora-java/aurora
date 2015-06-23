/*
 * Created on 2009-9-7 下午01:51:01
 * Author: Zhou Fan
 */
package aurora.application.features;
import aurora.service.ServiceContext;
import uncertain.composite.CompositeMap;
import uncertain.logging.ILogger;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.OCManager;
import uncertain.proc.Procedure;
import uncertain.proc.ProcedureConfigManager;
import uncertain.proc.ProcedureRunner;
/**
 * Base class for feature that invoke a procedure at certain action point
 */
public abstract class AbstractProcedureInvoker {
    
    /**
     * @param ocManager
     */
    public AbstractProcedureInvoker(OCManager ocManager, IObjectRegistry registry ) {
        mOcManager = ocManager;
        mRegistry = registry;
    }

    OCManager           mOcManager;
    IObjectRegistry     mRegistry;   
    public void runProcedure(CompositeMap config, ProcedureRunner parent_runner)
            throws Exception {
        CompositeMap proc_config = ProcedureConfigManager.createConfigNode("procedure");
        proc_config.copy(config);
        proc_config.setSourceFile(config.getSourceFile());
        /*
        Procedure proc = (Procedure)mOcManager.createObject(proc_config);   
        parent_runner.call(proc);
        parent_runner.checkAndThrow();
        */
        Procedure proc = (Procedure)mOcManager.createObject(proc_config);        
        ProcedureRunner runner = parent_runner.spawn(proc);
        runner.run();
        //ILogger logger = parent_runner.getLogger();
        ServiceContext sc = ServiceContext.createServiceContext(parent_runner.getContext());
        runner.checkAndThrow();
        if(!sc.isSuccess()){
            //logger.severe("success is false");
            parent_runner.stop();
        }        
    }
}
