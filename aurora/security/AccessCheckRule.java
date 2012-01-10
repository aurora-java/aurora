package aurora.security;

import aurora.database.service.BusinessModelService;
import aurora.database.service.IDatabaseServiceFactory;
import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;

public class AccessCheckRule implements IAccessRule{
	
	String name;
	String description;
	String checkBM;
	String checkField;
	String successValue="Y";
	IDatabaseServiceFactory mDatabaseServiceFactory;
	
	public AccessCheckRule(IDatabaseServiceFactory databaseServiceFactory){
		mDatabaseServiceFactory=databaseServiceFactory;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCheckBM() {
		return checkBM;
	}

	public void setCheckBM(String checkBM) {
		this.checkBM = checkBM;
	}

	public String getCheckField() {
		return checkField;
	}

	public void setCheckField(String checkField) {
		this.checkField = checkField;
	}

	public String getSuccessValue() {
		return successValue;
	}

	public void setSuccessValue(String successValue) {
		this.successValue = successValue;
	}
	
	public boolean isValid(CompositeMap context_map) throws Exception {
		String parsed_model = TextParser.parse(this.getCheckBM(), context_map);
		BusinessModelService service=mDatabaseServiceFactory.getModelService(parsed_model, context_map);
		CompositeMap result=service.queryAsMap(service.getServiceContext().getParameter());		
		boolean isValid=false;
		if(successValue.equals(result.getObject("record/"+this.getCheckField())))
			isValid=true;
		return isValid;
	}

}
