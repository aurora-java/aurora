/*
 * Created on 2010-5-11 下午12:15:35
 * $Id$
 */
package aurora.database.profile;

/**
 * A simple DTO to implement sql statement mapping section in database profile config file
 */
public class SqlBuilderMapping {
    
    Class                   sqlBuilder;    
    StatementMapping[]      mappings;
    
    public SqlBuilderMapping(){
        
    }
    
    public Class getSqlBuilder() {
        return sqlBuilder;
    }
    public void setSqlBuilder(Class sqlBuilder) {
        this.sqlBuilder = sqlBuilder;
    }
    public StatementMapping[] getMappings() {
        return mappings;
    }
    public void setMappings(StatementMapping[] mappings) {
        this.mappings = mappings;
    }
    
    

}
