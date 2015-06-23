package aurora.ide.api.statistics;

public class Status {
	final public static int WARNING = 0;
	final public static int ERROR = 1;
	final public static int INFO = 2;
	final public static int OK = 3;
	final public static int CANCEL = 4;

	final public static Status OK_STATUS = new Status(OK);
	public static final Status CANCEL_STATUS = new Status(CANCEL);

	private String message;
	private int status;

	public Status(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
