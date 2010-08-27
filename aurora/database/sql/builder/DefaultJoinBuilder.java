/*
 * Created on 2010-8-26 下午09:16:10
 * $Id$
 */
package aurora.database.sql.builder;

import aurora.database.sql.ISqlStatement;
import aurora.database.sql.Join;

public class DefaultJoinBuilder extends AbstractSqlBuilder {

    public String createSql(ISqlStatement sqlStatement) {
        if(sqlStatement instanceof Join){
            Join join = (Join)sqlStatement;
            StringBuffer buf = new StringBuffer();
            if(join.getOrder()==0){
                buf.append(mRegistry.getSql(join.getLeftPart()));
            }
            buf.append("\r\n\t").append(join.getType()).append(" ");
            buf.append(mRegistry.getSql(join.getRightPart()));
            buf.append(" ").append(mRegistry.getDatabaseProfile().getKeyword("ON"));
            buf.append(" ");
            buf.append(mRegistry.getSql(join.getJoinConditions()));
            return buf.toString();
        }else
            return null;
    }   

}
