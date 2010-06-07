/*
 * Created on 2008-5-23
 */
package aurora.bm;

import aurora.database.profile.IDatabaseFactory;
import aurora.database.service.BusinessModelServiceContext;
import aurora.database.sql.ISqlStatement;
import aurora.database.sql.InsertStatement;

public class InsertSqlCreator extends AbstractSqlCreator {
    
    public InsertSqlCreator(IModelFactory model_fact, IDatabaseFactory db_fact){
        super(model_fact, db_fact);
    }    
    
    public InsertStatement createInsertStatement(BusinessModel model){
        InsertStatement stmt = new InsertStatement(model.getBaseTable());        
        Field[] fields = model.getFields();
        for(int i=0; i<fields.length; i++){
            Field field = fields[i];
            if(field.isForInsert())
                stmt.addInsertField(field.getPhysicalName(), field.getInsertExpression());
        }
        return stmt;
    }
    
    public void onCreateInsertStatement(BusinessModel model, BusinessModelServiceContext context){
        InsertStatement statement = createInsertStatement(model);
        context.setStatement(statement);
    }
    
    public void onCreateInsertSql(ISqlStatement s, BusinessModelServiceContext context){
        doCreateSql("insert", s, context);
        /*
        StringBuffer sql = createSql(s,context);
        context.setSqlString(sql);
        ILogger logger = LoggingContext.getLogger(context.getObjectContext(), "aurora.bm");
        logger.config("insert sql: "+sql);
        */
    }   
    /*
    public void onExecuteInsert( StringBuffer sql, BusinessModelServiceContext bmsc)
        throws Exception
    {
        super.executeUpdateSql(sql, bmsc);      
    }
   */

}
