/*
 * Created on 2007-8-4
 */
package aurora.presentation;

import java.io.IOException;
import java.util.Map;

import uncertain.ocm.ISingleton;
import uncertain.util.template.TextTemplate;

public class TemplateBasedView implements IViewBuilder, ISingleton {
    
    public static final String     TEMPLATE_EXT = ".tplt";
    public static final String     KEY_TEMPLATE = "template";
    static final String[]          TEMPLATE_BASED_SEQUENCE = {"LoadTemplate", "CreateViewContent" };    

    public void buildView(BuildSession session, ViewContext view_context) 
        throws IOException, ViewCreationException
    {
        TextTemplate template = view_context.getTemplate();
        if(template==null){
            String template_name = view_context.getView().getName() + TEMPLATE_EXT;
            template = session.getTemplate(template_name);
        }
        if(template==null) throw new ViewCreationException("No template defined during view content creation process");        
        template.createOutput(session.getWriter(), view_context.getContextMap());
    }
    
    public String[] getBuildSteps( ViewContext context ){
        return TEMPLATE_BASED_SEQUENCE;
    }

}
