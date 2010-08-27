/*
 * Created on 2008-3-28
 */
package aurora.database.sql;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Join extends AbstractStatement {
    
    public static final String TYPE_INNER_JOIN = "INNER JOIN";
    public static final String TYPE_LEFT_OUTTER_JOIN = "LEFT OUTER JOIN";    
    public static final String TYPE_RIGHT_OUTTER_JOIN = "RIGHT OUTER JOIN";
    public static final String TYPE_FULL_OUTTER_JOIN = "FULL OUTER JOIN";
    public static final String TYPE_CROSS_JOIN = "CROSS JOIN";
    public static final String NATURAL = "NATURAL";
   
    static Set ALL_TYPES = new HashSet();
    
    static {
        ALL_TYPES.add(TYPE_INNER_JOIN);
        ALL_TYPES.add(TYPE_LEFT_OUTTER_JOIN);
        ALL_TYPES.add(TYPE_RIGHT_OUTTER_JOIN);
        ALL_TYPES.add(TYPE_FULL_OUTTER_JOIN);
        ALL_TYPES.add(TYPE_CROSS_JOIN);
    };

    
    public static boolean isTypeValid(String type){
        if(type==null) return false;
        return ALL_TYPES.contains(type.toUpperCase());
    }


    String              type;
    boolean             isNatural = false;
    SelectSource      leftPart;
    SelectSource      rightPart;
    ConditionList       joinConditions;
    int                 joinOrder = 0;
    
    public Join( String type, SelectSource left, SelectSource right ){
        super(type);
        if(left==null || right==null)
            throw new IllegalArgumentException("Must provide both left part and right part for a join");
        if(!isTypeValid(type))
            throw new IllegalArgumentException("invalid join type:"+type);
        joinConditions = new ConditionList();
        joinConditions.setParent(this);
        setLeftPart(left);
        setRightPart(right);
    }

    /**
     * @return if this join is natural
     */
    public boolean isNatural() {
        return isNatural;
    }

    /**
     * @param set join to be natural
     */
    public void setNatural(boolean isNatural) {
        if(TYPE_CROSS_JOIN.equals(type) && isNatural)
            throw new IllegalArgumentException("Can't set a cross join to natural");
        this.isNatural = isNatural;
    }

    /**
     * @return the left table 
     */
    public SelectSource getLeftPart() {
        return leftPart;
    }

    /**Set left part of this join
     * @param leftPart the left part to set
     */
    public void setLeftPart(SelectSource leftPart) {
        this.leftPart = leftPart;
    }

    /**
     * @return the right part
     */
    public SelectSource getRightPart() {
        return rightPart;
    }

    /**
     * Set right part of this join
     * @param rightPart the right part to set
     */
    public void setRightPart(SelectSource rightPart) {
        this.rightPart = rightPart;
    }
    
    public ConditionList getJoinConditions(){
        return joinConditions;
    }
    
    public void addJoinCondition(ILogicalExpression condition){
        joinConditions.addCondition(condition);
    }
    
    public void addJoinConditions(ILogicalExpression[] condition){
        joinConditions.addConditions(condition);
    }
    
    public void addJoinConditions(Collection condition_list){
        joinConditions.addConditions(condition_list);
    }
    
    public void addJoinField( SelectField left_field, SelectField right_field){        
        //CompareExpression cexp = new CompareExpression(left_field, CompareExpression.EQUAL, right_field);
        OracleJoinExpression cexp = new OracleJoinExpression( getType(), left_field, CompareExpression.EQUAL, right_field);
        addJoinCondition(cexp);
    }

    /** A int number to identify its index number in a join list, starts from 0  */
    public int getOrder() {
        return joinOrder;
    }

    public void setOrder(int joinOrder) {
        this.joinOrder = joinOrder;
    }
    
    
}
