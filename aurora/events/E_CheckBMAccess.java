/*
 * Created on 2010-8-30 下午05:11:24
 * $Id$
 */
package aurora.events;

import aurora.bm.BusinessModel;
import aurora.service.ServiceInstance;

public interface E_CheckBMAccess {
    
    public static final String EVENT_NAME = "CheckBMAccess";
    
    public void onCheckBMAccess( BusinessModel model, String operation_name, ServiceInstance svc )
        throws Exception;

}
