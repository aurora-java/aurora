package aurora.events;


public interface E_TransactionCommit {
	public static final String EVENT_NAME="TransactionCommit";
	public int onTransactionCommit();
}
