/*
 * Created on 2009-11-23 下午03:25:20
 * Author: Zhou Fan
 */
package aurora.database.local.oracle.sql;

import java.util.LinkedList;
import java.util.List;

import aurora.database.profile.ISqlBuilder;
import aurora.database.sql.AbstractStatement;
import aurora.database.sql.FieldWithSource;
import aurora.database.sql.ISqlStatement;
import aurora.database.sql.InsertField;

/** implements oracle returning into clause
 *  returning field1, field2, ... , fieldn into value1, value2, ... , valuen 
 */
public class ReturningIntoStatement extends AbstractStatement implements ISqlStatement {
    
    public ReturningIntoStatement() {
        super(RETURNING_INTO);
        mFieldsMapping = new LinkedList();
    }

    public static final String RETURNING_INTO = "RETURNING_INTO";
    /** List<FieldWithSource> to hold returning into fields */
    List    mFieldsMapping;

    public String getType() {
        return RETURNING_INTO;
    }
    
    public List getFields(){
        return mFieldsMapping;
    }
    
    public void addField( FieldWithSource field ){
        mFieldsMapping.add(field);
        field.setParent(this);
    }
    
    public void addField( String field_name, String returning_target ){
        InsertField field = new InsertField(this, field_name, returning_target);
        addField(field);
    }


}
