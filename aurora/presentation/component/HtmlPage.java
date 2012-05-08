/*
 * Created on 2009-4-27
 */
package aurora.presentation.component;

import java.io.IOException;
import java.util.Collection;

import uncertain.composite.CompositeMap;
import uncertain.ocm.ISingleton;
import uncertain.util.template.CompositeMapTagCreator;
import uncertain.util.template.ITagContent;
import uncertain.util.template.ITagCreatorRegistry;
import uncertain.util.template.TagCreatorRegistry;
import uncertain.util.template.TextTemplate;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;
import aurora.presentation.component.std.IDGenerator;

public class HtmlPage implements IViewBuilder, ISingleton {
    
    public static final String EVENT_PREPARE_PAGE_CONTENT = "PreparePageContent";

    /**
     * Handle ${page:content} in html page template
     * @author Zhou Fan
     *
     */
    
    public static class PageContentTag implements ITagContent {
        
        public PageContentTag(BuildSession _session, CompositeMap model,
                Collection view_list) {
            this._session = _session;
            this.model = model;
            this.view_list = view_list;
        }

        BuildSession    _session;
        CompositeMap    model;
        Collection      view_list;        
        
        public String getContent(CompositeMap context) {
            try{
                _session.buildViews(model, view_list);
            }catch(Exception ex){
                throw new RuntimeException("error when building page content",ex);
            }
            return null;
        }
    }
    
    public static class HtmlPageTagCreator extends ViewContextTagCreator {
        
        BuildSession    _session;

        public HtmlPageTagCreator(BuildSession _session, ViewContext context ) {
            super(context);
            this._session = _session;
        }

        public ITagContent createInstance(String namespace, String tag) {
            if("content".equals(tag)){
                ViewContext context = getViewContext();
                CompositeMap model = context.getModel();
                Collection view_list = context.getView().getChilds();
                return new PageContentTag( _session, model, view_list );
            }
            else
                return super.createInstance(namespace, tag);
        }
        
    }
    
    
    
    protected ITagCreatorRegistry createTagCreatorRegistry( BuildSession session, ViewContext view_context )
    {
        HtmlPageTagCreator creator = new HtmlPageTagCreator( session, view_context );
        TagCreatorRegistry reg = new TagCreatorRegistry();
        reg.setDefaultCreator(CompositeMapTagCreator.DEFAULT_INSTANCE);
        
        reg.registerTagCreator("page", creator);
        reg.setParent(session.getPresentationManager().getTagCreatorRegistry());
        return reg;
    }
    
    public void buildView(BuildSession session, ViewContext view_context) throws IOException, ViewCreationException {
    	try{
    		CompositeMap model = view_context.getModel();
        	String pageid = IDGenerator.getInstance().generate();
        	//TODO Change title creation method
        	String title = session.getTitle();
        	title = session.getLocalizedPrompt(title);
        	title = uncertain.composite.TextParser.parse(title, model);
        	if(session.getManifest()!=null){
        		view_context.getContextMap().put(TemplateRenderer.KEY_MANIFEST, "manifest=\""+session.getManifest()+"\"");
        	}
        	
        	view_context.getContextMap().put(TemplateRenderer.KEY_TITLE, title);
        	view_context.getContextMap().put("pageid", pageid);
        	view_context.getContextMap().put("contextPath", session.getContextPath());
        	session.getSessionContext().put("pageid", pageid);
            session.fireBuildEvent(EVENT_PREPARE_PAGE_CONTENT, view_context, true);
        }catch(Exception ex){
            throw new ViewCreationException("Error when fire 'PreparePageContent' event",ex);
        }
        ITagCreatorRegistry reg = createTagCreatorRegistry(session, view_context );
        TextTemplate template = TemplateRenderer.getViewTemplate(session, view_context, reg);
        try{
            //template.createOutput(session.getWriter(), view_context.getContextMap());
            template.createOutput(session.getWriter(), view_context.getModel());
        }finally{
            template.clear();
        } 
    }

    public String[] getBuildSteps(ViewContext context) {
        return null;
    }
}
