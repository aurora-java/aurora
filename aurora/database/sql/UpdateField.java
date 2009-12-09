/*
 * Created on 2008-5-23
 */
package aurora.database.sql;

public class UpdateField extends FieldWithSource {
    
    /**
     * @param parent
     * @param name
     * @param source_expression
     */
    public UpdateField(ISqlStatement parent, String name,
            String source_expression) {
        super(parent, name, source_expression);
        setType(UPDATE_FIELD);
    }

    /**
     * @param parent
     * @param name
     */
    public UpdateField(ISqlStatement parent, String name) {
        super(parent, name);
        setType(UPDATE_FIELD);
    }

    public static final String UPDATE_FIELD = "UPDATE_FIELD";


}
