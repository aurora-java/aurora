/*
 * Created on 2008-7-2
 */
package aurora.application.util;

import java.util.ResourceBundle;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import aurora.service.ServiceContext;

public class AppUtil {
    
    public static ResourceBundle getResourceBundle( ServiceContext sc ){
        ResourceBundle bundle = (ResourceBundle)sc.getInstanceOfType(ResourceBundle.class);
        return bundle;
    }
    
    public static ResourceBundle getResourceBundle( CompositeMap context ){
        ServiceContext sc = (ServiceContext)DynamicObject.cast(context, ServiceContext.class);
        return getResourceBundle(sc);
    }

}
