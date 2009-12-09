/*
 * Created on 2009-11-16 下午04:54:50
 * Author: Zhou Fan
 */
package aurora.database.sql;

public class InsertField extends UpdateField {
    
    public static final String INSERT_FIELD = "INSERT_FIELD";

    public InsertField(ISqlStatement parent, String name,
            String source_expression) {
        super(parent, name, source_expression);
        setType(INSERT_FIELD);
    }

    public InsertField(ISqlStatement parent, String name) {
        super(parent, name);
        setType(INSERT_FIELD);
    }

}
