package aurora.transaction;

import java.sql.Connection;
import java.sql.SQLException;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import aurora.database.service.SqlServiceContext;
import aurora.service.IService;

public class UserTransactionImpl implements UserTransaction{
	Connection mConn;		
	public void initialize(IService svc){		
		SqlServiceContext context = (SqlServiceContext) svc
		.getServiceContext().castTo(SqlServiceContext.class);
		this.mConn=context.getConnection();
	}
	public void clear(){
		mConn=null;
	}
	public void commit() throws RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SecurityException,
			IllegalStateException, SystemException {		
		try {
			mConn.commit();
		} catch (SQLException e) {			
			throw new RuntimeException("Error when doing commit", e);
		}
	}
	public void rollback() throws IllegalStateException, SecurityException,
	SystemException {
		try {
			mConn.rollback();
		} catch (SQLException e) {		
			throw new RuntimeException("Error when doing rollback", e);
		}
	}
	public int getStatus() throws SystemException {
		return 0;
	}	
	public void begin() throws NotSupportedException, SystemException {}
	public void setRollbackOnly() throws IllegalStateException, SystemException {}
	public void setTransactionTimeout(int arg0) throws SystemException {}

}
