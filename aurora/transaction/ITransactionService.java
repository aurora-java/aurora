package aurora.transaction;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
public interface ITransactionService{
	public TransactionManager getTransactionManager();
	public UserTransaction getUserTransaction();
	public void stop();
}
