/** Defines events fired in database operation
 *  Not for implement
 *  Created on 2009-4-27
 */
package aurora.database;

import aurora.database.service.RawSqlService;

public interface IEventDefine {
    
    /** Fired from RawSqlService */
    public void onPopulateQuerySql( RawSqlService service, StringBuffer sqlString);
    
    /** Fired from RawSqlService */
    public void onQueryFinish();

}
