/*
 * Created on 2010-3-31
 * $Id$
 */
package aurora.database.sql.builder;

import aurora.database.profile.IDatabaseProfile;
import aurora.database.sql.ConditionList;
import aurora.database.sql.DeleteStatement;
import aurora.database.sql.ISqlStatement;
import aurora.database.sql.UpdateField;
import aurora.database.sql.UpdateStatement;
import aurora.database.sql.UpdateTarget;

public class DefaultDeleteBuilder extends AbstractSqlBuilder {
    
    public String createWherePart( DeleteStatement statement ){
        ConditionList where = statement.getWhereClause();
        if(where.size()==0) return "";
        StringBuffer sql = new StringBuffer();
        sql.append(getKeyword(IDatabaseProfile.KEYWORD_WHERE));
        sql.append(" ");
        sql.append(mRegistry.getSql(where));
        return sql.toString();
    }
    
    public String createSql( DeleteStatement statement ){
        UpdateTarget target = statement.getUpdateTarget();
        if(target.getAlias()==null)
            target.setAlias("t");
        StringBuffer sql = new StringBuffer();
        sql.append(getKeyword(IDatabaseProfile.KEY_DELETE)).append(" ").append(IDatabaseProfile.KEY_FROM).append(" ");
        sql.append(mRegistry.getSql(statement.getUpdateTarget())).append(" ");
        sql.append("\r\n");
        sql.append(createWherePart(statement));
        return sql.toString();
    }
    
    public String createSql(ISqlStatement statement){
        if(statement instanceof DeleteStatement)
            return createSql((DeleteStatement)statement);
        else
            return null;
    }

}
