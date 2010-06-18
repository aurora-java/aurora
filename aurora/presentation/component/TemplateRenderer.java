/*
 * Created on 2009-4-27
 */
package aurora.presentation.component;

import java.io.File;
import java.io.IOException;

import uncertain.composite.CompositeMap;
import uncertain.logging.ILogger;
import uncertain.ocm.ISingleton;
import uncertain.util.template.ITagCreatorRegistry;
import uncertain.util.template.TagCreatorRegistry;
import uncertain.util.template.TagTemplateParser;
import uncertain.util.template.TextTemplate;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.PresentationManager;
import aurora.presentation.TemplateBasedView;
import aurora.presentation.ViewComponentPackage;
import aurora.presentation.ViewConfigurationError;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;

/**
 * Implements &lt;template&gt; tag
 * TemplateRenderer
 * @author Zhou Fan
 *
 */
public class TemplateRenderer implements IViewBuilder, ISingleton {
    
    public static final String KEY_PACKAGE = "package";
    public static final String KEY_TEMPLATE = "template";
    //ResourceTemplateFactory     mFactory;
    TagTemplateParser             mTemplateParser;
    
    public TemplateRenderer(){
        mTemplateParser = new TagTemplateParser();
    }
    
    protected static TextTemplate getViewTemplate( BuildSession session, ViewContext view_context, ITagCreatorRegistry tag_reg )
        throws ViewCreationException, IOException
    {
        PresentationManager prm = session.getPresentationManager();
        // Get template name
        CompositeMap view = view_context.getView();
        String name = view.getString(KEY_TEMPLATE);
        if(name==null) throw new ViewConfigurationError("'template' must be set");
        if(name.indexOf('.')<0)
            name += TemplateBasedView.TEMPLATE_EXT;
        // Get package name
        ViewComponentPackage pkg = null;
        String pkg_name = view.getString(KEY_PACKAGE);
        if(pkg_name==null)
            pkg = session.getCurrentPackage();
        else{
            pkg = session.getPresentationManager().getPackage(pkg_name);
            if(pkg==null) throw new ViewCreationException("Can't load package "+pkg_name);
        }
        // Get template file
        File template_file = pkg.getTemplateFile(session.getTheme(), name);
        if(template_file==null)
            throw new ViewCreationException("Can't load template "+name);
        // Parse template file
        TagTemplateParser parser = prm.getTemplateParser();
        return parser.buildTemplate(template_file, tag_reg);
    }
    
    protected ITagCreatorRegistry createTagCreatorRegistry( BuildSession session, ViewContext view_context )
    {
        TagCreatorRegistry reg = new TagCreatorRegistry();
        ViewPartTagCreator creator = new ViewPartTagCreator(session, view_context);
        reg.setDefaultCreator(creator);
        reg.setParent(session.getPresentationManager().getTagCreatorRegistry());
        return reg;
    }
    
    public void buildView(BuildSession session, ViewContext view_context)
            throws IOException, ViewCreationException 
    {
        ITagCreatorRegistry reg = createTagCreatorRegistry(session, view_context );
        TextTemplate template = getViewTemplate(session, view_context, reg);
        try{
            template.createOutput(session.getWriter(), view_context.getContextMap());
        }finally{
            template.clear();
        }
    }

    public String[] getBuildSteps(ViewContext context) {
        return null;
    }

}
