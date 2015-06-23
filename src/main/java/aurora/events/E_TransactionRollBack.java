package aurora.events;


public interface E_TransactionRollBack {
	public static final String EVENT_NAME = "TransactionRollBack";
	public int onTransactionRollBack();
}
