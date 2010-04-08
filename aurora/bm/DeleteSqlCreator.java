/*
 * Created on 2010-4-2 下午12:44:50
 * Author: Zhou Fan
 */
package aurora.bm;

import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import aurora.database.service.BusinessModelServiceContext;
import aurora.database.sql.ConditionList;
import aurora.database.sql.DeleteStatement;
import aurora.database.sql.ISqlStatement;
import aurora.database.sql.InsertStatement;
import aurora.database.sql.RawSqlExpression;
import aurora.database.sql.UpdateStatement;
import aurora.database.sql.UpdateTarget;
import aurora.database.sql.builder.ISqlBuilderRegistry;

public class DeleteSqlCreator extends AbstractSqlCreator {
    
    DeleteStatement     statement;
    
    public void addPrimaryKeyQuery( BusinessModel model, DeleteStatement stmt){
        ConditionList where = stmt.getWhereClause();
        Field[] fields = model.getPrimaryKeyFields();
        UpdateTarget table = stmt.getUpdateTarget();
        for(int i=0; i<fields.length; i++){
            where.addEqualExpression(table.createField(fields[i].getPhysicalName()), new RawSqlExpression( fields[i].getUpdateExpression() ));
        }
    }

    public DeleteSqlCreator(IModelFactory fact, ISqlBuilderRegistry reg) {
        super(fact, reg);
    }

    public DeleteStatement createDeleteStatement(BusinessModel model){
        DeleteStatement stmt = new DeleteStatement(model.getBaseTable());
        return stmt;
    }
    
    public void onCreateDeleteStatement(BusinessModel model, BusinessModelServiceContext context){
        statement = createDeleteStatement(model);
        String type = context.getObjectContext().getString("DeleteType", "PK");
        if("PK".equals(type)){
            addPrimaryKeyQuery( model, statement );
        }
        context.setStatement(statement);
    }
    
    public void onCreateDeleteSql(ISqlStatement s, BusinessModelServiceContext context){  
        StringBuffer sql = new StringBuffer(registry.getSql(s));
        context.setSqlString(sql);
        ILogger logger = LoggingContext.getLogger(context.getObjectContext(), "aurora.bm");
        logger.config("delete sql: "+sql);
    }   
    
    public void onExecuteDelete( StringBuffer sql, BusinessModelServiceContext bmsc)
        throws Exception
    {
        super.executeUpdateSql(sql, bmsc);      
    }
    
}
