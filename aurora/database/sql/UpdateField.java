/*
 * Created on 2008-5-23
 */
package aurora.database.sql;

public class UpdateField extends BaseField {
    
    public static final String UPDATE_FIELD = "UPDATE_FIELD";
    ISqlStatement   updateSource;
    
    public UpdateField( ISqlStatement parent, String name ){
        super(UPDATE_FIELD);
        setParent(parent);
        setFieldName(name);
    }
    
    public UpdateField( ISqlStatement parent, String name, String source_expression ){
        this(parent,name);
        setUpdateSource( new RawSqlExpression(source_expression));
    }
    
    public ISqlStatement getUpdateSource(){
        return updateSource;
    }
    
    public void setUpdateSource(ISqlStatement update_source){
        this.updateSource = update_source;
    }

}
