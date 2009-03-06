/*
 * Created on 2007-12-28
 */
package aurora.database;

public interface IResultSetConsumer {
    
    public void begin( String root_name );
    
    public void newRow( String row_name );
    
    public void loadField( String name, Object value );
    
    public void endRow();
    
    public void end();
    
    public void setRecordCount( long count );
    
    public Object getResult();

}
