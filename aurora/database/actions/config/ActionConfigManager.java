/*
 * Created on 2009-9-10 下午02:32:29
 * Author: Zhou Fan
 */
package aurora.database.actions.config;

import aurora.application.Version;
import uncertain.composite.CompositeMap;

public class ActionConfigManager {
    
    public static CompositeMap createActionConfig(String name){
        return new CompositeMap(null, Version.AURORA_FRAMEWORK_NAMESPACE, name);
    }
    
    public static ModelQueryConfig createModelQuery(){
        CompositeMap map = createActionConfig("model-query");
        ModelQueryConfig config = new ModelQueryConfig();
        config.initialize(map);
        return config;
    }
    
    public static ModelQueryConfig createModelQuery(String model_name){
        ModelQueryConfig config = createModelQuery();
        config.setModel(model_name);
        return config;
    }

}
