/*
 * Created on 2007-7-31
 */
package aurora.presentation;

import java.io.IOException;

/**
 * This interface defines general behavior for class that can do view content generation
 * @author Zhou Fan
 *
 */
public interface IViewBuilder {
    
    /**
     * Build view content
     * @param session Current view build session
     * @param model Current model associated with this view
     * @param view_context Container to hold variables generated during build process for this view
     */
    public void buildView( BuildSession session,  ViewContext view_context ) 
        throws IOException, ViewCreationException;
    
    public String[] getBuildSteps( ViewContext context );    

}
