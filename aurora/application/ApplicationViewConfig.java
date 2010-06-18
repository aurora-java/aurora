/*
 * Created on 2010-6-18 下午02:30:24
 * $Id$
 */
package aurora.application;

import uncertain.composite.DynamicObject;

/**
 * UI related config section in application config
 */
public class ApplicationViewConfig extends DynamicObject {
    
    public static final String KEY_DEFAULT_TEMPLATE = "defaulttemplate";
    public static final String KEY_DEFAULT_PACKAGE = "defaultpackage";
    
    public String getDefaultPackage(){
        return getString(KEY_DEFAULT_PACKAGE);
    }
    
    public void setDefaultPackage( String pkg ){
        putString(KEY_DEFAULT_PACKAGE, pkg);
    }
    
    public String getDefaultTemplate(){
        return getString(KEY_DEFAULT_TEMPLATE);
    }
    
    public void setDefaultTemplate( String template ){
        putString(KEY_DEFAULT_TEMPLATE, template);
    }

}
