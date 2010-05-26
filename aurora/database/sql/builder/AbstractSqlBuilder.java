/*
 * Created on 2008-4-11
 */
package aurora.database.sql.builder;

import aurora.database.profile.IDatabaseProfile;
import aurora.database.profile.ISqlBuilder;
import aurora.database.profile.ISqlBuilderRegistry;
import aurora.database.sql.ISqlStatement;

public abstract class AbstractSqlBuilder implements ISqlBuilder {
    
    protected ISqlBuilderRegistry     mRegistry;    

    public abstract String createSql(ISqlStatement sqlStatement);

    public void setRegistry(ISqlBuilderRegistry registry){
        this.mRegistry = registry;
    }
    
    public ISqlBuilderRegistry getRegistry(){
        return this.mRegistry;
    }
    
    public IDatabaseProfile getDatabaseProfile(){        
        return mRegistry==null?null:mRegistry.getDatabaseProfile();
    }
    
    public String getKeyword(String key){
        IDatabaseProfile profile = getDatabaseProfile();
        return profile==null?null:profile.getKeyword(key);
    }
    
    /**
     * if specified key is not found in database profile config, return null
     */
    public String getKeywordWithNull( String key ){
        String keyword = getKeyword(key);
        if(keyword!=null)
            if(keyword.equals(key))
                keyword=null;
        return keyword;
    }

}
