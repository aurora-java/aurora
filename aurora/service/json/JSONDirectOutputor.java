/*
 * Created on 2008-5-29
 */
package aurora.service.json;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import aurora.database.IResultSetConsumer;

public class JSONDirectOutputor implements IResultSetConsumer {
    
    static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SS");
    
    Writer      output;
    
    DateFormat  dateFormat;
    
    int    rowid = 0;
    int    colid = 0;
    Long   totalCount = null;
    
    public JSONDirectOutputor( Writer w ){
        this(w,DEFAULT_DATE_FORMAT);
    }
    
    /**
     * @param output
     * @param dateFormat
     */
    public JSONDirectOutputor(Writer output, DateFormat dateFormat) {
        super();
        this.output = output;
        this.dateFormat = dateFormat;
    }
    
    public String getJSONString( Object value ){
        if( value == null )
            return "null";
        else if( value instanceof Date)
            return "\""+dateFormat.format((Date)value)+"\"";
        else if( value instanceof Number )
            return value.toString();
        else
            return JSONObject.quote(value.toString());
    }

    void writeObjectBegin( String obj_name )
        throws IOException
    {
        output.write('\"');
        output.write(obj_name);
        output.write("\":");
    }
    
    void writeSilently( String content ){
        try{
            output.write(content);
        }catch(IOException ex){
            handle(ex);
        }
    }
    
    void handle(Exception ex){
        throw new RuntimeException("Can't write JSON content to output");
    }

    public void begin(String root_name) {
        rowid = 0;
        totalCount = null;
        try{
            writeObjectBegin(root_name);
            output.write("[");
        }catch(IOException ex){
            handle(ex);
        }
    }

    public void end() {
        writeSilently("] ");
        if(totalCount!=null){
            writeSilently(", totalCount:"+totalCount);
        }
        try{
            output.flush();
        }catch(IOException ex){
            
        }
    }

    public void endRow() {
        writeSilently("} ");
        rowid++;
    }

    public Object getResult() {
        return output;
    }

    public void loadField(String name, Object value) {        
        StringBuffer buf = new StringBuffer();
        if(colid>0) buf.append(",");
        buf.append("\"").append(name).append("\":");
        buf.append(getJSONString(value));
        writeSilently(buf.toString());
        colid++;
    }

    public void newRow(String row_name) {
        colid=0;
        if(rowid>0)
            writeSilently(",");
        writeSilently("\r\n");
        //writeSilently(row_name);
        writeSilently("\t{ ");
    }

    /**
     * @return the dateFormat
     */
    public DateFormat getDateFormat() {
        return dateFormat;
    }

    /**
     * @param dateFormat the dateFormat to set
     */
    public void setDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }
    
    public void setRecordCount( long count ){
        totalCount = new Long(count);
    }
    

}
