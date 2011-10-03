/*
 * Created on 2010-4-29 下午03:44:03
 * $Id$
 */
package aurora.database.profile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uncertain.composite.CompositeMap;
import uncertain.event.ParticipantManager;
import uncertain.ocm.ClassRegistry;

public class DatabaseFactory implements IDatabaseFactory {
    ISqlBuilderRegistry  mDefaultSqlBuilderRegistry;
    String              mDefaultDatabaseName;
    // name -> IDatabaseProfile
    Map                 mDatabaseProfileMap = new HashMap();
    ClassRegistry       mClassRegistry;
    ParticipantManager  mParticipantManager;
    CompositeMap        mProperties = new CompositeMap("properties");
    
    
    //ILogger             mLogger;
    
    /*
    UncertainEngine     mEngine;
    public DatabaseFactory( UncertainEngine engine ){
        this();
        mEngine = engine;
    }
    */
    
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
        profile.setOwner(this);
        ISqlBuilderRegistry reg = profile.getSqlBuilderRegistry();
        if(reg!=null)
            reg.setParent(mDefaultSqlBuilderRegistry);
        if(name.equals(mDefaultDatabaseName))
            mDefaultSqlBuilderRegistry.setDatabaseProfile(profile);
    }
    
    public void addProperties( CompositeMap propers ){
        mProperties.copy(propers);
    }
    
    public String getDefaultDatabase(){
        return mDefaultDatabaseName;
    }
    
    public void setDefaultDatabase(String database){
        mDefaultDatabaseName = database;
        IDatabaseProfile prof = (IDatabaseProfile)mDatabaseProfileMap.get(database);
        if(prof != null && mDefaultSqlBuilderRegistry!=null)
            mDefaultSqlBuilderRegistry.setDatabaseProfile(prof);
    }
    
    public void addClassRegistry( ClassRegistry reg ){
        mClassRegistry = reg;
    }
    
    public ClassRegistry getClassRegistry(){
        return mClassRegistry;
    }
    
    public void addParticipantManager( ParticipantManager pm ){
        mParticipantManager = pm;
    }
    
    public ParticipantManager getParticipantManager(){
        return mParticipantManager;
    }
    
    public IDatabaseProfile getDefaultDatabaseProfile(){
        if(mDefaultDatabaseName==null)
            throw new IllegalArgumentException("default database name not set");
        IDatabaseProfile prof = getDatabaseProfile(mDefaultDatabaseName);
        if(prof==null)
            throw new IllegalArgumentException("specified default database name "+mDefaultDatabaseName+" not found");
        return prof;
    }
    
    public Object getProperty( String key ){
        return mProperties.get(key);
    }
    
    public CompositeMap getProperties(){
        return mProperties;
    }
    
    public void setProperty( String key, Object value ){
        mProperties.put(key, value);    
    }
/*    
    public void onInitialize(){
        mLogger = mEngine.getLogger("aurora.database");
        mLogger.info("Constructing database factory");
        IObjectRegistry reg = mEngine.getObjectRegistry();
        reg.registerInstanceOnce(IDatabaseFactory.class, this);
        
        DatabaseServiceFactory dbsf = (DatabaseServiceFactory)reg.getInstanceOfType(DatabaseServiceFactory.class);
        if(dbsf==null){
            mLogger.warning("No DatabaseServiceFactory instance created");
        }else{
            dbsf.setDatabaseFactory(this);
        }
    }
*/    
    public ISqlBuilderRegistry getDefaultSqlBuilderRegistry(){
        return mDefaultSqlBuilderRegistry;
    }
    
    public void setDefaultSqlBuilderRegistry(ISqlBuilderRegistry reg){
        mDefaultSqlBuilderRegistry = reg;
    }

}
