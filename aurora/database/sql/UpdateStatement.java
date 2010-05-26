/*
 * Created on 2008-5-22
 */
package aurora.database.sql;

import java.util.LinkedList;
import java.util.List;

import aurora.database.DatabaseConstant;

public class UpdateStatement extends AbstractStatementWithWhere implements IWithUpdateTarget {
    
    List            updateFields;
    UpdateTarget    updateTarget;
    
    public UpdateStatement( String table_name ){
        super(DatabaseConstant.TYPE_UPDATE);
        updateFields = new LinkedList();
        updateTarget = new UpdateTarget(table_name);
    }
    
    public UpdateStatement( String table_name, String alias ){
        this(table_name);
        if(alias!=null) updateTarget.setAlias(alias);
    }
    
    public UpdateTarget getUpdateTarget(){
        return updateTarget;
    }
    
    public List getUpdateFields(){
        return  updateFields;
    }
    
    public void addUpdateField(UpdateField field){
        updateFields.add(field);
    }
    
    public UpdateField addUpdateField( String name, String source ){
        UpdateField field = updateTarget.createUpdateField(name, source);
        addUpdateField(field);
        return field;
    }

}
