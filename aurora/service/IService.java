/*
 * Created on 2007-11-23
 */
package aurora.service;

import uncertain.event.Configuration;
import uncertain.proc.Procedure;


/**
 * Defines general behavior that a service must have
 * @author Zhou Fan
 *
 */
public interface IService {
    
    public Configuration getConfig();
    
    public void setServiceContext( ServiceContext context );
    
    public ServiceContext getServiceContext();
    
    public boolean invoke( Procedure proc ) throws Exception;

}
