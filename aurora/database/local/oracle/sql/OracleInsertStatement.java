/*
 * Created on 2010-4-29 下午04:43:18
 * $Id$
 */
package aurora.database.local.oracle.sql;

import aurora.database.DatabaseConstant;
import aurora.database.sql.InsertStatement;

public class OracleInsertStatement extends InsertStatement {
    
    ReturningIntoStatement      mReturningInto;
    
    public OracleInsertStatement( InsertStatement another ){
        super(another);
    }

    public OracleInsertStatement(String table_name) {
        super(table_name);
    }
    
    public ReturningIntoStatement getReturningInto(){
        return mReturningInto;
    }
    
    public void setReturningInto( ReturningIntoStatement stmt ){
        mReturningInto = stmt;        
    }

}
