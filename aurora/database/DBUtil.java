/**
 * Created on: 2003-9-11 10:36:38
 * Author:     zhoufan
 */
package aurora.database;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.logging.Level;

import uncertain.composite.CompositeMap;
import uncertain.logging.ILogger;

/**
 *  quietly close jdbc connection, statement, etc
 */
public class DBUtil {
	
	public static void closeConnection( Connection conn ){
		if( conn == null) return;
		try{
			conn.close();
		} catch(SQLException ex){
			
		}
	}
	
	public static void closeResultSet( ResultSet rs ){
		if( rs == null) return;
		try{
			rs.close();
		} catch(SQLException ex){
			
		}
	}
	
	public  static void closeStatement( Statement stmt ){
		if( stmt == null) return;
		try{
			stmt.close();
		} catch(SQLException ex){
			
		}
	}
    
     public static void printTraceInfo( String type, PrintWriter out, SqlRunner runner){
            out.println("============= [" + type + "] SQL Statement execution dump ============");
            out.println("Execution Date:"+new Date());
            out.println("------------------------------------------------------");  
            out.println("=== Parsed SQL ===");
            out.println(runner.getStatement().getParsedSQL());
            out.println("------------------------------------------------------");  
            out.println("=== Parameters ===");
            CompositeMap map = runner.getSqlServiceContext().getCurrentParameter();
            if(map!=null)
                out.println(map.toXML());
            else
                out.println("(null)");
            out.println("------------------------------------------------------");
            out.println("=== Binding info ===");
            out.println(runner.getBindDescription());
            out.print("================== END of [" + type + "]==================");
            out.println();
            out.flush();
        }

     public static void printTraceInfo( String type, ILogger logger, SqlRunner runner){
         logger.log(Level.CONFIG, "============= [{0}] SQL Statement execution dump ============", new Object[]{type});
         logger.config("=== Parsed SQL ===");
         logger.config(runner.getStatement().getParsedSQL());
         logger.config("------------------------------------------------------");
         logger.config("=== Binding info ===");
         logger.config(runner.getBindDescription());
         logger.config("================== END of [" + type + "]==================");
         logger.config("");
     }
     
}
