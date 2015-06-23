/*
 * Created on 2009-9-10 下午02:32:29
 * Author: Zhou Fan
 */
package aurora.database.actions.config;

import aurora.application.AuroraApplication;
import uncertain.composite.CompositeMap;

public class ActionConfigManager {
    
    public static CompositeMap createActionConfig(String name){
        return new CompositeMap(null, AuroraApplication.AURORA_FRAMEWORK_NAMESPACE, name);
    }
    
    public static CompositeMap createModelAction( String type, String model ){
        CompositeMap conf = new CompositeMap(type);
        conf.put("model", model);
        return conf;
    }
    
    public static CompositeMap createModelUpdate(String model){
        return createModelAction("model-update", model);
    }
    
    public static CompositeMap createModelInsert(String model){
        return createModelAction("model-insert", model);
    }    
    
    public static CompositeMap createModelDelete(String model){
        return createModelAction("model-delete", model);
    }
    
    public static CompositeMap createModelBatchUpdate(String model){
        return createModelAction("model-batch-update", model);
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
