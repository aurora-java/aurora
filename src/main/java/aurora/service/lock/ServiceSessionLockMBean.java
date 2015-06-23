/*
 * Created on 2011-9-19 下午02:16:10
 * $Id$
 */
package aurora.service.lock;

public interface ServiceSessionLockMBean {
    
    public String showAllLocks();
    
    public int getLockCount();
    
    public void clear();

}
