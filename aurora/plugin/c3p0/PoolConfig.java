package aurora.plugin.c3p0;

import java.util.HashMap;

public class PoolConfig {
	HashMap config;
	
	public PoolConfig(){
		config=new HashMap();
	}
	
//	public Long getExpirationTime() {
//		return (Long)config.get("expirationTime");
//	}
//	public void setExpirationTime(Long expirationTime) {
//		if(expirationTime!=null)
//			config.put("expirationTime", expirationTime);		
//	}
//	public Long getSleepTime() {
//		return (Long)config.get("sleepTime");		
//	}
//	public void setSleepTime(Long sleepTime) {
//		if(sleepTime!=null)
//			config.put("sleepTime", sleepTime);		
//	}
	public Integer getMaxSize() {
		return (Integer)config.get("maxPoolSize");		
	}
	public void setMaxSize(Integer maxSize) {
		if(maxSize!=null)
			config.put("maxPoolSize", maxSize);			
	}
	public Integer getMinSize() {
		return (Integer)config.get("minPoolSize");			
	}
	public void setMinSize(Integer minSize) {
		if(minSize!=null)
			config.put("minPoolSize", minSize);			
	}
//	public Integer getDeadlockMaxWait() {		
//		return (Integer)config.get("deadlockMaxWait");		
//	}
//	public void setDeadlockMaxWait(Integer deadlockMaxWait) {
//		if(deadlockMaxWait!=null)
//			config.put("deadlockMaxWait", deadlockMaxWait);		
//	}
//	public Integer getDeadlockRetryWait() {
//		return (Integer)config.get("deadlockRetryWait");			
//	}
//	public void setDeadlockRetryWait(Integer deadlockRetryWait) {
//		if(deadlockRetryWait!=null)
//			config.put("deadlockRetryWait", deadlockRetryWait);		
//	}
	public HashMap getConfig(){
		return this.config;
	}
}
