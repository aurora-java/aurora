/*
 * Created on 2009-9-7 下午01:51:01
 * Author: Zhou Fan
 */
package aurora.application.features;

import java.sql.Connection;

import javax.sql.DataSource;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.OCManager;
import uncertain.proc.Procedure;
import uncertain.proc.ProcedureConfigManager;
import uncertain.proc.ProcedureRunner;
import aurora.database.DBUtil;
import aurora.service.ServiceContext;

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
    DataSource          mDataSource;
    Connection          mConnection;
    
    /** @todo Use JTA instead */
    void beginTransaction( ServiceContext context ) throws Exception {
        mConnection = (Connection)context.getInstanceOfType(Connection.class);
        if(mConnection==null){
            mDataSource = (DataSource)mRegistry.getInstanceOfType(DataSource.class);
            if(mDataSource==null)
                throw new IllegalStateException("No DataSource instance configured in engine");
            mConnection = mDataSource.getConnection();
            context.setInstanceOfType(Connection.class, mConnection);
        }
    }
    
    void rollbackTransaction( ServiceContext context ) throws Exception {
        if(mConnection!=null)
            mConnection.rollback();
    }
    
    void commitTransaction( ServiceContext context ) throws Exception {
        if(mConnection!=null)
            mConnection.commit();
    }
    
    void clearUp(){
        if(mConnection!=null)
            DBUtil.closeConnection(mConnection);
    }

    public void runProcedure(CompositeMap config, ProcedureRunner parent_runner)
            throws Exception {
        ServiceContext svc_context = ServiceContext.createServiceContext(parent_runner.getContext());
        beginTransaction(svc_context);
        try{
            CompositeMap proc_config = ProcedureConfigManager.createConfigNode("procedure");
            proc_config.copy(config);
            Procedure proc = (Procedure)mOcManager.createObject(proc_config);
            ProcedureRunner runner = parent_runner.spawn(proc);
            runner.run();
            if(runner.getException()!=null){
                rollbackTransaction(svc_context);
            }
            runner.checkAndThrow();
            commitTransaction(svc_context);
        } finally{
            clearUp();
        }
    }

}
