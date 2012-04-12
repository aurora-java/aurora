package aurora.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import uncertain.composite.CompositeMap;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import aurora.database.DatabaseConstant;
import aurora.database.service.SqlServiceContext;
import aurora.events.E_TransactionCommit;
import aurora.events.E_TransactionRollBack;
import aurora.service.IService;

public class UserTransactionImpl implements UserTransaction{
	//Connection mConn;
    SqlServiceContext context;
    ILogger           logger;
/*    
	public void initialize(IService svc){		
		context = (SqlServiceContext) svc
		.getServiceContext().castTo(SqlServiceContext.class);
		logger = LoggingContext.getLogger(context.getObjectContext(), DatabaseConstant.AURORA_DATABASE_LOGGING_TOPIC);
	}
*/	
	public void setContext( CompositeMap map ){
	    context = SqlServiceContext.createSqlServiceContext(map);
        logger = LoggingContext.getLogger(map, DatabaseConstant.AURORA_DATABASE_LOGGING_TOPIC);
	}
	
	public void commit() throws RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SecurityException,
			IllegalStateException, SystemException {		
	    Set conn_set = context.getAllConnection();
	    if(conn_set==null)
	        return;
	    Iterator it = conn_set.iterator();
	    while(it.hasNext()){
	        Connection conn = (Connection)it.next();
	        try{
	            conn.commit();
	        }catch(SQLException ex){
	            logger.severe("Error when commit connection:"+ex.getMessage());
	        }	        
	    }
	    context.fireEvent(E_TransactionCommit.EVENT_NAME);
	}
	
	public void rollback() throws IllegalStateException, SecurityException,
	SystemException {
        Set conn_set = context.getAllConnection();
        if(conn_set==null)
            return;        
        Iterator it = conn_set.iterator();
        while(it.hasNext()){
            Connection conn = (Connection)it.next();
            try{
                conn.rollback();
            }catch(SQLException ex){
                logger.severe("Error when rollback connection:"+ex.getMessage());
            }           
        }
        context.fireEvent(E_TransactionRollBack.EVENT_NAME);
	}
	public int getStatus() throws SystemException {
		return 0;
	}	
	public void begin() throws NotSupportedException, SystemException {}
	public void setRollbackOnly() throws IllegalStateException, SystemException {}
	public void setTransactionTimeout(int arg0) throws SystemException {}

}
