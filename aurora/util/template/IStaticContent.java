/*
 * Created on 2007-8-5 12:06:44
 */
package aurora.util.template;

import java.io.Writer;

public interface IStaticContent {
    
    public String getContent();
    
    public void write( Writer out);

}