/*
 * Created on 2008-3-27
 */
package aurora.database.sql;

/**
 * Interface to define SQL expression, fragment or complete statement 
 * @author Zhou Fan
 *
 */
public interface ISqlStatement {
    
    /** A string to identify statement type, such as "select", "update", etc */
    public String   getType();
    
    /** Get owner of this statement */
    public ISqlStatement   getParent();
    
    /** Set parent statement that owns this statement */
    public void setParent(ISqlStatement parent);
    
}
