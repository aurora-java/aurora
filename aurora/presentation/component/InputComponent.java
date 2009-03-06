/*
 * Created on 2007-8-22
 */
package aurora.presentation.component;

import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.ocm.ISingleton;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

public class InputComponent  implements ISingleton {
    
    private static final String KEY_NAME = "name";

    public void onCreateViewContent( BuildSession session, ViewContext context ){
        context.transferAttribute(KEY_NAME, true);
    }

}
