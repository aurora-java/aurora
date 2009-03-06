/*
 * Created on 2007-8-15 ÏÂÎç10:50:46
 */
package aurora.util.template;

import java.io.IOException;
import java.io.InputStream;

public class ResourceTemplateFactory implements ITemplateFactory {
    
    String      basePath;
    
    TagTemplateParser parser = new TagTemplateParser();
    ClassLoader       class_loader = Thread.currentThread().getContextClassLoader(); 
    
    public ResourceTemplateFactory(String basePath){
        this.basePath = basePath;
    }
    
    public TextTemplate getTemplate( String template_name ) throws IOException {
        InputStream is = class_loader.getResourceAsStream(basePath+template_name+".html");
        return parser.buildTemplate(is);
    }

}
