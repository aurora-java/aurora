/*
 * Created on 2011-7-23 下午04:08:55
 * $Id$
 */
package aurora.service;

import java.sql.SQLException;

import javax.transaction.UserTransaction;

import uncertain.composite.CompositeMap;
import uncertain.core.IContainer;
import uncertain.event.Configuration;
import uncertain.proc.IProcedureManager;
import aurora.database.service.SqlServiceContext;
import aurora.transaction.ITransactionService;
import aurora.transaction.UserTransactionImpl;

public class ServiceFactoryImpl implements IServiceFactory {

    IContainer          mContainer;
    ITransactionService mTransactionService;
    IProcedureManager mProcedureManager;
    ThreadLocal mTransactionLocal = new ThreadLocal();

    public ServiceFactoryImpl(IContainer container, ITransactionService ts, IProcedureManager pm) {
        mContainer = container;
        mTransactionService = ts;
        mProcedureManager = pm;
    }

    public IService createService(CompositeMap context ){
        return createService(null, context);
    }
    
    public IService createService(String service_name, CompositeMap context )  {
        ServiceInstance inst = new ServiceInstance(service_name,
                mProcedureManager);
        inst.setContextMap(context);
        inst.setName(service_name);
        Configuration config = (Configuration)mContainer.getEventDispatcher();
        if(config!=null)
            inst.setRootConfig(config);
        return inst;
    }

    // TODO NEED REFACTOR! Use common resource management framework later
    public void beginService(CompositeMap context) {
        UserTransaction trans = mTransactionService.getUserTransaction();
        try {
            trans.begin();
            if (trans instanceof UserTransactionImpl) {
                ((UserTransactionImpl) trans).setContext(context);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        mTransactionLocal.set(trans);

    }

    // TODO NEED REFACTOR! Use common resource management framework later
    public void finishService(CompositeMap context) {
        try {
            ServiceContext ctx = ServiceContext.createServiceContext(context);
            UserTransaction trans = (UserTransaction) mTransactionLocal.get();
            if (trans != null) {
                if(ctx.isSuccess()){
                    trans.commit();
                }else{
                    trans.rollback();
                }
            }
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }finally {
            mTransactionLocal.remove();
            SqlServiceContext ctx = SqlServiceContext.createSqlServiceContext(context);
            try{
                ctx.freeConnection();
            }catch(SQLException ex){
                ex.printStackTrace(System.err);
            }
        }
    }

}
