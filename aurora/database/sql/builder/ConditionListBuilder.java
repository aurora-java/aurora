/*
 * Created on 2008-4-26
 */
package aurora.database.sql.builder;

import java.util.Iterator;

import aurora.database.sql.CompareExpression;
import aurora.database.sql.ComplexExpression;
import aurora.database.sql.Condition;
import aurora.database.sql.ConditionList;
import aurora.database.sql.ExistsClause;
import aurora.database.sql.ILogicalExpression;
import aurora.database.sql.ISqlStatement;
import aurora.database.sql.RawSqlExpression;

public class ConditionListBuilder extends AbstractSqlBuilder {
    
    protected String createSql(Condition condition, boolean first){
        StringBuffer result = new StringBuffer();
        if(!first){
            result.append(" ");
            result.append(condition.getLogicalOperator());
            result.append(" ");
        }
        ILogicalExpression exp = condition.getExpression();
        boolean use_parentheses = exp instanceof ConditionList;
        if(use_parentheses) use_parentheses = ((ConditionList)exp).size()>1;
        if(use_parentheses)
            result.append(" (");
        result.append(registry.getSql(exp));
        if(use_parentheses)
            result.append(") ");
        return result.toString();
    }
    
    public String createSql(ComplexExpression exp){
        return exp.toSql();
    }
    
    public String createSql(RawSqlExpression exp){
        return exp.toSql();
    }
    
    public String createSql(ExistsClause clause){
        StringBuffer result = new StringBuffer();
        result.append(clause.getType());
        result.append(" (");
        result.append(registry.getSql(clause.getQuery()));
        result.append(") ");
        return result.toString();
    }
    
    public String createSql(CompareExpression exp){
            if(exp.getLeftField()==null) return "";
            StringBuffer result = new StringBuffer();
            result.append(registry.getSql(exp.getLeftField()));
            result.append(' ');
            result.append(CompareExpression.getOperatorText( exp.getOperator() ));
            if(!CompareExpression.isSingleOperator(exp.getOperator())){
                result.append(' ');
                result.append(registry.getSql(exp.getRightField()));
            }        
            return result.toString();
    }
    
    public String createSql(ConditionList list){
        int id = 0;
        StringBuffer result = new StringBuffer();        
        Iterator it = list.getConditions().iterator();
        while(it.hasNext()){
            Condition condition = (Condition)it.next();
            result.append(createSql(condition, id==0));
            id++;
        }
        return result.toString();        
    }

    public String createSql(ISqlStatement sqlStatement) {
        if(sqlStatement instanceof ConditionList){
            return createSql((ConditionList)sqlStatement);
        }else if(sqlStatement instanceof CompareExpression)
            return createSql((CompareExpression)sqlStatement);
        else if(sqlStatement instanceof ExistsClause)
            return createSql((ExistsClause)sqlStatement);
        else if(sqlStatement instanceof ComplexExpression)
            return createSql((ComplexExpression)sqlStatement);
        else if(sqlStatement instanceof RawSqlExpression)
            return createSql((RawSqlExpression)sqlStatement);        
        else
            return null;        
    }

}
