/*
 * Created on 2008-5-23
 */
package aurora.database.sql;

public class UpdateTarget extends BaseTable {

    public static final String UPDATE_TABLE = "UPDATE_TABLE";

    public UpdateTarget(String table_name) {
        super(UpdateTarget.UPDATE_TABLE);
        setTableName(table_name);
    }

    protected BaseField createFieldInstance(String name, String alias) {
        UpdateField field = new UpdateField(this,name);
        return field;
    }
    
    public UpdateField createUpdateField( String name, String source ){
        UpdateField field = (UpdateField)createField(name, source);
        field.setUpdateSource( new RawSqlExpression(source) );
        return field;
    }
    
    public UpdateField createUpdateField( String name, ISqlStatement exp ){
        UpdateField field = (UpdateField)createField(name);
        field.setUpdateSource(exp);
        return field;
    }

}
