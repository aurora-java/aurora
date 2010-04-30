/*
 * Created on 2009-11-23 下午03:33:50
 * Author: Zhou Fan
 */
package aurora.database.local.oracle.sql;

import java.util.Iterator;

import aurora.database.profile.ISqlBuilder;
import aurora.database.profile.ISqlBuilderRegistry;
import aurora.database.sql.FieldWithSource;
import aurora.database.sql.ISqlStatement;
import aurora.database.sql.InsertStatement;
import aurora.database.sql.builder.DefaultInsertBuilder;

/** Build oracle specific sql */
public class OracleSqlBuilder implements ISqlBuilder {
    
    ISqlBuilderRegistry                     mRegistry;
    DefaultInsertBuilder           mInsertBuilder = new DefaultInsertBuilder();
    
    public String createSql( ReturningIntoStatement stmt ){
        StringBuffer sql = new StringBuffer();
        StringBuffer fields = new StringBuffer();
        StringBuffer into_target = new StringBuffer();
        int i=0;
        for(Iterator it = stmt.getFields().iterator(); it.hasNext(); ){
            FieldWithSource field = (FieldWithSource)it.next();
            if(i>0){
                fields.append(",");
                into_target.append(",");
            }
            fields.append(field.getFieldName());
            fields.append(mRegistry.getSql(field.getUpdateSource()));
            i++;
        }
        if(i==0)
            throw new IllegalArgumentException("ReturningIntoStatement doesn't contain any field");
        sql.append(" returning ");
        sql.append(fields.toString());
        sql.append(" into ");
        sql.append(into_target.toString());
        return sql.toString();
    }
    
    public String createSql( OracleInsertStatement stmt ){        
        StringBuffer sql = new StringBuffer(mInsertBuilder.createSql((InsertStatement)stmt));
        ReturningIntoStatement rt_into = stmt.getReturningInto();
        if(rt_into!=null){
            sql.append(" ");
            sql.append(createSql(rt_into));
        }
        return sql.toString();
    }

    public String createSql(ISqlStatement sqlStatement) {
        if(sqlStatement instanceof ReturningIntoStatement)
            return createSql((ReturningIntoStatement)sqlStatement);
        return null;
    }

    public void setRegistry(ISqlBuilderRegistry registry) {
        mRegistry = registry;
        registerOracleSql();
    }
    
    void registerOracleSql(){
        mRegistry.registerSqlBuilder( ReturningIntoStatement.class, this);
        mRegistry.registerSqlBuilder( OracleInsertStatement.class, this);
    }

}
