/*
 * Created on 2010-4-2 下午12:44:50
 * Author: Zhou Fan
 */
package aurora.bm;

import aurora.database.profile.IDatabaseFactory;
import aurora.database.service.BusinessModelServiceContext;
import aurora.database.sql.ConditionList;
import aurora.database.sql.DeleteStatement;
import aurora.database.sql.ISqlStatement;
import aurora.database.sql.RawSqlExpression;
import aurora.database.sql.UpdateTarget;

public class DeleteSqlCreator extends AbstractSqlCreator {
    
    //DeleteStatement     statement;
    
    public void addPrimaryKeyQuery( BusinessModel model, DeleteStatement stmt){
        ConditionList where = stmt.getWhereClause();
        Field[] fields = model.getPrimaryKeyFields();
        UpdateTarget table = stmt.getUpdateTarget();
        for(int i=0; i<fields.length; i++){
            where.addEqualExpression(table.createField(fields[i].getPhysicalName()), new RawSqlExpression( fields[i].getUpdateExpression() ));
        }
    }

    public DeleteSqlCreator(IModelFactory model_fact, IDatabaseFactory db_fact){
        super(model_fact, db_fact);
    }    

    public DeleteStatement createDeleteStatement(BusinessModel model){
        DeleteStatement stmt = new DeleteStatement(model.getBaseTable(), model.getAlias());
        return stmt;
    }
    
    public void onCreateDeleteStatement(BusinessModel model, BusinessModelServiceContext context){
        DeleteStatement     statement = createDeleteStatement(model);
        String type = context.getObjectContext().getString("DeleteType", "PK");
        if("PK".equals(type)){
            addPrimaryKeyQuery( model, statement );
        }
        context.setStatement(statement);
    }
    
    
    public void onCreateDeleteSql(ISqlStatement s, BusinessModelServiceContext context){
        doCreateSql("delete", s, context);
        /*
        StringBuffer sql = createSql(s,context);
        context.setSqlString(sql);
        ILogger logger = LoggingContext.getLogger(context.getObjectContext(), "aurora.bm");
        logger.config("delete sql: "+sql);
        */
    }   
    
    /*
    public void onExecuteDelete( StringBuffer sql, BusinessModelServiceContext bmsc)
        throws Exception
    {
        super.executeUpdateSql(sql, bmsc);      
    }
    */
}
