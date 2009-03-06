/*
 * Created on 2007-8-16 ÏÂÎç03:49:35
 */
package aurora.presentation.features;

import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.ocm.ISingleton;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

/**
 * Create 'value' property in ViewContext, based on databinding properties
 * Properties implemented: Value, DataField
 * @author Zhou Fan
 *
 */
public class FieldBinding  implements ISingleton {
   
    public static Object getFieldValue( ViewContext context ){
        CompositeMap model = context.getModel();
        CompositeMap view = context.getView();
        Object value = null;
        String datavalue = view.getString(DataBinding.KEY_VALUE);
        if(datavalue!=null){
            value = context.parseString(datavalue, model);
        }else{        
            String datafield = view.getString(DataBinding.KEY_DATAFIELD);
            if(datafield!=null && model!=null){
                value = model.getObject(datafield);
            }
        }
        return value;
        
    }
    
    public void onCreateViewContent( BuildSession session, ViewContext context ){
        Map map = context.getMap();
        Object value = getFieldValue(context);
        if(value!=null) map.put(DataBinding.KEY_VALUE, value);        
    }    

}
