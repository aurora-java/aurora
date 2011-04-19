/*
 * Created on 2011-4-11 下午07:18:21
 * $Id$
 */
package aurora.transaction;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import javax.transaction.xa.XAResource;

import uncertain.core.IGlobalInstance;

public class DummyTransactionService implements ITransactionService, IGlobalInstance {
    
    static final DummyTransactionManager TM = new DummyTransactionManager();
    static final DummyTransaction TS = new DummyTransaction();
    
    public static class DummyTransaction implements Transaction, UserTransaction {

        public void commit() throws RollbackException, HeuristicMixedException,
                HeuristicRollbackException, SecurityException,
                IllegalStateException, SystemException {
           
        }

        public boolean delistResource(XAResource arg0, int arg1)
                throws IllegalStateException, SystemException {
            return true;
        }

        public boolean enlistResource(XAResource arg0)
                throws RollbackException, IllegalStateException,
                SystemException {
            return true;
        }

        public int getStatus() throws SystemException {
            return 0;
        }

        public void registerSynchronization(Synchronization arg0)
                throws RollbackException, IllegalStateException,
                SystemException {
            
        }

        public void rollback() throws IllegalStateException, SystemException {
            
        }

        public void setRollbackOnly() throws IllegalStateException,
                SystemException {
            
        }

        public void begin() throws NotSupportedException, SystemException {
            
        }

        public void setTransactionTimeout(int t) throws SystemException {
            
        }
        
    }
    
    public static class DummyTransactionManager implements TransactionManager {
        
        public DummyTransactionManager(){
            
        }

        public void begin() throws NotSupportedException, SystemException {
            
        }

        public void commit() throws RollbackException, HeuristicMixedException,
                HeuristicRollbackException, SecurityException,
                IllegalStateException, SystemException {
            
        }

        public int getStatus() throws SystemException {
            return 0;
        }

        public Transaction getTransaction() throws SystemException {
            return TS;
        }

        public void resume(Transaction arg0)
                throws InvalidTransactionException, IllegalStateException,
                SystemException {
            
        }

        public void rollback() throws IllegalStateException, SecurityException,
                SystemException {
            
        }

        public void setRollbackOnly() throws IllegalStateException,
                SystemException {
            
        }

        public void setTransactionTimeout(int t) throws SystemException {
            
        }

        public Transaction suspend() throws SystemException {
            return TS;
        }
    }

    public TransactionManager getTransactionManager() {
        return TM;
    }

    public UserTransaction getUserTransaction() {
        return TS;
    }

    public void stop() {

    }

}
