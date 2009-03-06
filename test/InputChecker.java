/*
 * Created on 2007-11-9
 */
package test;

import uncertain.composite.CompositeMap;
import uncertain.ocm.OCManager;

public class InputChecker {
    
    public String Name;
    public Integer Deptno;    
    
    OCManager oc_manager;
    
    public InputChecker(OCManager oc_manager){
        this.oc_manager = oc_manager;
    }
    
    public void onValidateInput(CompositeMap param){
        oc_manager.populateObject(param, this);
        if(Name==null)
            throw new IllegalArgumentException("Name is required");
        if(Name.length()<3||Name.length()>10)
            throw new IllegalArgumentException("Name must be 3~10 characters");
        if(Deptno.intValue()<=0)
            throw new IllegalArgumentException("Deptno must be > 0");
    }

}
