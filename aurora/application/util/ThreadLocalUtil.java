/*
 * Created on 2011-5-23 下午05:05:59
 * $Id$
 */
package aurora.application.util;

import java.util.HashMap;
import java.util.Map;

public class ThreadLocalUtil {
    
    static Map      THREAD_LOCAL_MAP = new HashMap();
    
    public synchronized static void put( String key, Object value ){
        ThreadLocal tl = (ThreadLocal)THREAD_LOCAL_MAP.get(key);
        if(tl==null){
            tl = new ThreadLocal();
            THREAD_LOCAL_MAP.put(key, tl);
        }
        tl.set(value);
    }
    
    public static Object get( String key ){
        ThreadLocal tl = (ThreadLocal)THREAD_LOCAL_MAP.get(key);
        if(tl==null)
            return null;
        return tl.get();
    }
    
    public synchronized static void remove( String key ){
        ThreadLocal tl = (ThreadLocal)THREAD_LOCAL_MAP.get(key);
        if(tl!=null){
            tl.remove();
        }
    }

}
