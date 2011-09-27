/*
 * Created on 2011-9-4 上午09:37:50
 * $Id$
 */
package aurora.application.features.cstm;

import uncertain.composite.CompositeMap;

public interface ICustomizationDataProvider {
    
    public CompositeMap getCustomizationData( String service_name, CompositeMap context );
    
    public boolean getDefaultCustomizationEnabled();

}
