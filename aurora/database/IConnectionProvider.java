/*
 * Created on 2008-5-30
 */
package aurora.database;

import java.sql.Connection;

public interface IConnectionProvider {
    
    public Connection getNamedConnection( String name );
    
    public Connection getConnection();

}
