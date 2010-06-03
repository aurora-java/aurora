/*
 * Created on 2010-5-24 下午04:30:08
 * $Id$
 */
package aurora.application;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IConfigurable;

public class ApplicationConfig implements IConfigurable, IApplicationConfig {
    
    CompositeMap    mConfig;

    public void beginConfigure(CompositeMap config) {
        mConfig = config;

    }

    public void endConfigure() {

    }
    
    public CompositeMap getApplicationConfig(){
        return mConfig;
    }

}
