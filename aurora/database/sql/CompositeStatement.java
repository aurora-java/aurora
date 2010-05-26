/*
 * Created on 2010-5-26 上午10:53:53
 * $Id$
 */
package aurora.database.sql;

import java.util.LinkedList;
import java.util.List;

import aurora.database.DatabaseConstant;

public class CompositeStatement extends AbstractStatement {
    
    // List<ISqlStatement>
    List        childs;

    public CompositeStatement() {
        super(DatabaseConstant.TYPE_COMPOSITE_STATEMENT);
        childs = new LinkedList();
    }
    
    public void addStatement( ISqlStatement statement ){
        childs.add(statement);
    }
    
    public List getStatements(){
        return childs;
    }

}
