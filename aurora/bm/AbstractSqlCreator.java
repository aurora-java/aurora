/*
 * Created on 2008-5-23
 */
package aurora.bm;

import uncertain.composite.CompositeMap;
import aurora.database.ParsedSql;
import aurora.database.SqlRunner;
import aurora.database.service.BusinessModelServiceContext;
import aurora.database.service.SqlServiceContext;
import aurora.database.sql.builder.ISqlBuilderRegistry;

public abstract class AbstractSqlCreator {
    
    IModelFactory                    modelFactory;
    ISqlBuilderRegistry             registry;
    
    public AbstractSqlCreator(IModelFactory fact, ISqlBuilderRegistry reg){
        modelFactory = fact;        
        registry = reg;
    }
    
    /** Invoke by sub class to execute previously generated DML sql */
    public void executeUpdateSql( StringBuffer sql, BusinessModelServiceContext bmsc )
        throws Exception
    {
        ParsedSql s = new ParsedSql(sql.toString());
        SqlServiceContext context = null;
        CompositeMap root =  bmsc.getObjectContext().getParent();
        if(root!=null)
            context = SqlServiceContext.createSqlServiceContext(root);
        else
            context = bmsc;
        SqlRunner runner = new SqlRunner(context, s);
        runner.setTrace(bmsc.isTrace());        
        bmsc.setSqlRunner(runner);
        runner.update(bmsc.getCurrentParameter());        
    }
 
}
