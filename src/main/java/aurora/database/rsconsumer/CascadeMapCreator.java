/*
 * Created on 2011-7-13 上午10:49:15
 * $Id$
 */
package aurora.database.rsconsumer;

import uncertain.composite.TextParser;
import uncertain.exception.BuiltinExceptionFactory;

public class CascadeMapCreator extends CompositeMapCreator {

    String      key;
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }

    public void endRow() {
        String k = TextParser.parse(key, currentRecord);
        rootMap.put(k, currentRecord);
    }

    public void begin(String root_name) {
        if(key==null)
            throw BuiltinExceptionFactory.createAttributeMissing(this, "key");
        super.begin(root_name);
    }
    
    

}
