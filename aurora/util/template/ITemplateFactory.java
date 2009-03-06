/*
 * Created on 2007-8-8 обнГ10:18:54
 */
package aurora.util.template;

import java.io.IOException;

/**
 * Create template instance by template name
 * @author Zhou Fan
 *
 */
public interface ITemplateFactory {
    
    public TextTemplate getTemplate( String template_name ) throws IOException;

}
