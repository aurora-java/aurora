/*
 * Created on 2008-3-28
 */
package aurora.database.sql;

public class Condition extends AbstractStatement {
    
    public static final String CONDITION = "CONDITION";
    public static final String AND = "AND";
    public static final String OR = "OR";
    /*
    public static final String EXISTS = "EXISTS";
    public static final String NOT_EXISTS = "NOT EXISTS";
    */
    String                  logicalOperator;
    ILogicalExpression      expression;
    
    public Condition(String logicalOperator, ILogicalExpression expression){
        super(CONDITION);
        setLogicalOperator(logicalOperator);
        setExpression(expression);
    }
    
    public Condition(ILogicalExpression expression){
        this(AND,expression);
    }

    /**
     * @return the expression
     */
    public ILogicalExpression getExpression() {
        return expression;
    }

    /**
     * @param expression the expression to set
     */
    public void setExpression(ILogicalExpression expression) {        
        expression.setParent(this);
        this.expression = expression;        
    }

    /**
     * @return the logicalOperator
     */
    public String getLogicalOperator() {
        return logicalOperator;
    }

    /**
     * @param logicalOperator the logicalOperator to set
     */
    public void setLogicalOperator(String logicalOperator) {
        this.logicalOperator = logicalOperator;
    }

}
