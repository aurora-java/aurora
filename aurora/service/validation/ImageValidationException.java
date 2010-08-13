package aurora.service.validation;

public class ImageValidationException extends Exception {
	
	private static final long serialVersionUID = 7194850476794172199L;
	
	private String message;

	public ImageValidationException(String msg) {
		super();
		message = msg;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
