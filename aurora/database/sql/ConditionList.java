/*
 * Created on 2008-3-28
 */
package aurora.database.sql;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ConditionList extends AbstractStatement implements ILogicalExpression {

    public static final String CONDITION_LIST = "CONDITION_LIST";
    
    // List<Condition>
    List  condition_list;

    public ConditionList() {        
        super(CONDITION_LIST);
        condition_list = new LinkedList();
    }
    
    public ConditionList(ILogicalExpression[] conditions){
        this();
        for(int i=0; i<conditions.length; i++){
            addCondition(conditions[i]);
        }
    }
    
    public void addCondition(Condition condition){
        condition.setParent(this);
        condition_list.add(condition);
    }
    
    public void addCondition(String operator, ILogicalExpression expression){
        addCondition( new Condition(operator, expression));
    }
    
    public void addCondition(ILogicalExpression expression){
        addCondition(Condition.AND, expression);
    }
    
    public void addCondition(String raw_expression){
        addCondition( new RawSqlExpression(raw_expression));
    }
    
    public void addConditions(ILogicalExpression[] expressions){
        for(int i=0; i<expressions.length; i++)
            addCondition(expressions[i]);
    }
    
    public void addConditions(Collection condition_list){
        Iterator it = condition_list.iterator();
        while(it.hasNext()){
            ILogicalExpression exp = (ILogicalExpression)it.next();
            addCondition(exp);
        }
    }
    
    public void addEqualExpression(ISqlStatement left, ISqlStatement right){
        CompareExpression comp_exp = new CompareExpression(left, CompareExpression.EQUAL,  right);
        addCondition(comp_exp);
    }
    
    public boolean removeCondition(Condition c){
        int index = condition_list.indexOf(c);
        if(index<0) return false;
        ILogicalExpression exp = (ILogicalExpression)condition_list.get(index);
        exp.setParent(null);
        condition_list.remove(index);
        return true;
    }
    
    /**
     * @return An unmodifiable version of contained conditions
     */
    public List getConditions(){
        return Collections.unmodifiableList(condition_list);
    }
    
    public int size(){
        return condition_list.size();
    }
    

}
