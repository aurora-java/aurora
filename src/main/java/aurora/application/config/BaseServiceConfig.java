/*
 * Created on 2009-9-15 下午06:34:13
 * Author: Zhou Fan
 */
package aurora.application.config;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.event.Configuration;
import aurora.application.AuroraApplication;

public class BaseServiceConfig extends DynamicObject {

    public static final String KEY_PARAMETER = "parameter";
    public static final String KEY_INIT_PROCEDURE = "init-procedure";
    
    Configuration       mServiceConfig;
    
    /*
    public static BaseServiceConfig createServiceConfig(CompositeMap map) {
        BaseServiceConfig cfg = new BaseServiceConfig();
        cfg.initialize(map);
        return cfg;
    }
    */
    
    public void setConfiguration( Configuration config ){
        mServiceConfig = config;
    }

    public CompositeMap getInitProcedureConfig() {
        return getChildNotNull(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE,KEY_INIT_PROCEDURE);
    }
    
    public CompositeMap getParameterConfig(){
        return object_context.getChild(KEY_PARAMETER);
    }
    
    public void addInitProcedureAction( CompositeMap config ){
        CompositeMap init_config = getObjectContext().getChild(KEY_INIT_PROCEDURE);
        if(init_config==null){
            init_config = object_context.createChild(KEY_INIT_PROCEDURE);
            init_config.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
            if(mServiceConfig!=null)
                mServiceConfig.loadConfig(init_config);
        }
        init_config.addChild(config);
        return;
    }

}
