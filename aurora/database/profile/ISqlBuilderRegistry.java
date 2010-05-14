/*
 * Created on 2008-4-14
 */
package aurora.database.profile;

import aurora.database.sql.ISqlStatement;

public interface ISqlBuilderRegistry {

    public IDatabaseProfile getDatabaseProfile();
    
    public void setDatabaseProfile(IDatabaseProfile profile);
    
    public ISqlBuilder      getBuilder( ISqlStatement   statement );
    
    public void registerSqlBuilder( Class statement_type, ISqlBuilder sql_builder );
    
    public void setParent( ISqlBuilderRegistry parent );
    
    public String getSql( ISqlStatement statement );

}
