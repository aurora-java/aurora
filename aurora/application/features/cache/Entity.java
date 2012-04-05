package aurora.application.features.cache;


public class Entity extends Relation {
	private String name;
	private String operations;
	public final static String SERPRATOR_CHAR=",";

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOperations() {
		return operations;
	}
	public void setOperations(String operations) {
		this.operations = operations;
	}
}
