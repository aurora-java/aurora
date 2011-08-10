/*
 * Created on 2008-7-2
 */
package aurora.application.util;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.ocm.IObjectRegistry;
import aurora.application.ApplicationConfig;
import aurora.application.IApplicationConfig;
import aurora.application.ISessionInfoProvider;
import aurora.i18n.ILocalizedMessageProvider;
import aurora.i18n.IMessageProvider;

public class LanguageUtil {
    
    public static final String DEFAULT_LANG_PATH = "/session/@lang";
    
    /** get session's language parameter path defined by application config file. return default
     * language path '/session/@lang' if not defined
     * @param reg
     * @return
     */
    public static String getLanguagePath( IObjectRegistry reg ){
        /*
        ApplicationConfig cfg = (ApplicationConfig)reg.getInstanceOfType(IApplicationConfig.class);
        if(cfg!=null){
            CompositeMap session_config = cfg.getApplicationSessionConfig();
            if(session_config!=null){
                String p = session_config.getString("language_path");
                if(p!=null)
                    return p;
            }
        }
        */
        ISessionInfoProvider sp = (ISessionInfoProvider)reg.getInstanceOfType(ISessionInfoProvider.class);
        if(sp!=null){
            return sp.getUserLanguagePath();
        }
        return DEFAULT_LANG_PATH;
    }
    
    public static String getSessionLanguage( IObjectRegistry reg, CompositeMap context ){
        String path = getLanguagePath(reg);
        Object obj = context.getObject(path);
        return obj==null?null:obj.toString();
    }
    
    public static ILocalizedMessageProvider getLocalizedMessageProvider( IObjectRegistry reg, CompositeMap context ){
        String lang = getSessionLanguage(reg, context);
        IMessageProvider mp = (IMessageProvider)reg.getInstanceOfType(IMessageProvider.class);
        if(mp!=null)
            return mp.getLocalizedMessageProvider(lang);
        return null;
    }
    
    public static String getTranslatedMessage( IObjectRegistry reg, String msg_text, CompositeMap context ){
        ILocalizedMessageProvider mp = getLocalizedMessageProvider( reg, context);
        return getTranslatedMessage(mp, msg_text, context);
        
    }    
    
    public static String getTranslatedMessage( ILocalizedMessageProvider mp, String msg_text, CompositeMap context ){
        if(msg_text==null)
            return null;
        if(mp!=null){
            String s = mp.getMessage(msg_text);
            if(s!=null)
                msg_text = s;
        }
        if(msg_text.indexOf('$')>=0)
            msg_text = TextParser.parse(msg_text, context);
        return msg_text;        
    }
    
    /*
    public static ResourceBundle getResourceBundle( ServiceContext sc ){
        ResourceBundle bundle = (ResourceBundle)sc.getInstanceOfType(ResourceBundle.class);
        return bundle;
    }
    
    public static ResourceBundle getResourceBundle( CompositeMap context ){
        ServiceContext sc = (ServiceContext)DynamicObject.cast(context, ServiceContext.class);
        return getResourceBundle(sc);
    }
    */

}
