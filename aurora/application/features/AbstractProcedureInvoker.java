/*
 * Created on 2009-9-7 下午01:51:01
 * Author: Zhou Fan
 */
package aurora.application.features;

import java.sql.SQLException;
import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.OCManager;
import uncertain.proc.Procedure;
import uncertain.proc.ProcedureConfigManager;
import uncertain.proc.ProcedureRunner;
import aurora.database.service.SqlServiceContext;

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
    ProcedureRunner	    mRunner;

    
    void clearUp() throws SQLException{
    	if(mRunner!=null)
    		SqlServiceContext.createSqlServiceContext(mRunner.getContext()).freeConnection();
    }

    public void runProcedure(CompositeMap config, ProcedureRunner parent_runner)
            throws Exception {
        try{
            CompositeMap proc_config = ProcedureConfigManager.createConfigNode("procedure");
            proc_config.copy(config);
            Procedure proc = (Procedure)mOcManager.createObject(proc_config);
            ProcedureRunner runner = parent_runner.spawn(proc);
            runner.run();
            runner.checkAndThrow();
            mRunner=runner;
        } finally{
            clearUp();
        }
    }
}
