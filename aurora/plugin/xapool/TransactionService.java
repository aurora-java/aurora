package aurora.plugin.xapool;
import javax.naming.NamingException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import org.objectweb.jotm.Jotm;

import aurora.transaction.ITransactionService;

public class TransactionService implements ITransactionService{
	Jotm jotm=null;
	public TransactionService() throws NamingException{
		jotm=new Jotm(true,false);
	}
	public TransactionManager getTransactionManager() {
		return jotm.getTransactionManager();
	}

	public UserTransaction getUserTransaction() {
		
		return jotm.getUserTransaction();
	}

	public void stop() {
		jotm.stop();		
	}
}
