/*
 * Created on 2008-4-26
 */
package aurora.database.sql.builder;

import java.util.Iterator;

import aurora.database.profile.DatabaseProfile;
import aurora.database.sql.CompareExpression;
import aurora.database.sql.ComplexExpression;
import aurora.database.sql.Condition;
import aurora.database.sql.ConditionList;
import aurora.database.sql.ExistsClause;
import aurora.database.sql.ILogicalExpression;
import aurora.database.sql.ISqlStatement;
import aurora.database.sql.Join;
import aurora.database.sql.OracleJoinExpression;
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
        result.append(mRegistry.getSql(exp));
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
        result.append(mRegistry.getSql(clause.getQuery()));
        result.append(") ");
        return result.toString();
    }
    
    public String createSql(CompareExpression exp){
            boolean is_oracle_join = exp instanceof OracleJoinExpression;
            if(DatabaseProfile.isUseJoinKeyword( getDatabaseProfile() ))
                is_oracle_join = false;
            //super.getDatabaseProfile().getProperty(name)
            OracleJoinExpression join = is_oracle_join ? (OracleJoinExpression)exp: null;
            String join_type = is_oracle_join? join.getJoinType(): null;
            if(exp.getLeftField()==null) return "";
            StringBuffer result = new StringBuffer();
            result.append(mRegistry.getSql(exp.getLeftField()));
            if(is_oracle_join){
                if(Join.TYPE_RIGHT_OUTTER_JOIN.equalsIgnoreCase(join_type) || 
                   Join.TYPE_FULL_OUTTER_JOIN.equalsIgnoreCase(join_type))               
                 result.append("(+)").append(' ');
            }
            result.append(' ');
            result.append(CompareExpression.getOperatorText( exp.getOperator() ));
            if(!CompareExpression.isSingleOperator(exp.getOperator())){
                result.append(' ');
                result.append(mRegistry.getSql(exp.getRightField()));
            }        
            if(is_oracle_join){
                if(Join.TYPE_LEFT_OUTTER_JOIN.equalsIgnoreCase(join_type) || 
                   Join.TYPE_FULL_OUTTER_JOIN.equalsIgnoreCase(join_type))               
                 result.append("(+)");
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
