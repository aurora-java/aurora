/*
 * Created on 2011-7-20 上午01:18:56
 * $Id$
 */
package aurora.database.actions;

import java.util.logging.Level;

import javax.transaction.UserTransaction;

import uncertain.composite.CompositeMap;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.OCManager;
import uncertain.proc.Procedure;
import uncertain.proc.ProcedureRunner;
import aurora.database.DatabaseConstant;
import aurora.database.service.SqlServiceContext;
import aurora.transaction.ITransactionService;
import aurora.transaction.UserTransactionImpl;

public class Transaction extends Procedure {
    
    IObjectRegistry     mRegistry;

    public Transaction(OCManager om, IObjectRegistry reg) {
        super(om);
        this.mRegistry = reg;
    }

    public void run(ProcedureRunner runner) throws Exception {
        CompositeMap context = runner.getContext();
        //SqlServiceContext sql_context = SqlServiceContext.createSqlServiceContext(context);
        ILogger logger = LoggingContext.getLogger(context, DatabaseConstant.AURORA_DATABASE_LOGGING_TOPIC);
        
        UserTransaction trans = null;
        ITransactionService ts = (ITransactionService) mRegistry
                .getInstanceOfType(ITransactionService.class);
        if (ts == null)
            throw BuiltinExceptionFactory.createInstanceNotFoundException(this, ITransactionService.class);
        
        trans = ts.getUserTransaction();
        if(trans instanceof UserTransactionImpl){
            ((UserTransactionImpl) trans).setContext(context);
        }
        try {
            trans.begin();
            logger.config("Transaction begins");
            super.run(runner);
            try{
                trans.commit();
                logger.config("Transaction commit");
            }catch(Exception ex){
                logger.log(Level.SEVERE, "Can't execute commit", ex);
            }
        }catch(Throwable thr){
            try{
                trans.rollback();
                logger.config("Transaction rollback due to exception");
            }catch(Exception ex){
                logger.log(Level.SEVERE, "Can't execute rollback", ex);
            }
            runner.throwException(thr);
        }finally{
            ts.stop();
        }
    }
    
    

}
