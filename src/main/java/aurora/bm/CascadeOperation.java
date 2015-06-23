/*
 * Created on 2010-9-9 下午09:23:39
 * $Id$
 */
package aurora.bm;

import java.util.HashSet;
import java.util.Set;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;

/**
 * Defines  
 */
public class CascadeOperation extends DynamicObject {
    
    public static final String KEY_INPUT_PATH = "inputpath";
    public static final String KEY_MODEL = "model";
    
    Set mEnabledOperations;
    
    public static CascadeOperation createCascadeOperation( CompositeMap context ){
        CascadeOperation op = new CascadeOperation();
        op.initialize(context);
        return op;
    }

    public String   getModel(){
        return getString(KEY_MODEL);
    }
    
    public void setModel(String model){
        putString(KEY_MODEL, model);
    }
    
    public String getInputPath(){
        return getString(KEY_INPUT_PATH);
    }
    
    public void setInputPath(String path){
        putString(KEY_INPUT_PATH, path);
    }
    
    public String getOperations(){
        return getString("operations");
    }
    
    public void setOperations( String ops ){
        putString("operations", ops );
        makeReady();
    }
    
    public void makeReady(){
        if(mEnabledOperations!=null)
            mEnabledOperations.clear();        
        String ops = getOperations();
        if(ops!=null){
            if(mEnabledOperations==null)
                mEnabledOperations = new HashSet();
            String[] op_array = ops.split(",");
            for(int i=0; i<op_array.length; i++){
                String op = op_array[i].trim().toLowerCase();
                mEnabledOperations.add(op);
            }
        }else{
            mEnabledOperations = null;
        }
    }

    public DynamicObject initialize(CompositeMap context) {
        super.initialize(context);
        makeReady();
        return this;
    }
    
    public Set getEnabledOperations(){
        return mEnabledOperations;
    }

}
