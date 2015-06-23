/*
 * Created on 2008-5-28
 */
package aurora.bm;

import uncertain.composite.DynamicObject;
import uncertain.core.ConfigurationError;
import uncertain.exception.BuiltinExceptionFactory;
import aurora.database.sql.BaseField;
import aurora.database.sql.CompareExpression;
import aurora.database.sql.Condition;
import aurora.database.sql.ConditionList;
import aurora.database.sql.ILogicalExpression;
import aurora.database.sql.ISqlStatement;
import aurora.database.sql.RawSqlExpression;

public class QueryField extends DynamicObject {
    
    public static final String KEY_LOGICAL_OPERATOR = "logicaloperator";

    public static final String KEY_FIELD = "field";
    
    public static final String KEY_NAME = "name";
    
    public static final String KEY_QUERY_OPERATOR = "queryoperator";
    
    public static final String KEY_MATCH_ANY="matchany";
    
    public boolean getMatchAny(){
    	return getBoolean(KEY_MATCH_ANY, false);
    }
    
    public void setMatchAny(boolean value){
    	putBoolean(KEY_MATCH_ANY, value);
    }
    
    public String getQueryOperator(){
        return getString(KEY_QUERY_OPERATOR);
    }
    
    public void setQueryOperator( String op ){
        putString(KEY_QUERY_OPERATOR, op);
    }
    
    public String getField(){
        return getString(KEY_FIELD);
    }
    
    public void setField( String field ){
        putString(KEY_FIELD, field);
    }
    
    public String getQueryExpression(){
        return getString(Field.KEY_QUERY_EXPRESSION);
    }
    
    public void setQueryExpression( String exp ){
        putString( Field.KEY_QUERY_EXPRESSION, exp );
    }
    
    public String getLogicalOperator(){
        return getString(KEY_LOGICAL_OPERATOR);
    }
    
    public void setLogicalOperator( String op){
        putString(KEY_LOGICAL_OPERATOR, op);
    }
    
    /*
    public String getParameterPath(){
        return getString(Field.KEY_PARAMETER_PATH);
    }
    
    public void setParameterPath(String path){
        putString(Field.KEY_PARAMETER_PATH, path);
    }
    */
    
    public String getName(){
        return getString(Field.KEY_NAME);
    }
    
    public void setName(String name){
        putString(Field.KEY_NAME, name);
    }
    
    public void addToWhereClause( ConditionList list, String param_path ){
        addToWhereClause( list, null, param_path);
    }
    
    public void addToWhereClause( ConditionList list, ISqlStatement left_field, String param_path ){
        String op = getQueryOperator();
        ILogicalExpression stmt = null;
        if(op!=null){
            //ISqlStatement left_field = base_field==null ? new RawSqlExpression(getName()) : (ISqlStatement) base_field;
            int op_id = CompareExpression.getOperatorID(op);
            if(op_id<0) throw new ConfigurationError("queryOperator '"+op+"' is invalid in query field config:"+getObjectContext().toXML());
            if(CompareExpression.isSingleOperator(op_id))
                stmt = new CompareExpression( left_field, op_id, null);
            else{
            	String sqlExpression=Field.defaultParamExpression(param_path);
            	if(getMatchAny()&&"like".equalsIgnoreCase(op))
            		sqlExpression="'%'||"+sqlExpression+"||'%'";            	
            	stmt = new CompareExpression( left_field, op_id, new RawSqlExpression(sqlExpression));
            }
        }else{
            String exp = getQueryExpression();
            if(exp!=null) stmt = new RawSqlExpression(exp);
            else
                throw BuiltinExceptionFactory.createOneAttributeMissing(getObjectContext().asLocatable(), "queryOperator,queryExpression");
        }
        String logical_op = getLogicalOperator();
        if(logical_op==null)
            logical_op = Condition.AND;
        if(stmt!=null)
            list.addCondition(logical_op, stmt);
        
    }
}
