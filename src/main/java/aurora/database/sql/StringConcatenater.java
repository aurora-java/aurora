/*
 * Created on 2008-4-10
 */
package aurora.database.sql;

public class StringConcatenater {
    
    StringBuffer    buf;
    String          separator;
    int             index = 0;
    
    public StringConcatenater(){
        this(",");
    }
    
    public StringConcatenater(String separator){
        buf = new StringBuffer();
        this.separator = separator;
    }
    
    public void clear(){
        buf.setLength(0);
        index = 0;
    }
    
    public void setSeparatorChar( String s ){
        this.separator = s;
    }
    
    public void append(String content){
        if(index>0)
            buf.append(separator);
        buf.append(content);
        index++;
    }
    
    public String getContent(){
        return buf.toString();
    }

}
