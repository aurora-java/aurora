/*
 * Created on 2010-8-27 下午12:43:23
 * $Id$
 */
package aurora.database.sql;


/**
 * A nature join expression, such as
 * t1.department_id = t2.record_id (+)
 */
public class OracleJoinExpression extends CompareExpression {
    
    String      joinType;

    /**
     * @param leftField
     * @param operator
     * @param rightField
     */
    public OracleJoinExpression(String join_type, ISqlStatement leftField, int operator,
            ISqlStatement rightField) {
        super(leftField, operator, rightField);
        setJoinType(join_type);
    }

    public String getJoinType() {
        return joinType;
    }

    public void setJoinType(String joinType) {
        this.joinType = joinType;
    }

}
