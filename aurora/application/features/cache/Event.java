package aurora.application.features.cache;

public class Event extends Relation{
	private String message;
	private String operation;
	
	public Event(){
		super();
	}
	
	public Event(String message, String operation,String pkColumns, boolean isMultiValue,String mapColumns) {
		super(pkColumns,isMultiValue,mapColumns);
		this.message = message;
		this.operation = operation;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}

}
