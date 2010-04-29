/*
 * Created on 2010-4-29 下午03:44:03
 * $Id$
 */
package aurora.database.profile;

import java.util.HashMap;
import java.util.Map;

public class DatabaseFactory implements IDatabaseFactory {
    
    String  mDefaultDatabaseName;
    Map     mDatabaseProfileMap = new HashMap();
    Map     mSqlBuilderRegistryMap = new HashMap();

    public IDatabaseProfile getDatabaseProfile(String database_name) {
        return (IDatabaseProfile)mDatabaseProfileMap.get(database_name);
    }

    public ISqlBuilderRegistry getSqlBuilderRegistry(String database_name) {
        return (ISqlBuilderRegistry)mSqlBuilderRegistryMap.get(database_name);
    }

    public String[] getSuppportedDatabases() {
        // TODO Auto-generated method stub
        return null;
    }

    public void addDatabaseProfile(IDatabaseProfile profile) {
        mDatabaseProfileMap.put(profile.getDatabaseName(), profile);
    }

    public void setSqlBuilderRegistry(String database_name,
            ISqlBuilderRegistry reg) {
        mSqlBuilderRegistryMap.put(database_name, reg);
    }
    
    public String getDefaultDatabase(){
        return mDefaultDatabaseName;
    }

}
