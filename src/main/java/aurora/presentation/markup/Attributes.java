/*
 * Created on 2007-8-22
 */
package aurora.presentation.markup;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Attributes extends HashMap {
    
    public String toString(){
        StringBuffer buf = new StringBuffer();
        Iterator it = entrySet().iterator();
        while(it.hasNext()){
            Map.Entry entry = (Map.Entry)it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            if(key!=null){
                buf.append(key.toString()).append("=\"").append( value==null?"":value.toString()).append("\" ");
            }
        }
        return buf.toString();
    }

}
