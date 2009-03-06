/*
 * Created on 2007-9-6
 */
package aurora.presentation.features;

import uncertain.composite.CompositeMap;

public interface IOptionBuilder {
    
    public String createOption( CompositeMap item, Object value, String prompt, boolean is_select);
    
}