/*
 * Created on 2010-5-26 上午11:20:57
 * $Id$
 */
package aurora.database.sql.builder;

import java.util.Iterator;

import aurora.database.profile.IDatabaseProfile;
import aurora.database.profile.ISqlBuilderRegistry;
import aurora.database.sql.CompositeStatement;
import aurora.database.sql.ISqlStatement;

public class DefaultCompositeBuilder extends AbstractSqlBuilder {
    
    String  mStatementPrefix = null;
    String  mStatementSeparator = null;
    String  mStatementPostfix = null;
    boolean inited = false;

    public DefaultCompositeBuilder() {
        super();
    }    
    
    private void checkInit(){
        if(inited) return;
        if(getDatabaseProfile()==null)
            return;
        mStatementPrefix = getKeywordWithNull("composite_statement_prefix");
        mStatementSeparator = getKeywordWithNull("composite_statement_separator");
        mStatementPostfix = getKeywordWithNull("composite_statement_postfix");
        inited = true;
    }

    public void setRegistry(ISqlBuilderRegistry registry) {
        super.setRegistry(registry);        
    }    

    public String createSql(ISqlStatement sqlStatement) {
        checkInit();
        if(sqlStatement instanceof CompositeStatement){
            Iterator it = ((CompositeStatement)sqlStatement).getStatements().iterator();
            StringBuffer sql = new StringBuffer();
            while(it.hasNext()){
                ISqlStatement stmt = (ISqlStatement)it.next();
                String s = mRegistry.getSql(stmt);
                if(s!=null){
                    sql.append(s);
                    if(mStatementSeparator!=null && !s.endsWith(mStatementSeparator))
                        sql.append(mStatementSeparator);
                }
            }
            if(sql.length()>0){
                if(mStatementPrefix!=null)
                    sql.insert(0,mStatementPrefix);
                if(mStatementPostfix!=null)
                    sql.append(mStatementPostfix);
            }
            return sql.toString();
        } else
            return null;
    }
    
    

}
