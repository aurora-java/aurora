/*
 * Created on 2008-5-22
 */
package aurora.database.sql;

import java.util.LinkedList;
import java.util.List;

import aurora.database.DatabaseConstant;

public class DeleteStatement extends AbstractStatementWithWhere implements IWithUpdateTarget {

    UpdateTarget    updateTarget;
    
    public DeleteStatement( String table_name ){
        super(DatabaseConstant.TYPE_DELETE);
        updateTarget = new UpdateTarget(table_name);
    }
    
    public DeleteStatement( String table_name, String alias ){
        this(table_name);
        if(alias!=null) updateTarget.setAlias(alias);
    }
    
    public UpdateTarget getUpdateTarget(){
        return updateTarget;
    }

}
