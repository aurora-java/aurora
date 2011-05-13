/*
 * Created on 2010-5-24 下午04:30:08
 * $Id$
 */
package aurora.application;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.core.IGlobalInstance;
import uncertain.ocm.IConfigurable;

public class ApplicationConfig extends DynamicObject implements IConfigurable, IApplicationConfig, IGlobalInstance {
    
    public static final String APPLICATION_VIEW_CONFIG = "application-view-config";
    public static final String APPLICATION_SESSION_CONFIG = "application-session-config";

    public void beginConfigure(CompositeMap config) {
        initialize(config);

    }

    public void endConfigure() {

    }
    
    public CompositeMap getApplicationConfig(){
        return super.getObjectContext();
    }
    
    public CompositeMap getApplicationSessionConfig(){
        return getObjectContext().getChild(APPLICATION_SESSION_CONFIG);
    }
    
    public ApplicationViewConfig getApplicationViewConfig(){
        CompositeMap section = getObjectContext().getChild(APPLICATION_VIEW_CONFIG);
        if(section==null)
            return null;
        else
            return (ApplicationViewConfig)DynamicObject.cast(section, ApplicationViewConfig.class);
    }

}
