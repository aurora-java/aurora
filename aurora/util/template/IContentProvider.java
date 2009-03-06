/*
 * Created on 2007-8-5
 */
package aurora.util.template;

import java.io.IOException;
import java.io.Writer;

/**
 * Get template content based 
 * IContentProvider
 * @author Zhou Fan
 *
 */
public interface IContentProvider {
    
    public boolean accepts( IDynamicContent tag );    
    
    public Object createContent(IDynamicContent content);
    
    //public void write(Writer writer, IDynamicContent content) throws IOException;

}
