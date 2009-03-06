/*
 * Created on 2008-3-29
 */
package aurora.database.sql;

public class ExistsClause extends AbstractStatement implements ILogicalExpression {

    public static final String NOT_EXISTS = "NOT EXISTS";
    public static final String EXISTS = "EXISTS";
    
    SelectStatement subQuery;

    public ExistsClause(boolean is_exists, SelectStatement statement) {
        super(is_exists?EXISTS:NOT_EXISTS);
        subQuery = statement;
        subQuery.setParent(this);
    }
    
    public ExistsClause(SelectStatement statement){
        this(true,statement);
    }
    
    public SelectStatement getQuery(){
        return subQuery;
    }

}
