/*
 * Created on 2008-3-28
 */
package aurora.database.sql;

public class RawSqlExpression extends AbstractStatement implements ISimpleSqlText, ILogicalExpression {
    
    public static final String EXPRESSION = "EXPRESSION";    
    
    String  expressionText;
    
    public RawSqlExpression(String expressionText) {
        super(EXPRESSION);
        this.expressionText = expressionText;
    }

    /**
     * @return the expressionText
     */
    public String getExpressionText() {
        return expressionText;
    }

    /**
     * @param expressionText the expressionText to set
     */
    public void setExpressionText(String expressionText) {
        this.expressionText = expressionText;
    }
    
    public String toSql(){
        return expressionText;
    }
    
   

}
