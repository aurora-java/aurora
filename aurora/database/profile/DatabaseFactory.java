/*
 * Created on 2010-4-29 下午03:44:03
 * $Id$
 */
package aurora.database.profile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uncertain.ocm.ClassRegistry;

public class DatabaseFactory implements IDatabaseFactory {
    SqlBuilderRegistry  mDefaultSqlBuilderRegistry;
    String              mDefaultDatabaseName;
    // name -> IDatabaseProfile
    Map                 mDatabaseProfileMap = new HashMap();
    ClassRegistry       mClassRegistry;    
    
    public DatabaseFactory(){
        mDefaultSqlBuilderRegistry = new SqlBuilderRegistry();
    }

    public IDatabaseProfile getDatabaseProfile(String database_name) {
        return (IDatabaseProfile)mDatabaseProfileMap.get(database_name);
    }

    public IDatabaseProfile[]   getDatabases(){
        Set s = new HashSet();
        s.addAll(mDatabaseProfileMap.values());
        int length = s.size();
        IDatabaseProfile[] profile = new IDatabaseProfile[length];
        Object[] obj = s.toArray();
        System.arraycopy(obj, 0, profile, 0, length);
        return profile;
    }
    
    public void setDatabases( IDatabaseProfile[] databases ){
        for(int i=0; i<databases.length; i++)
            addDatabaseProfile(databases[i]);
    }

    public void addDatabaseProfile(IDatabaseProfile profile) {
        String name = profile.getDatabaseName();
        if(name==null)
            throw new IllegalArgumentException("Must set databaseName for database profile");
        mDatabaseProfileMap.put(name, profile);
        ISqlBuilderRegistry reg = profile.getSqlBuilderRegistry();
        if(reg!=null)
            reg.setParent(mDefaultSqlBuilderRegistry);
    }

    
    public String getDefaultDatabase(){
        return mDefaultDatabaseName;
    }
    
    public void setDefaultDatabase(String database){
        mDefaultDatabaseName = database;
    }
    
    public void addClassRegistry( ClassRegistry reg ){
        mClassRegistry = reg;
    }
    
    public ClassRegistry getClassRegistry(){
        return mClassRegistry;
    }
    
    public IDatabaseProfile getDefaultDatabaseProfile(){
        if(mDefaultDatabaseName==null)
            throw new IllegalArgumentException("default database name not set");
        IDatabaseProfile prof = getDatabaseProfile(mDefaultDatabaseName);
        if(prof==null)
            throw new IllegalArgumentException("specified default database name "+mDefaultDatabaseName+" not found");
        return prof;
    }

}
