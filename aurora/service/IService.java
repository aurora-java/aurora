/*
 * Created on 2007-11-23
 */
package aurora.service;

import java.util.List;
import java.util.Map;

/**
 * Defines general behavior that a service must have
 * @author Zhou Fan
 *
 */
public interface IService {
    
    public void setServiceContext( ServiceContext context );
    
    public ServiceContext getServiceContext();
    
    public void invoke() throws Exception;

}
