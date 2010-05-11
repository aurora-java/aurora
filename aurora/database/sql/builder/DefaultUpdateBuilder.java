/*
 * Created on 2008-5-23
 */
package aurora.database.sql.builder;

import java.util.Iterator;
import java.util.List;

import aurora.database.profile.IDatabaseProfile;
import aurora.database.sql.ConditionList;
import aurora.database.sql.ISqlStatement;
import aurora.database.sql.StringConcatenater;
import aurora.database.sql.UpdateField;
import aurora.database.sql.UpdateStatement;
import aurora.database.sql.UpdateTarget;

public class DefaultUpdateBuilder extends AbstractSqlBuilder {
    
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
            exp.append(mRegistry.getSql(field.getUpdateSource()));
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
        sql.append(mRegistry.getSql(where));
        return sql.toString();
    }
    
    public String createSql( UpdateStatement statement ){
        if(statement.getUpdateFields().size()==0)
            throw new IllegalArgumentException("No field defined in update statement");
        UpdateTarget target = statement.getUpdateTarget();
        if(target.getAlias()==null)
            target.setAlias("t");
        StringBuffer sql = new StringBuffer();
        sql.append(getKeyword(IDatabaseProfile.KEY_UPDATE));
        sql.append(" ");
        sql.append(mRegistry.getSql(statement.getUpdateTarget()));
        sql.append("\r\n");
        sql.append(getKeyword(IDatabaseProfile.KEY_SET)).append(" ");
        sql.append(createUpdateList(statement.getUpdateFields()));
        sql.append("\r\n");
        sql.append(createWherePart(statement));
        return sql.toString();
    }
    
    public String createSql(ISqlStatement statement){
        if(statement instanceof UpdateStatement)
            return createSql((UpdateStatement)statement);
        else if( statement instanceof UpdateTarget){
            return createSql((UpdateTarget)statement);
        }        
        else if( statement instanceof UpdateField){
            return createSql((UpdateField)statement);
        }
        else
            return null;
    }

}
