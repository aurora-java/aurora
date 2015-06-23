/*
 * Created on 2007-11-23
 */
package aurora.service;

import uncertain.composite.CompositeMap;
import uncertain.event.IEventDispatcher;
import uncertain.proc.Procedure;


/**
 * Defines general behavior that a service must have
 * @author Zhou Fan
 *
 */
public interface IService {
    
    public IEventDispatcher getConfig();
    
    public void setServiceContext( ServiceContext context );
    
    public ServiceContext getServiceContext();
    
    public boolean invoke( Procedure proc ) throws Exception;
    
    public CompositeMap getServiceConfigData();
    
    /** release all resources allocated */
    public void release();
    
    /** Add an extra IResourceReleaser instance, which can be called in release() method,
     * to release resources allocated by third party.
     */
    public void addResourceReleaser( IResourceReleaser releaser );

}
