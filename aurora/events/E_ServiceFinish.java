/*
 * Created on 2011-9-16 下午03:09:52
 * $Id$
 */
package aurora.events;

import aurora.service.IService;

public interface E_ServiceFinish {
    
    public static final String EVENT_NAME = "ServiceFinish";
    
    public int onServiceFinish( IService   service ) throws Exception;    

}
