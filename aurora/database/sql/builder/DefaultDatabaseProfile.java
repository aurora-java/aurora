/*
 * Created on 2008-4-26
 */
package aurora.database.sql.builder;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DefaultDatabaseProfile implements IDatabaseProfile {
    
    String  databaseName;
    Map     properties;
    
    public DefaultDatabaseProfile( String name ){
        this.databaseName = name;
        properties = new HashMap();
    }
    
    public DefaultDatabaseProfile( String name, Properties props ){
        this(name);
        properties.putAll(props);
    }

    public String getDatabaseName() {        
        return databaseName;
    }

    public String getKeyword(String keyword_code) {
        return keyword_code;
    }

    public String getProperty(String name) {        
        Object value = properties.get(name);
        return value==null?null:value.toString();
    }

    public void setProperty(String name, String value) {
        properties.put(name, value);
    }

}
