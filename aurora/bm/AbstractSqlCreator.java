/*
 * Created on 2008-5-23
 */
package aurora.bm;

import aurora.database.service.BusinessModelServiceContext;
import aurora.database.sql.builder.ISqlBuilderRegistry;

public abstract class AbstractSqlCreator {
    
    IModelFactory                    modelFactory;
    ISqlBuilderRegistry             registry;
    
    public AbstractSqlCreator(IModelFactory fact, ISqlBuilderRegistry reg){
        modelFactory = fact;        
        registry = reg;
    }
 
}
