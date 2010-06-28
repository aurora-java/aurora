package aurora.database.features;

import aurora.bm.AbstractSqlCreator;
import aurora.bm.BusinessModel;
import aurora.database.service.BusinessModelServiceContext;
import aurora.database.sql.UpdateStatement;

public class TagDeleteField{
	String expression;
	String name;	
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
	public void onDecideDeleteStatement(BusinessModelServiceContext context){		
		context.setOperation("TagDelete");
	}
	public void onCreateTagDeleteStatement(BusinessModel model,BusinessModelServiceContext context){
		UpdateStatement stmt = new UpdateStatement(model.getBaseTable(), model.getAlias());
		stmt.addUpdateField(name, expression);
		String type = context.getObjectContext().getString("UpdateType", "PK");
        if("PK".equals(type)){
        	AbstractSqlCreator.addPrimaryKeyQuery(model, stmt);
        }
        context.setStatement(stmt);
        context.put("SqlStatementType", new StringBuffer("Update"));
	} 	
}
