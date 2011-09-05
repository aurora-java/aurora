/*
 * Created on 2011-9-4 下午10:00:37
 * $Id$
 */
package aurora.service;

import uncertain.composite.CompositeMap;

public interface IConfigurableService {
    
    public void setServiceConfigData( CompositeMap configMap, boolean parse );
    
    public boolean isConfigParsed();
    
    public void parseConfig();

}
