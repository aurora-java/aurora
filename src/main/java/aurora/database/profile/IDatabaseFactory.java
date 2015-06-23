/*
 * Created on 2010-4-29 下午03:31:57
 * $Id$
 */
package aurora.database.profile;

import uncertain.composite.CompositeMap;

/**
 * The facade interface to get a ISqlBuilderRegistry instance under specified database profile
 * ISqlFactory
 * The overall structure of database profile object model:
 * 
 * IDatabaseFactory
 *       (contains) -> IDatabaseProfile
 *                          (contains)  -> ISqlBuilderRegistry
 *                                              (contains)   ->  ISqlBuilder
 *                                              
 * The client usually uses API like this:
 * get IDatabaseFactory instance;
 * get IDatabaseProfile from IDatabaseFactory with specified database name;
 * get ISqlBuilderRegistry associated with this IDatabaseProfile instance;
 * get actual sql statement from ISqlBuilderRegistry;
 */
public interface IDatabaseFactory {
    
    /** get/set IDatabaseProfile by database name */
    public IDatabaseProfile getDatabaseProfile( String database_name );
    
    public void addDatabaseProfile( IDatabaseProfile profile );
    
    /** get all registered database profiles  */
    public IDatabaseProfile[]   getDatabases();
    
    /** get default database profile */
    public IDatabaseProfile getDefaultDatabaseProfile();
    
    /** get/set global property */
    public Object getProperty( String key );
    
    public void setProperty( String key, Object value );
    
    public CompositeMap getProperties();

}
