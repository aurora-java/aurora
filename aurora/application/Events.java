/*
 * Created on 2010-4-28 下午12:15:53
 * $Id$
 */
package aurora.application;

/**
 * Define aurora builtin event constant
 */
public class Events {
    
    /** fires before service got invoked to determine which procedure should be run 
     *  Parameter: IService 
     */
    public static final String EVT_DETECT_PROCEDURE = "DetectProcedure";
    
    /**
     * fires before load service config
     * Parameter: IService (ServiceInstance)
     */
    public static final String EVT_PREPARE_SERVICE_CONFIG = "PrepareServiceConfig";

}
