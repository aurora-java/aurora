package aurora.events;

import aurora.service.IService;

public interface E_TransactionRollBack {
	public static final String EVENT_NAME = "TransactionRollBack";
	public int onTransactionRollBack(IService service);
}
