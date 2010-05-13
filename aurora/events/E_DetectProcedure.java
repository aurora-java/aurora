/*
 * Created on 2010-5-11 下午04:45:43
 * $Id$
 */
package aurora.events;

import aurora.service.IService;

/** fires before service got invoked to determine which procedure should be run 
 *  Parameter: IService 
 */

public interface E_DetectProcedure {
    
    public static final String EVENT_NAME = "DetectProcedure";
    
    public int onDetectProcedure( IService service ) throws Exception;

}
