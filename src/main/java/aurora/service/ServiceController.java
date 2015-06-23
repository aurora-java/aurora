/*
 * Created on 2009-9-1
 */
package aurora.service;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;

public class ServiceController extends DynamicObject {
    
    public static final String KEY_PROCEDURE_NAME = "__procedure_name__";
    public static final String KEY_CONTINUE_FLAG = "__continue_flag__";
    
    public static ServiceController createServiceController( CompositeMap map ){
        ServiceController controller = new ServiceController();
        controller.initialize(map);
        return controller;
    }

    public boolean getContinueFlag(){
        return getBoolean(KEY_CONTINUE_FLAG, true);
    }
    
    public void setContinueFlag( boolean is_continue ){
        putBoolean( KEY_CONTINUE_FLAG, is_continue );
    }
    
    public String getProcedureName(){
        return getString(KEY_PROCEDURE_NAME);
    }
    
    public void setProcedureName( String name ){
        putString(KEY_PROCEDURE_NAME, name);
    }
    
    public void reset(){
        setContinueFlag(true);
        setProcedureName(null);
    }

}
