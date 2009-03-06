/*
 * Created on 2007-8-4
 */
package aurora.presentation;

import java.io.IOException;
import java.util.Map;

import uncertain.ocm.ISingleton;

import aurora.util.template.CompositeContentProvider;
import aurora.util.template.ITemplateFactory;
import aurora.util.template.MapContentProvider;
import aurora.util.template.TextTemplate;

public class TemplateBasedView implements IViewBuilder, ISingleton {
    
    public static final String     KEY_TEMPLATE = "template";
    static final String[]          TEMPLATE_BASED_SEQUENCE = {"LoadTemplate", "CreateViewContent" };    

    public void buildView(BuildSession session, ViewContext view_context) 
        throws IOException, ViewCreationException
    {
        Map content = view_context.getMap();
        //CompositeContentProvider cp = new CompositeContentProvider();
        //cp.register();
        MapContentProvider provider = new MapContentProvider(content);
        TextTemplate template = view_context.getTemplate();
        if(template==null){
            /*
            String template_name = view_context.getView().getName();
            ITemplateFactory fact = session.getPresentationManager().getTemplateFactory();
            template = fact.getTemplate(template_name);
            */
            //Component c = session.getPresentationManager().getComponent(view_context.getView());
            //c.getOwner().getResource
        }
        if(template==null) throw new ViewCreationException("No template defined during view content creation process");        
        template.createOutput(session.getWriter(), provider);
    }
    
    public String[] getBuildSteps( ViewContext context ){
        return TEMPLATE_BASED_SEQUENCE;
    }

}
