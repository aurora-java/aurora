/*
 * Created on 2010-4-29 下午03:31:57
 * $Id$
 */
package aurora.database.profile;

/**
 * The facade interface to get a ISqlBuilderRegistry instance under specified database profile
 * ISqlFactory
 */
public interface IDatabaseFactory {
    
    /** get/set ISqlBuilderRegistry by database name */
    public ISqlBuilderRegistry getSqlBuilderRegistry( String database_name );
    
    public void setSqlBuilderRegistry( String database_name, ISqlBuilderRegistry reg );
    
    /** get/set IDatabaseProfile by database name */
    public IDatabaseProfile getDatabaseProfile( String database_name );
    
    public void addDatabaseProfile( IDatabaseProfile profile );
    
    /** get all supported database names, in String array */
    public String[] getSuppportedDatabases();
    
    public String getDefaultDatabase();

}
