/*
 * Created on 2008-4-26
 */
package aurora.database.profile;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectCreator;

public class DatabaseProfile implements IDatabaseProfile {
    
    String                  mDatabaseName;
    CompositeMap            mProperties = new CompositeMap();
    SqlBuilderRegistry      mSqlBuilderRegistry;
    IObjectCreator          mObjectCreator;
    
    public DatabaseProfile(){
        
    }
    
    public DatabaseProfile( IObjectCreator creator ){
        mObjectCreator = creator;
    }
    
    public DatabaseProfile( String name ){
        this.mDatabaseName = name;
    }
    
    public DatabaseProfile( String name, Properties props ){
        this(name);
        mProperties.putAll(props);
    }

    public String getDatabaseName() {        
        return mDatabaseName;
    }
    
    public void setDatabaseName(String name){
        mDatabaseName = name;
    }
    

    public String getKeyword(String keyword_code) {
        return keyword_code;
    }

    public String getProperty(String name) {        
        Object value = mProperties.get(name);
        return value==null?null:value.toString();
    }

    public void setProperty(String name, String value) {
        mProperties.put(name, value);
    }

    public IObjectCreator getObjectCreator() {
        return mObjectCreator;
    }

    public void setObjectCreator(IObjectCreator objectCreator) {
        mObjectCreator = objectCreator;
    }
    
    protected ISqlBuilder createInstance(Class type)
        throws Exception
    {
        if(mObjectCreator==null)
            return (ISqlBuilder)type.newInstance();
        else
            return (ISqlBuilder)mObjectCreator.createInstance(type);
    }
    
    public void addSqlBuilderMapping( SqlBuilderMapping mapping )
    {

        if(mSqlBuilderRegistry==null)
            mSqlBuilderRegistry = new SqlBuilderRegistry();
        Class builder_type = mapping.getSqlBuilder();
        if(builder_type==null)
            throw new IllegalArgumentException("Must set sqlBuilder property in SqlBuilderMapping");
        ISqlBuilder builder = mSqlBuilderRegistry.getSqlBuilderByType(builder_type);
        if(builder==null)
            try{
                builder = createInstance(builder_type);
            }catch(Exception ex){
                throw new RuntimeException("Error when create ISqlBuilder instance for type "+builder_type.getName(), ex);
            }
        StatementMapping[] marray = mapping.getMappings();
        if(marray!=null)
            for(int i=0; i<marray.length; i++)
                mSqlBuilderRegistry.registerSqlBuilder(marray[i].getStatementClass(), builder);
    }
    
    public ISqlBuilderRegistry getSqlBuilderRegistry(){
        return mSqlBuilderRegistry;
    }
    
    public void addProperties( CompositeMap props ){
        mProperties.putAll(props);
    }
    
    public CompositeMap getProperties(){
        return mProperties;
    }


}
