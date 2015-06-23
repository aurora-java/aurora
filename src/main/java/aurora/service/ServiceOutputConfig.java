/*
 * Created on 2009-12-9 下午02:41:23
 * Author: Zhou Fan
 */
package aurora.service;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;

public class ServiceOutputConfig extends DynamicObject {
    
    public static final String KEY_ARRAYS = "arrays";

    public static final String KEY_OUTPUT = "output";
    
    public static final String KEY_SERVICE_OUTPUT = "service-output";
    
    public static ServiceOutputConfig getInstance( CompositeMap map ){
        ServiceOutputConfig soc = new ServiceOutputConfig();
        soc.initialize(map);
        return soc;
    }

    public String getOutput() {
        return super.getString(KEY_OUTPUT);
    }

    public void setOutput(String output) {
        super.putString(KEY_OUTPUT, output);
    }
    
    public String getArrays(){
        return getString(KEY_ARRAYS);
    }
    
    public void setArrays(String arrays){
        putString(KEY_ARRAYS, arrays);
    }

}
