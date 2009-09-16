/*
 * Created on 2009-9-14 下午02:49:09
 * Author: Zhou Fan
 */
package aurora.database.service;

import aurora.database.sql.builder.IDatabaseProfile;
import aurora.database.sql.builder.ISqlBuilderRegistry;

public interface IDatabaseServiceFactory {
    
    /**
     * Get current <code>IDatabaseProfile</code> under use
     */
    public IDatabaseProfile getDatabaseProfile();
    
    public void setDatabaseProfile(IDatabaseProfile databaseProfile);
    
    public ISqlBuilderRegistry getSqlBuilderRegistry();
    
    public void setSqlBuilderRegistry(ISqlBuilderRegistry sqlBuilderRegistry);

}
