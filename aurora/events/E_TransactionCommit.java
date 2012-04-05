package aurora.events;

import aurora.service.IService;

public interface E_TransactionCommit {
	public static final String EVENT_NAME="TransactionCommit";
	public int onTransactionCommit(IService service);
}
