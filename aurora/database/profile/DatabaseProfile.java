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
    CompositeMap            mProperties = new CompositeMap("properties");
    CompositeMap            mKeywords = new CompositeMap("keywords");
    SqlBuilderRegistry      mSqlBuilderRegistry;
    IObjectCreator          mObjectCreator;
    IDatabaseFactory        owner;
    
    public static boolean getBooleanValue( IDatabaseProfile profile, String key, boolean default_value ){
        Object obj  = profile.getProperty(key);
        if(obj==null)
            return default_value;
        String str = obj.toString();
        return (str != null) && str.equalsIgnoreCase("true");        
    }
    
    public static boolean isUseJoinKeyword( IDatabaseProfile profile ){
        return getBooleanValue(profile, IDatabaseProfile.KEY_USE_JOIN_KEYWORD, true );
    }    
    
    public DatabaseProfile(){
        mSqlBuilderRegistry = new SqlBuilderRegistry(this);
    }
    
    public DatabaseProfile( IObjectCreator creator ){
        this();
        mObjectCreator = creator;
    }
    
    public DatabaseProfile( String name ){
        this();
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
        String keyword = mKeywords.getString(keyword_code);
        if(keyword!=null)
            return keyword;
        else
            return keyword_code;
    }

    public Object getProperty(String name) {        
        Object value = mProperties.get(name);
        return value==null?null:value.toString();
    }

    public void setProperty(String name, Object value) {
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
    
    public void addKeywords( CompositeMap words ){
        mKeywords.putAll(words);
    }    
    
    public CompositeMap getProperties(){
        return mProperties;
    }

    public IDatabaseFactory getOwner() {
        return owner;
    }

    public void setOwner(IDatabaseFactory owner) {
        this.owner = owner;
        if(owner instanceof DatabaseFactory){
            ISqlBuilderRegistry default_reg = ((DatabaseFactory)owner).getDefaultSqlBuilderRegistry();
            if(default_reg!=null)
                mSqlBuilderRegistry.setParent(default_reg);
        }
    }


}
