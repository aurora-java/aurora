/*
 * Created on 2008-5-23
 */
package aurora.bm;

import uncertain.composite.CompositeMap;
import aurora.database.ParsedSql;
import aurora.database.SqlRunner;
import aurora.database.profile.ISqlBuilderRegistry;
import aurora.database.service.BusinessModelServiceContext;
import aurora.database.service.SqlServiceContext;
import aurora.database.sql.ConditionList;
import aurora.database.sql.ISqlStatement;
import aurora.database.sql.RawSqlExpression;
import aurora.database.sql.UpdateStatement;
import aurora.database.sql.UpdateTarget;

public class UpdateSqlCreator extends AbstractSqlCreator {
    
    UpdateStatement statement;
    
    /*
    public static String getUpdateSource( Field f ){
        String source = f.getUpdateExpression();
        if(source==null) source = "${@" + f.getName() + "}";
        return source;
    }
    */
    
    public UpdateSqlCreator(IModelFactory fact, ISqlBuilderRegistry reg){
        super(fact,reg);
    }    
    
    public UpdateStatement createUpdateStatement(BusinessModel model){
        UpdateStatement stmt = new UpdateStatement(model.getBaseTable(), model.getAlias());        
        Field[] fields = model.getFields();
        for(int i=0; i<fields.length; i++){
            Field field = fields[i];
            if(field.isForUpdate())
                stmt.addUpdateField(field.getPhysicalName(), field.getUpdateExpression());
        }
        return stmt;
    }
    
    public void addPrimaryKeyQuery( BusinessModel model, UpdateStatement stmt){
        ConditionList where = stmt.getWhereClause();
        Field[] fields = model.getPrimaryKeyFields();
        UpdateTarget table = stmt.getUpdateTarget();
        for(int i=0; i<fields.length; i++){
            where.addEqualExpression(table.createField(fields[i].getPhysicalName()), new RawSqlExpression( fields[i].getUpdateExpression() ));
        }
    }
    
    public void onCreateUpdateStatement(BusinessModel model, BusinessModelServiceContext context){
        statement = createUpdateStatement(model);
        String type = context.getObjectContext().getString("UpdateType", "PK");
        if("PK".equals(type)){
            addPrimaryKeyQuery( model, statement );
        }
        context.setStatement(statement);
    }
    
    public void onCreateUpdateSql(ISqlStatement s, BusinessModelServiceContext context){  
        StringBuffer sql = new StringBuffer(registry.getSql(s));
        context.setSqlString(sql);
    }   
    
    public void onExecuteUpdate( StringBuffer sql, BusinessModelServiceContext bmsc)
        throws Exception
    {
        super.executeUpdateSql(sql, bmsc);        
    }
   

}
