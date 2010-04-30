/*
 * Created on 2008-6-19
 */
package aurora.database.features;

import java.sql.ResultSet;

import uncertain.ocm.ISingleton;
import aurora.database.DBUtil;
import aurora.database.IResultSetConsumer;
import aurora.database.ParsedSql;
import aurora.database.SqlRunner;
import aurora.database.service.ServiceOption;
import aurora.database.service.SqlServiceContext;

public class AutoQueryCounter implements ISingleton {
    
    public void onQueryFinish(SqlServiceContext context)
        throws Exception
    {
        ServiceOption option = context.getServiceOption();
        if(option==null) return;
        IResultSetConsumer consumer = context.getResultsetConsumer();
        if(consumer==null) return;
        if(option.isAutoCount()){
            long count = 0;
            StringBuffer oldsql = context.getSqlString();
            if(oldsql==null) return;
            StringBuffer sql = new StringBuffer(oldsql.toString());            
            sql.insert(0, "select count(1) from ( ");
            sql.append(" ) s");
            ParsedSql s = new ParsedSql(sql.toString());
            SqlRunner runner = new SqlRunner(context, s);  
            runner.setConnectionName(option.getConnectionName());
            runner.setTrace(context.isTrace());
            ResultSet rs = null;
            try{
                rs = runner.query(context.getCurrentParameter());
                if(rs.next()){
                    count = rs.getLong(1);
                }
                consumer.setRecordCount(count);
            }finally{
                DBUtil.closeResultSet(rs);
            }
        }   
        
    }
    

}
