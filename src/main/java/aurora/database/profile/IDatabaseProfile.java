/*
 * Created on 2008-4-10
 */
package aurora.database.profile;

import uncertain.composite.CompositeMap;

public interface IDatabaseProfile {
    
    public static final String KEYWORD_SELECT =     "SELECT";
    public static final String KEYWORD_FROM =       "FROM";
    public static final String KEYWORD_WHERE =      "WHERE";
    public static final String KEYWORD_EXISTS =     "EXISTS";
    public static final String KEYWORD_GROUP_BY =   "GROUP BY";
    public static final String KEYWORD_ORDER_BY =   "ORDER BY";    
    public static final String KEYWORD_HAVING =     "HAVING";    
    public static final String KEYWORD_AND =        "AND";    
    public static final String KEYWORD_OR =         "OR";
    public static final String KEYWORD_NOT =        "NOT";
    public static final String KEYWORD_AS =         "AS";
    public static final String KEY_UPDATE =         "UPDATE";
    public static final String KEY_INSERT =         "INSERT";
    public static final String KEY_DELETE =         "DELETE";
    public static final String KEY_VALUES =         "VALUES";
    public static final String KEY_SET =            "SET";
    public static final String KEY_FROM =            "FROM";
    public static final String KEY_INTO =            "INTO"; 
    
    public static final String  KEY_USE_JOIN_KEYWORD = "use_join_keyword";
    public static final String KEY_CURRENT_TIME = "current_time";
    public static final String KEY_CURRENT_DATE = "current_date";
    
    //public void setProperty( String name, String value);
    
    public CompositeMap getProperties();
    
    public Object   getProperty( String name );
    
    public void setProperty( String name, Object value );
    
    public String   getDatabaseName();
    
    public String   getKeyword( String keyword_code );
    
    public ISqlBuilderRegistry getSqlBuilderRegistry();
    
    public IDatabaseFactory getOwner();
    
    public void setOwner(IDatabaseFactory fact);

}
