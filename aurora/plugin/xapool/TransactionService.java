package aurora.plugin.xapool;
import javax.naming.NamingException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import org.objectweb.jotm.Jotm;

import aurora.transaction.ITransactionService;
import aurora.transaction.UserTransactionImp;

public class TransactionService implements ITransactionService{
	Jotm jotm=null;
	UserTransaction trans=null;	
	TransactionManager transactionManager=null;
	public TransactionService(boolean useTransactionManager) throws NamingException{
		if(useTransactionManager){
			jotm=new Jotm(true,false);
			trans=jotm.getUserTransaction();
			transactionManager=jotm.getTransactionManager();
		}else{
			trans=new UserTransactionImp();
		}
	}
	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	public UserTransaction getUserTransaction() {		
		return trans;
	}

	public void stop() {
		if(jotm!=null)
			jotm.stop();
		if(trans instanceof UserTransactionImp){
			trans=null;
		}
	}
}
