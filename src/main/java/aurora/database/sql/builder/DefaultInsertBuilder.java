/*
 * Created on 2008-5-23
 */
package aurora.database.sql.builder;

import java.util.Iterator;

import aurora.database.profile.IDatabaseProfile;
import aurora.database.sql.ISqlStatement;
import aurora.database.sql.InsertField;
import aurora.database.sql.InsertStatement;

public class DefaultInsertBuilder extends AbstractSqlBuilder {
/*    
    public String createSql( UpdateField field ){
        return field.getNameForOperate();
    }
    
    public String createSql( UpdateTarget   target ){
        StringBuffer sql = new StringBuffer();
        sql.append(target.getTableName());
        String alias = target.getAlias();
        if(alias!=null)
            sql.append(" ").append(alias);
        return sql.toString();
    }
    
    public String createUpdateList(List fields){
        StringConcatenater sc = new StringConcatenater();
        Iterator it = fields.iterator();
        while(it.hasNext()){
            UpdateField field = (UpdateField)it.next();
            StringBuffer exp = new StringBuffer();
            exp.append(field.getNameForOperate());
            exp.append("=");
            exp.append(registry.getSql(field.getUpdateSource()));
            sc.append(exp.toString());
        }
        return sc.getContent();
    }
    
    public String createWherePart( UpdateStatement statement ){
        ConditionList where = statement.getWhereClause();
        if(where.size()==0) return "";
        StringBuffer sql = new StringBuffer();
        sql.append(getKeyword(IDatabaseProfile.KEYWORD_WHERE));
        sql.append(" ");
        sql.append(registry.getSql(where));
        return sql.toString();
    }
*/    
    public String createSql( InsertStatement statement ){        
        if(statement.getInsertFields().size()==0)
            throw new IllegalArgumentException("No field defined in insert statement");
        StringBuffer sql = new StringBuffer();
        sql.append(getKeyword(IDatabaseProfile.KEY_INSERT));
        sql.append(" ").append(getKeyword(IDatabaseProfile.KEY_INTO)).append(" ");
        sql.append(mRegistry.getSql(statement.getInsertTable()));
        sql.append(" ( ");
        
        StringBuffer fields = new StringBuffer(), values = new StringBuffer();
        int i=0;
        for(Iterator it = statement.getInsertFields().iterator(); it.hasNext(); ){
            InsertField f = (InsertField)it.next();
            if(i>0){
               fields.append(",");
               values.append(",");
            }
            fields.append(f.getFieldName());
            values.append(mRegistry.getSql(f.getUpdateSource()));
            i++;
        }
        
        sql.append(fields.toString()).append(") ");
        if(statement.getSelectStatement()==null){
            sql.append(getKeyword(IDatabaseProfile.KEY_VALUES)).append(" ( ");
            sql.append(values.toString());
            sql.append(")");
        }else{
            sql.append(mRegistry.getSql(statement.getSelectStatement()));
        }
        return sql.toString();
    }
    
    public String createSql(ISqlStatement statement){
        if(statement instanceof InsertStatement)
            return createSql((InsertStatement)statement);
        else
            return null;
    }

}
