/*
 * Created on 2008-5-23
 */
package aurora.database.sql;

public abstract class FieldWithSource extends BaseField {
    
    public static final String FIELD_WITH_SOURCE = "FIELD_WITH_SOURCE";
    ISqlStatement   updateSource;
    
    public FieldWithSource( ISqlStatement parent, String name ){
        super(FIELD_WITH_SOURCE);
        setParent(parent);
        setFieldName(name);
    }
    
    public FieldWithSource( ISqlStatement parent, String name, String source_expression ){
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
