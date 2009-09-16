/*
 * Created on 2009-9-15 上午02:33:39
 * Author: Zhou Fan
 */
package aurora.bm;

import aurora.database.sql.builder.ISqlBuilderRegistry;

public class InsertStatementBuilder extends AbstractSqlCreator {
    
    
    public InsertStatementBuilder(IModelFactory fact, ISqlBuilderRegistry reg) {
        super(fact, reg);
    }

}
