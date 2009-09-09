/*
 * Created on 2009-9-3 上午10:44:40
 * Author: Zhou Fan
 */
package aurora.service;

public interface IServiceFactory {
    
    public IService createService( String service_name ) throws Exception;

}
