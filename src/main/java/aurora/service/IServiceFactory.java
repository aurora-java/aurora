/*
 * Created on 2009-9-3 上午10:44:40
 * Author: Zhou Fan
 */
package aurora.service;

import uncertain.composite.CompositeMap;

public interface IServiceFactory {
    
    public IService createService( CompositeMap context );
    
    public IService createService( String service_name, CompositeMap context );
    
    public void beginService( CompositeMap context );

    public void finishService( CompositeMap context );
    
}
