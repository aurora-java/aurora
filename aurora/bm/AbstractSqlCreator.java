/*
 * Created on 2008-5-23
 */
package aurora.bm;

import aurora.database.service.BusinessModelServiceContext;
import aurora.database.sql.builder.ISqlBuilderRegistry;

public abstract class AbstractSqlCreator {
    
    ModelFactory                    modelFactory;
    ISqlBuilderRegistry             registry;
    
    public AbstractSqlCreator(ModelFactory fact, ISqlBuilderRegistry reg){
        modelFactory = fact;        
        registry = reg;
    }
 
}
