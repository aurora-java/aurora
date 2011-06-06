/*
 * Created on 2011-4-30 下午10:33:54
 * $Id$
 */
package aurora.application.features;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;

import javax.servlet.http.HttpServletResponse;

import uncertain.cache.ICache;
import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.event.EventModel;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.util.resource.ISourceFile;
import uncertain.util.resource.ISourceFileManager;
import aurora.application.config.ScreenConfig;
import aurora.events.E_DetectProcedure;
import aurora.presentation.PresentationManager;
import aurora.presentation.cache.IResponseCacheProvider;
import aurora.service.IService;
import aurora.service.ServiceController;
import aurora.service.http.HttpServiceInstance;

public class CachedScreenListener implements E_DetectProcedure {
    
    public static final String CACHE_KEY = "__screen_cache_key";

    IResponseCacheProvider mCacheProvider;
    ICache mCache;


    public static String getFullKey(IResponseCacheProvider provider,
            HttpServiceInstance svc, String key) {

        File source = svc.getServiceConfigData().getSourceFile();
        if (source == null) {
            ILogger logger = LoggingContext.getLogger(svc.getContextMap(),
                    PresentationManager.LOGGING_TOPIC);
            logger.warning("Can't get source file, thus file last update date can be append to cache key");
            return null;
        }

        key = provider.getFullCacheKey(source, key);
        if (key != null)
            key = TextParser.parse(key, svc.getContextMap());

        return key;
    }
    
    public static void setCacheKey( CompositeMap context, String key ){
        context.put(CACHE_KEY, key);
    }
    
    public static String getCacheKey( CompositeMap context ){
        return context.getString(CACHE_KEY);
    }

    /**
     * @param cacheFactory
     */
    public CachedScreenListener(IResponseCacheProvider provider,
            ISourceFileManager manager) {
        this.mCacheProvider = provider;
        mCache = provider.getCacheForResponse();
        if (mCache == null)
            throw new IllegalStateException("Can't get named cache for screen");
    }

    public static void writeCachedResponse(String contentType,
            HttpServiceInstance svc, String result) throws IOException {
        HttpServletResponse response = svc.getResponse();
        response.setContentType(contentType);
        Writer out = response.getWriter();
        out.write(result);
        out.flush();
    }

    public String getCachedContent(IService service) {
        HttpServiceInstance svc = (HttpServiceInstance) service;
        ScreenConfig screen = ScreenConfig.createScreenConfig(svc
                .getServiceConfigData());
        String key = screen.getCacheKey();
        if (key != null) {
            key = mCacheProvider.getFullCacheKey(key);
            key = TextParser.parse(key, svc.getContextMap());
            Object result = mCache.getValue(key);
            if (result != null)
                return result.toString();
        }
        return null;
    }

    public int preDetectProcedure(IService service) throws Exception {
        if (service instanceof HttpServiceInstance) {
            HttpServiceInstance svc = (HttpServiceInstance) service;
            ScreenConfig screen = ScreenConfig.createScreenConfig(svc
                    .getServiceConfigData());
            boolean cacheEnabled = screen.isCacheEnabled();
            if (cacheEnabled) {
                ILogger logger = LoggingContext.getLogger(svc.getContextMap(),
                        PresentationManager.LOGGING_TOPIC);
                String key = screen.getCacheKey();
                key = getFullKey(mCacheProvider, svc, key);
                if (key != null) {
                    setCacheKey(svc.getContextMap(), key);
                    Object result = mCache.getValue(key);
                    if (result != null) {
                        try {
                            writeCachedResponse(screen.getContentType(), svc,
                                    result.toString());
                            logger.log(
                                    Level.FINE,
                                    "Write cached result to client using key {0}",
                                    new Object[] { key });
                        } finally {
                            ServiceController controller = svc.getController();
                            controller.setContinueFlag(false);
                            svc.getServiceContext().setSuccess(true);
                        }
                        return EventModel.HANDLE_STOP;
                    } else {
                        logger.log(
                                Level.FINE,
                                "Cache miss for key {0}, running normal screen render procedure",
                                new Object[] { key });
                    }
                }else{
                    logger.warning("screen cache key is null, check configuration");
                }

            }
        }
        return EventModel.HANDLE_NORMAL;
    }

    public int onDetectProcedure(IService service) throws Exception {
        // will never happen. just for implementing interface
        return EventModel.HANDLE_NORMAL;
    }

}
