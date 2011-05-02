/*
 * Created on 2009-9-15 下午06:34:13
 * Author: Zhou Fan
 */
package aurora.application.config;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.composite.DynamicObject;

public class ScreenConfig extends BaseServiceConfig {

    public static final String KEY_CONTENT_TYPE = "contenttype";
    public static final String KEY_CACHE_KEY = "cachekey";
    public static final String KEY_VIEW = "view";
    public static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";
    
    public static ScreenConfig createScreenConfig(CompositeMap map) {
        ScreenConfig cfg = new ScreenConfig();
        cfg.initialize(map);
        return cfg;
    }

/*
    protected CompositeMap getChildNotNull(String name) {
        CompositeMap child = object_context.getChild(name);
        if (child == null)
            child = object_context.createChild(name);
        return child;
    }
*/

    public CompositeMap getViewConfig() {
        return object_context.getChild(KEY_VIEW);
    }
    
    public CompositeMap getDataSetsConfig(){
        return CompositeUtil.findChild(object_context, "dataSets");
    }
    
    public String getCacheKey(){
        return getString(KEY_CACHE_KEY);
    }
    
    public void setCacheKey(String key){
        putString(KEY_CACHE_KEY, key);
    }
    
    public String getContentType(){
        return getString(KEY_CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
    }
    
    public void setContentType(String type){
        putString(KEY_CONTENT_TYPE, type);
    }

}
