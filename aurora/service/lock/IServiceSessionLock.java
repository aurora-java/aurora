/*
 * Created on 2011-5-23 下午11:57:49
 * $Id$
 */
package aurora.service.lock;

public interface IServiceSessionLock {

    public void lock(String session_id, String resource, long timeout);

    public void unlock(String session_id, String resource);

    public boolean islocked(String session_id, String resource);

}