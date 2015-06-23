/*
 * Created on 2010-11-3 下午02:09:53
 * $Id$
 */
package aurora.service.http;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;

public class AutoCrudServiceContext extends DynamicObject {
    
    public static final String KEY_IS_AUTOCRUD_SERVICE = "is_autocrud_service";
    public static final String KEY_REQUESTED_BM_NAME = "requested_bm_name";
    public static final String KEY_REQUESTED_OPERATION = "requested_operation";
    
    public static AutoCrudServiceContext createAutoCrudServiceContext( CompositeMap m ){
        AutoCrudServiceContext ct = new AutoCrudServiceContext();
        ct.initialize(m);
        return ct;
    }

    public String   getRequestedOperation(){
        return getString(KEY_REQUESTED_OPERATION);
    }
    
    public void setRequestedOperation(String op){
        putString(KEY_REQUESTED_OPERATION, op);
    }
    
    public String   getRequestedBM(){
        return getString(KEY_REQUESTED_BM_NAME);
    }
    
    public void setRequestedBM(String op){
        putString(KEY_REQUESTED_BM_NAME, op);
    }
    
    public boolean isAutoCrudService(){
        return getBoolean(KEY_IS_AUTOCRUD_SERVICE, false);
    }
    
    public void setAutoCrudService( boolean flag ){
        putBoolean(KEY_IS_AUTOCRUD_SERVICE, flag);
    }


}
