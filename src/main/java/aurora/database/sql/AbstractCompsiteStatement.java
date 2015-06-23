/*
 * Created on 2008-3-28
 */
package aurora.database.sql;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractCompsiteStatement extends AbstractStatement {
    
    public static final int MAX_PARTS = 20;

    private List[]          parts ;
    
    public AbstractCompsiteStatement(String type){
        super(type);
        parts = new List[MAX_PARTS];
    }

    protected List getPartsNotNull(int id){
        List list = parts[id];
        if(list==null){
            list = new LinkedList();
            parts[id] = list;
        }
        return list;
    }    
    

}
