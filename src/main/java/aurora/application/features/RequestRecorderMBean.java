/*
 * Created on 2011-9-23 上午11:40:31
 * $Id$
 */
package aurora.application.features;

import java.util.Date;

public interface RequestRecorderMBean {

    public int getCurrentQueueSize();

    public int getMaxQueueSize();

    /**  */
    public Date getMaxQueueSizeTime();
    
    /** Total request processed */
    public long getRequestCount();
    
    /** Total time spent in processing request, in millisecond */ 
    public long getTotalProcessTime();
    
    /** Average time per request processing, in millisecond */
    public double getAverageProcessTime();  
    
    public long getProcessedCount();    

}