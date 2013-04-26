/*
 * Created on 2008-5-9
 */
package aurora.bm;

import aurora.application.AuroraApplication;
import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;

public class Reference extends DynamicObject {
    
    public static final String KEY_EXPRESSION = "expression";
    public static final String KEY_FOREIGN_FIELD = "foreignfield";
    public static final String KEY_LOCAL_FIELD = "localfield";
    public static final String REFERENCE = "reference";
    
    public static Reference getInstance( CompositeMap context ){
        Reference r = new Reference();
        r.initialize(context);
        return r;
    }
    public static Reference createReference(){
        CompositeMap m = new CompositeMap();
        m.setName("reference");
        m.setNameSpaceURI(AuroraApplication.AURORA_BUSINESS_MODEL_NAMESPACE);
        Reference r = new Reference();
        r.initialize(m);
        return r;
    }
    
    public String getLocalField(){
        return getString(KEY_LOCAL_FIELD);
    }
    
    public void setLocalField(String field_name){
        putString(KEY_LOCAL_FIELD, field_name);
    }
    
    public String getForeignField(){
        return getString(KEY_FOREIGN_FIELD);
    }
    
    public void setForeignField(String field_name){
        putString(KEY_FOREIGN_FIELD, field_name);
    }
    
    public String getExpression(){
        return getString(KEY_EXPRESSION);
    }
    
    public void setExpression(String exp){
        putString(KEY_EXPRESSION,exp);
    }
    

}
