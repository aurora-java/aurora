/*
 * Created on 2010-5-11 下午04:47:01
 * $Id$
 */
package aurora.events;

import aurora.service.IService;

/**
 * fires before load service config
 * Parameter: IService (ServiceInstance)
 */
public interface E_PrepareServiceConfig {
    
    public static final String EVT_PREPARE_SERVICE_CONFIG = "PrepareServiceConfig";
    
    public int onPrepareServiceConfig( IService service );
}
