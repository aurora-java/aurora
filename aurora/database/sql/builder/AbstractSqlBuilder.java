/*
 * Created on 2008-4-11
 */
package aurora.database.sql.builder;

import aurora.database.sql.ISqlStatement;

public abstract class AbstractSqlBuilder implements ISqlBuilder {
    
    ISqlBuilderRegistry     registry;    

    public abstract String createSql(ISqlStatement sqlStatement);

    public void setRegistry(ISqlBuilderRegistry registry){
        this.registry = registry;
    }
    
    public IDatabaseProfile getDatabaseProfile(){        
        return registry==null?null:registry.getDatabaseProflie();
    }
    
    public String getKeyword(String key){
        return registry==null?null:registry.getDatabaseProflie().getKeyword(key);
    }

}
