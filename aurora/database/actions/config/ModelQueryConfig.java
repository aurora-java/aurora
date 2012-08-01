/*
 * Created on 2009-9-10 下午02:53:24
 * Author: Zhou Fan
 */
package aurora.database.actions.config;

import java.util.Map;

public class ModelQueryConfig extends AbstractQueryActionConfig {

    public String getModel() {
        return getString("model");
    }

    public void setModel(String model) {
        putString("model", model);
    }

    public void setParameters(Map params) {
        super.setParameters(params);
        if (getRootPath() == null)
            setRootPath(getModel());
    }
    
    public void setCacheKey( String key ){
        putString("cachekey", key);
    }
}