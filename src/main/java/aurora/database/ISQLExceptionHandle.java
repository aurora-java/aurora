/*
 * Created on 2007-10-30
 */
package aurora.database;

import java.sql.SQLException;

import uncertain.composite.CompositeMap;

public interface ISQLExceptionHandle {
    
    public void handleException( CompositeMap param, SQLException exception );

}
