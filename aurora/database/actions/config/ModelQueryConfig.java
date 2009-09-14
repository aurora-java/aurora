/*
 * Created on 2009-9-10 下午02:53:24
 * Author: Zhou Fan
 */
package aurora.database.actions.config;

public class ModelQueryConfig extends AbstractQueryActionConfig {
    
    public String getModel() {
        return getString("model");
    }

    public void setModel(String model) {
        putString("model" , model);
    }

}
