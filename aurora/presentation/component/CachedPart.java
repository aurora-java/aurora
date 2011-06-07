/*
 * Created on 2011-4-30 上午01:00:43
 * $Id$
 */
package aurora.presentation.component;

import java.io.IOException;
import java.io.Writer;

import uncertain.cache.ICache;
import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.core.ConfigurationError;
import aurora.application.features.CachedScreenListener;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;
import aurora.presentation.cache.IResponseCacheProvider;

/*
 *         <!--
        <a:cached-part cacheKey="InitialScripts">
            ...
            any view content
            ...
        </a:cached-part>
        -->
 */
public class CachedPart implements IViewBuilder {
    
    IResponseCacheProvider  mCacheProvider;
    ICache                  mCache;
    

    /**
     * @param mCacheProvider
     */
    public CachedPart(IResponseCacheProvider mCacheProvider) {
        this.mCacheProvider = mCacheProvider;
        mCache = mCacheProvider.getCacheForResponse();
    }

    public void buildView(BuildSession session, ViewContext view_context)
            throws IOException, ViewCreationException {
       CompositeMap context = view_context.getModel().getRoot();
       CompositeMap view = view_context.getView();
       String key = view.getString("cachekey");
       if(key==null)
           throw new ConfigurationError(view, "Must set cacheKey property");
       
       String screen_key = CachedScreenListener.getCacheKey(context);
       
       key =  TextParser.parse(key, view_context.getModel());
       if(screen_key!=null)
           key = screen_key + key;
       Object result = mCache.getValue(key);
       Writer out = session.getWriter(); 
       if(result!=null){
           out.write(result.toString());
       }else{
           try{
               String str = session.buildViewsAsString(view_context.getModel(), view.getChilds());
               mCache.setValue(key, str);
               out.write(str);
           }catch(Exception ex){
               throw new ViewCreationException(ex);
           }
       }
       out.flush();
    }

    public String[] getBuildSteps(ViewContext context) {
        return null;
    }

}
