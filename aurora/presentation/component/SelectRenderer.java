/*
 * Created on 2007-9-6
 */
package aurora.presentation.component;

import uncertain.composite.CompositeMap;
import uncertain.ocm.ISingleton;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.features.IOptionBuilder;

public class SelectRenderer   implements ISingleton {
    
    public static class SelectOptionRenderer implements IOptionBuilder {
    
        public String createOption( CompositeMap item, Object value, String prompt, boolean is_selected){
            StringBuffer buf = new StringBuffer();
            buf.append("<option value=\"");
            if( value != null) buf.append(value.toString());
            buf.append("\" ");
            if( is_selected) buf.append("selected ");
            buf.append(">");
            if(prompt != null) buf.append(prompt);
            buf.append("\n");
            return buf.toString();
        }
    
    }
    
    public void onCreateOptionBuilder( BuildSession session, ViewContext context ){
        context.setInstance(IOptionBuilder.class, new SelectOptionRenderer() );
    }

}
