/*
 * Created on 2008-5-23
 */
package aurora.bm;

import uncertain.composite.CompositeMap;
import aurora.database.profile.IDatabaseFactory;
import aurora.database.service.BusinessModelServiceContext;
import aurora.database.service.ServiceOption;
import aurora.database.service.SqlServiceContext;
import aurora.database.sql.ISqlStatement;
import aurora.database.sql.UpdateStatement;
public class UpdateSqlCreator extends AbstractSqlCreator {
    
    /*
    public static String getUpdateSource( Field f ){
        String source = f.getUpdateExpression();
        if(source==null) source = "${@" + f.getName() + "}";
        return source;
    }
    */
    
    public UpdateSqlCreator(IModelFactory model_fact, IDatabaseFactory db_fact){
        super(model_fact, db_fact);
    }    
    
    public UpdateStatement createUpdateStatement(BusinessModel model, SqlServiceContext context ){
        UpdateStatement stmt = new UpdateStatement(model.getBaseTable(), model.getAlias()); 
        ServiceOption op = context.getServiceOption();
        CompositeMap param = context.getCurrentParameter();
        boolean update_passed_param = false;
        if(op!=null)
            update_passed_param = op.isUpdatePassedFieldOnly();
        Field[] fields = model.getFields();
        for(int i=0; i<fields.length; i++){
            Field field = fields[i];
            if(field.isForUpdate()){
                if(update_passed_param){
                    String path = field.getInputPath();
                    if(!field.isForceUpdate())
                        if(param.getObject(path)==null)
                            if(!param.containsKey(field.getName()))
                                continue;
                }
                stmt.addUpdateField(field.getPhysicalName(), field.getUpdateExpression());
            }
        }
        return stmt;
    }
    
    public void onCreateUpdateStatement(BusinessModel model, BusinessModelServiceContext context){
        UpdateStatement statement = createUpdateStatement(model, SqlServiceContext.createSqlServiceContext(context.getObjectContext()));
        String type = context.getObjectContext().getString("UpdateType", "PK");
        if("PK".equals(type)){
            addPrimaryKeyQuery( model, statement );
            
        }
        context.setStatement(statement);
    }
    
    public void onCreateUpdateSql(ISqlStatement s, BusinessModelServiceContext context){  
        doCreateSql("update", s, context);
        /*
        StringBuffer sql = createSql(s,context);
        context.setSqlString(sql);
        ILogger logger = LoggingContext.getLogger(context.getObjectContext(), "aurora.bm");
        logger.config("delete sql: "+sql);
        */        
    }   
    
    /*
    public void onExecuteUpdate( StringBuffer sql, BusinessModelServiceContext bmsc)
        throws Exception
    {
        super.executeUpdateSql(sql, bmsc);        
    }
    */
   
}
