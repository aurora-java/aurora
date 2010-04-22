/*
 * Created on 2007-11-23
 */
package aurora.service;


/**
 * Defines general behavior that a service must have
 * @author Zhou Fan
 *
 */
public interface IService {
    
    public void setServiceContext( ServiceContext context );
    
    public ServiceContext getServiceContext();
    
    public boolean invoke() throws Exception;

}
