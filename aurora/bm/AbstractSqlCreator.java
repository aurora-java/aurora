/*
 * Created on 2008-5-23
 */
package aurora.bm;

import java.util.Collection;
import java.util.logging.Level;

import uncertain.composite.CompositeMap;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import aurora.database.ParsedSql;
import aurora.database.SqlRunner;
import aurora.database.profile.IDatabaseFactory;
import aurora.database.profile.IDatabaseProfile;
import aurora.database.profile.ISqlBuilderRegistry;
import aurora.database.service.BusinessModelServiceContext;
import aurora.database.service.SqlServiceContext;
import aurora.database.sql.ISqlStatement;
import aurora.database.sql.IStatementWithParameter;

public abstract class AbstractSqlCreator {
    
    IModelFactory                    modelFactory;
    IDatabaseFactory                 mDatabaseFactory;
    
    public AbstractSqlCreator(IModelFactory model_fact, IDatabaseFactory db_fact ){
        modelFactory = model_fact;        
        mDatabaseFactory = db_fact;
    }
    
    /** Invoke by sub class to execute previously generated DML sql */
    public void executeUpdateSql( StringBuffer sql, BusinessModelServiceContext bmsc )
        throws Exception
    {
        Collection parameters = null;
        ISqlStatement stmt = bmsc.getStatement();
        if(stmt !=null)
            if(stmt instanceof IStatementWithParameter )
                parameters = ((IStatementWithParameter)stmt).getParameters();
        ParsedSql s = new ParsedSql();
        if(parameters!=null)
            s.defineParameters(parameters);
        s.parse(sql.toString());
        SqlServiceContext context = null;
        CompositeMap root =  bmsc.getObjectContext().getParent();
        if(root!=null)
            context = SqlServiceContext.createSqlServiceContext(root);
        else
            context = bmsc;
        SqlRunner runner = new SqlRunner(context, s);
        runner.setConnectionName(bmsc.getBusinessModel().getDataSourceName());
        runner.setTrace(bmsc.isTrace());        
        bmsc.setSqlRunner(runner);
        runner.update(bmsc.getCurrentParameter());        
    }
    
    /*
    public void executeUpdateSql( StringBuffer sql, BusinessModelServiceContext bmsc )
        throws Exception
    {
        executeUpdateSql( null, sql, bmsc);        
    }
    */
    
    public ISqlBuilderRegistry getSqlBuilderRegistry( BusinessModel model ){
        String databaseType = model.getDatabaseType();
        if(databaseType!=null){
            IDatabaseProfile prof = mDatabaseFactory.getDatabaseProfile(databaseType);
            if(prof==null)
                throw new IllegalArgumentException("Unknown database type:"+databaseType);
            return prof.getSqlBuilderRegistry();
        }else
            return mDatabaseFactory.getDefaultDatabaseProfile().getSqlBuilderRegistry();
    }
    
    protected StringBuffer createSql( ISqlStatement stmt, BusinessModelServiceContext context){
        ISqlBuilderRegistry reg = getSqlBuilderRegistry(context.getBusinessModel());
        String str = reg.getSql(stmt);
        if(str==null)
            throw new IllegalStateException("Can't get proper bulider for sql statement "+stmt.getClass().getName());
        StringBuffer sql = new StringBuffer(str);
        return sql;
    }
    
    protected void doCreateSql( String type, ISqlStatement stmt, BusinessModelServiceContext context){
        StringBuffer sql = createSql(stmt,context);
        context.setSqlString(sql);
        ILogger logger = LoggingContext.getLogger(context.getObjectContext(), "aurora.bm");
        logger.log(Level.CONFIG, "{0} sql: {1}", new Object[]{type, sql} );
    }
 
}
