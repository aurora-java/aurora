/*
 * Created on 2009-9-10 下午02:53:24
 * Author: Zhou Fan
 */
package aurora.database.actions.config;

import uncertain.composite.CompositeMap;

public class ModelQueryConfig extends AbstractQueryActionConfig {

    public String getModel() {
        return getString("model");
    }

    public void setModel(String model) {
        putString("model", model);
    }

    public void setParameters(CompositeMap params) {
        super.setParameters(params);
        if (getRootPath() == null)
            setRootPath(getModel());
    }
}