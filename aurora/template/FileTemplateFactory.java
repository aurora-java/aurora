/*
 * Created on 2007-8-15 ÏÂÎç10:50:46
 */
package aurora.template;

import java.io.IOException;
import java.io.InputStream;

import aurora.util.template.ITemplateFactory;
import aurora.util.template.TagTemplateParser;
import aurora.util.template.TextTemplate;

public class FileTemplateFactory implements ITemplateFactory {
    
    TagTemplateParser parser = new TagTemplateParser();
    ClassLoader       class_loader = FileTemplateFactory.class.getClassLoader();
    
    public TextTemplate getTemplate( String template_name ) throws IOException {
        InputStream is = class_loader.getResourceAsStream("aurora/resource/ui/"+template_name+".html");
        return parser.buildTemplate(is);
    }

}
