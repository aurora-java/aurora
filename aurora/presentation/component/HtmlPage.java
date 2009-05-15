/*
 * Created on 2009-4-27
 */
package aurora.presentation.component;

import java.io.IOException;

import uncertain.util.template.ITagCreatorRegistry;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;

public class HtmlPage extends TemplateRenderer {
    
    public void buildView(BuildSession session, ViewContext view_context)
            throws IOException, ViewCreationException {
        try{
            session.fireBuildEvent("PreparePageContent", view_context, true);
            super.buildView(session, view_context);
        }catch(Exception ex){
            ex.printStackTrace();
            throw new ViewCreationException(ex);
        }
    }

    protected ITagCreatorRegistry createTagCreatorRegistry(
            BuildSession session, ViewContext view_context) {
        ITagCreatorRegistry reg = super.createTagCreatorRegistry(session, view_context);
        ViewContextTagCreator creator = new ViewContextTagCreator(view_context);
        reg.registerTagCreator("page", creator);
        return reg;
    }

}
