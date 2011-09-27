/*
 * Created on 2011-5-23 10:52:17
 * $Id$
 */
package aurora.service.lock;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;

import aurora.service.IResourceReleaser;
import aurora.service.ServiceContext;

import uncertain.core.IGlobalInstance;
import uncertain.mbean.IMBeanNameProvider;
import uncertain.mbean.IMBeanRegister;
import uncertain.mbean.IMBeanRegistrable;

/**
 * Implements a simple session level resource locker.
 * Lock can be add on any string resources with a string session id.
 * Timeout can be specified for lock, so that if the client failed to unlock resource,
 * it can be automatically released after specified timeout period.
 */
public class ServiceSessionLock implements Serializable, IServiceSessionLock, IGlobalInstance, IMBeanRegistrable, ServiceSessionLockMBean {
    
    public static class Unlocker implements IResourceReleaser {
        IServiceSessionLock     mLock;
        String                  mSessionID;
        String                  mService;
        
        /**
         * @param mLock
         * @param mSessionID
         * @param mService
         */
        public Unlocker(IServiceSessionLock mLock, String mSessionID,
                String mService) {
            super();
            this.mLock = mLock;
            this.mSessionID = mSessionID;
            this.mService = mService;
        }

        public void doRelease(ServiceContext context) {
            mLock.unlock(mSessionID, mService);
        }
        
    }
    
    public static class ResourceLock {
        
        long     mExpireTime;
        String   mKey;
        
        /**
         * @param expireTime
         * @param key
         */
        public ResourceLock(String key, long expireTime) {
            super();
            this.mExpireTime = expireTime;
            this.mKey = key;
        }

        public int hashCode() {
            return mKey.hashCode();
        }

        public boolean equals(Object obj) {
            if(obj==null)
                return false;
            if(obj instanceof String)
                return mKey.equals((String)obj);
            if(obj instanceof ResourceLock)
                return mKey.equals(((ResourceLock)obj).mKey);
            return false;
        }

        public String toString() {
            return "{"+mKey+":"+ new java.util.Date(mExpireTime) + "}";
        }

    }
    
    public class TimeoutChecker extends Thread {

        public void run() {
            /*
            while(mIsRunning){
                if(mResourceLockMap.size()==0)
                    try{
                        sleep(100);
                    }catch(InterruptedException ex){
                        //ex.printStackTrace();
                    }
                Iterator it = mResourceLockMap.values().iterator();
                while(it.hasNext()){
                    ResourceLock lock = (ResourceLock)it.next();
                    if(lock.mExpireTime>0 && System.currentTimeMillis()>lock.mExpireTime)
                        mResourceLockMap.remove(lock);
                }
            }
            */
        }
        
    }
    
    Map                 mResourceLockMap;
    boolean             mIsRunning = true;
    TimeoutChecker      mCheckThread;
    
    public ServiceSessionLock(){
        start();
    }
    
    public static String getKey( String session_id, String resource ){
        return session_id + "." + resource;
    }
    
    /*
    private void log(String msg){
        System.out.println(new Date()+" "+ Thread.currentThread().getName()+" "+msg);
    }
    */
    
    public void lock( String session_id, String resource, long timeout){
        ResourceLock lock = new ResourceLock( getKey(session_id, resource), (timeout>0?System.currentTimeMillis() + timeout:0));
        mResourceLockMap.put(lock.mKey, lock);
        //log(lock.mKey+"===>>>lock");
    }
    
    public void unlock( String session_id, String resource){
        mResourceLockMap.remove( getKey(session_id, resource));
        //log(getKey(session_id, resource) + "<<<===unlock");
    }
    
    public boolean islocked( String session_id, String resource){
        boolean locked = mResourceLockMap.containsKey( getKey(session_id, resource) );
        //log(getKey(session_id, resource)+" islocked="+locked);
        return locked;
    }
    
    public void shutdown(){
        mIsRunning = false;
        mResourceLockMap.clear();
    }
    
    public void start(){
        mIsRunning = true;
        mResourceLockMap = new HashMap();
        //mCheckThread = new TimeoutChecker();
        //mCheckThread.start();
    }
    
    public String showAllLocks(){
        synchronized(mResourceLockMap){
            return mResourceLockMap.values().toString();
        }
    }
    
    public int getLockCount(){
        return mResourceLockMap.size();
    }
    
    public void clear(){
        synchronized(mResourceLockMap){
            mResourceLockMap.clear();
        }
    }

    public void registerMBean(IMBeanRegister register,
            IMBeanNameProvider name_provider)
            throws MalformedObjectNameException,
            InstanceAlreadyExistsException, MBeanRegistrationException,
            NotCompliantMBeanException {
       String name = name_provider.getMBeanName("Application", "name=ServiceSessionLock");
       register.register(name, this);
    }

}
