/*
 * Created on 2008-6-19
 */
package aurora.database.features;

import java.sql.ResultSet;

import uncertain.composite.CompositeMap;
import uncertain.ocm.ISingleton;
import aurora.database.DBUtil;
import aurora.database.IResultSetConsumer;
import aurora.database.ParsedSql;
import aurora.database.SqlRunner;
import aurora.database.profile.IDatabaseFactory;
import aurora.database.service.BusinessModelServiceContext;
import aurora.database.service.ServiceOption;

public class AutoQueryCounter implements ISingleton {
	IDatabaseFactory mFactory;
	
	public AutoQueryCounter(IDatabaseFactory fact){
		mFactory=fact;
	}
	
    public void onQueryFinish(BusinessModelServiceContext context)
        throws Exception
    {
        ServiceOption option = context.getServiceOption();
        if(option==null) return;
        IResultSetConsumer consumer = context.getResultsetConsumer();
        if(consumer==null) return;
        if(option.isAutoCount()){
            long count = 0;       
            StringBuffer sql =null;
            CompositeMap countSql=context.getBusinessModel().getCountSql();
            
            if(countSql!=null){
            	sql = new StringBuffer(countSql.getText());
            	WhereClauseCreator wcCreator=new WhereClauseCreator(mFactory);
            	wcCreator.doPopulateSql(context, sql);
            }else{
            	 StringBuffer oldsql = context.getSqlString();
                 if(oldsql==null) return;
                 sql = new StringBuffer(oldsql.toString());            
                 sql.insert(0, "select count(1) from ( ");
                 sql.append(" ) s");
            }            
           
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
