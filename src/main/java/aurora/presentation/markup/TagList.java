/*
 * Created on 2009-5-22
 */
package aurora.presentation.markup;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class TagList extends LinkedList {
    
    boolean endLine = true;

    public TagList() {

    }

    public TagList(Collection c) {
        super(c);
    }
    
    public String toString(){
        StringBuffer buf = new StringBuffer();
        Iterator it = iterator();
        int n = 0;
        while( it.hasNext()){
            Object obj = it.next(); 
            if(obj!=null){
                if( n>0 && endLine ) 
                    buf.append('\n');
                buf.append(obj.toString());
                n++;
            }
        }
        return buf.toString();
    }

}
