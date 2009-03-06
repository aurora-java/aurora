/*
 * Created on 2008-3-28
 */
package aurora.database.sql;

public class CompareExpression extends AbstractStatement implements ILogicalExpression {
    
    public static final int EQUAL               = 1;    
    public static final int NOT_EQUAL           = 2;
    public static final int LESS_THAN           = 3;
    public static final int LESS_OR_EQUAL       = 4;
    public static final int GREATER_THAN        = 5;
    public static final int GREATER_OR_EQUAL    = 6;
    public static final int LIKE                = 7;
    public static final int NOT_LIKE            = 8;
    public static final int IS_NULL             = 9;
    public static final int IS_NOT_NULL         = 10;
    
    static final String[] DEFAULT_OPERATOR_TEXT_ARRAY = {
      "",
      "=",
      "<>",
      "<",
      "<=",
      ">",
      ">=",
      "LIKE",
      "NOT LIKE",
      "IS NULL",
      "IS NOT NULL"      
    };
    
    public static boolean isValidOperator(int operator){
        if(operator<EQUAL||operator>IS_NOT_NULL)
            return false;
        return true;
    }
    
    public static int getOperatorID( String op_text ){
        for(int i=1; i<DEFAULT_OPERATOR_TEXT_ARRAY.length; i++){
            if( DEFAULT_OPERATOR_TEXT_ARRAY[i].equalsIgnoreCase(op_text))
                return i;
        }
        return -1;
    }
    
    public static boolean isSingleOperator(int operator){
        return operator==IS_NULL || operator==IS_NOT_NULL;
    }
    
    public static String getOperatorText(int operator){
        return DEFAULT_OPERATOR_TEXT_ARRAY[operator];
    }    
    
    public static final String EXPRESSION = "EXPRESSION";
    
    ISqlStatement          leftField;
    ISqlStatement          rightField;
    int                 operator;
    
    /**
     * @param type
     * @param leftField
     * @param rightField
     * @param operator
     */
    public CompareExpression(ISqlStatement leftField, int operator, ISqlStatement rightField ) {
        super(EXPRESSION);
        this.leftField = leftField;
        this.rightField = rightField;
        this.operator = operator;
    }

    /**
     * @return the leftField
     */
    public ISqlStatement getLeftField() {
        return leftField;
    }

    /**
     * @param leftField the leftField to set
     */
    public void setLeftField(ISqlStatement leftField) {
        this.leftField = leftField;
    }

    /**
     * @return the operator
     */
    public int getOperator() {
        return operator;
    }

    /**
     * @param operator the operator to set
     */
    public void setOperator(int operator) {
        if(!isValidOperator(operator))
            throw new IllegalArgumentException("Illegal operator: "+operator);
        this.operator = operator;
    }

    /**
     * @return the rightField
     */
    public ISqlStatement getRightField() {
        return rightField;
    }

    /**
     * @param rightField the rightField to set
     */
    public void setRightField(ISqlStatement rightField) {
        this.rightField = rightField;
    }

}
