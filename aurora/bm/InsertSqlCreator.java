/*
 * Created on 2008-5-23
 */
package aurora.bm;

import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import aurora.database.profile.ISqlBuilderRegistry;
import aurora.database.service.BusinessModelServiceContext;
import aurora.database.sql.ISqlStatement;
import aurora.database.sql.InsertStatement;

public class InsertSqlCreator extends AbstractSqlCreator {
    
    InsertStatement statement;
    
    public InsertSqlCreator(IModelFactory fact, ISqlBuilderRegistry reg){
        super(fact,reg);
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
        statement = createInsertStatement(model);
        context.setStatement(statement);
    }
    
    public void onCreateInsertSql(ISqlStatement s, BusinessModelServiceContext context){  
        StringBuffer sql = new StringBuffer(registry.getSql(s));
        context.setSqlString(sql);
        ILogger logger = LoggingContext.getLogger(context.getObjectContext(), "aurora.bm");
        logger.config("insert sql: "+sql);
    }   
    
    public void onExecuteInsert( StringBuffer sql, BusinessModelServiceContext bmsc)
        throws Exception
    {
        super.executeUpdateSql(sql, bmsc);      
    }
   

}
