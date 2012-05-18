/*
 * Created on 2007-8-4
 */
package aurora.presentation;

import java.io.IOException;

import uncertain.cache.CacheFactoryConfig;
import uncertain.cache.ICache;
import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.ISingleton;
import uncertain.util.template.TextTemplate;

public class TemplateBasedView implements IViewBuilder, ISingleton {
    
    public static final String CACHE_NAME = "ViewComponentTemplates";
    public static final String     TEMPLATE_EXT = ".tplt";
    public static final String     KEY_TEMPLATE = "template";
    static final String[]          TEMPLATE_BASED_SEQUENCE = {"LoadTemplate", "CreateViewContent" };  
    
    ICache      mCache;
    boolean     mIsCacheEnabled = false;
    String      mCacheName;
    /*
    public TemplateBasedView(){
        
    }
    */
    
    public TemplateBasedView( UncertainEngine engine ){
        initCache(engine);
    }
    
    private void initCache( UncertainEngine engine ){
        IObjectRegistry reg = engine.getObjectRegistry();
        mCache = CacheFactoryConfig.getNamedCache(reg, CACHE_NAME);
        mIsCacheEnabled = mCache!=null;
        /*
        mIsCacheEnabled = engine.getCacheConfigFiles();
        if( mIsCacheEnabled ){
            INamedCacheFactory cf = engine.getNamedCacheFactory();
            if(cf!=null){
                mCache = cf.getNamedCache(engine.getMBeanName("cache","name=ViewComponentTemplates"));
            }
        }
        */
    }
    
    // if cached is enabled, try to get cached template
    private TextTemplate getTemplate(String name, BuildSession session )
        throws IOException
    {
        if(!mIsCacheEnabled || mCache==null)
            return session.getTemplateByName(name);
        String pkg_name  = session.getCurrentPackage()==null?"":session.getCurrentPackage().getName();
        String theme_name = session.getTheme(); 
        String key_name = pkg_name+"."+name+"."+theme_name;
        TextTemplate tplt = (TextTemplate)mCache.getValue(key_name);
        if(tplt==null){
            tplt = session.getTemplateByName(name);
            mCache.setValue(key_name, tplt);
        }
        return tplt;
    }

    public void buildView(BuildSession session, ViewContext view_context) 
        throws IOException, ViewCreationException
    {
        TextTemplate template = view_context.getTemplate();        
        if(template==null){
            ViewComponentPackage pkg = session.getCurrentPackage();
            ViewComponent cpnt = pkg.getComponent(view_context.getView());
            String template_name = cpnt==null?null:cpnt.getDefaultTemplate();
            if(template_name==null)
                template_name = view_context.getView().getName() + TEMPLATE_EXT;
            //template = session.getTemplateByName(template_name);
            template = getTemplate(template_name, session);
        }
        if(template==null){
            CompositeMap view = view_context.getView();
            throw new ViewCreationException("No template defined during view content creation process:"+view==null?"":view.toXML());        
        }
        template.createOutput(session.getWriter(), view_context.getContextMap());
    }
    
    public String[] getBuildSteps( ViewContext context ){
        return TEMPLATE_BASED_SEQUENCE;
    }

}
