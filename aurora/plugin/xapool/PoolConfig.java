package aurora.plugin.xapool;

import uncertain.composite.CompositeMap;

public class PoolConfig {
	CompositeMap config;
	
	public PoolConfig(){
		config=new CompositeMap();
	}
	
	public Long getExpirationTime() {
		return config.getLong("expirationTime");
	}
	public void setExpirationTime(Long expirationTime) {
		if(expirationTime!=null)
			config.putLong("expirationTime", expirationTime.longValue());		
	}
	public Long getSleepTime() {
		return config.getLong("sleepTime");		
	}
	public void setSleepTime(Long sleepTime) {
		if(sleepTime!=null)
			config.putLong("sleepTime", sleepTime.longValue());		
	}
	public Integer getMaxConn() {
		return config.getInt("maxConn");		
	}
	public void setMaxConn(Integer maxConn) {
		if(maxConn!=null)
			config.putInt("maxConn", maxConn.intValue());			
	}
	public Integer getMinConn() {
		return config.getInt("minConn");			
	}
	public void setMinConn(Integer minConn) {
		if(minConn!=null)
			config.putInt("minConn", minConn.intValue());			
	}
	public Integer getDeadlockMaxWait() {		
		return config.getInt("deadlockMaxWait");		
	}
	public void setDeadlockMaxWait(Integer deadlockMaxWait) {
		if(deadlockMaxWait!=null)
			config.putInt("deadlockMaxWait", deadlockMaxWait.intValue());		
	}
	public Integer getDeadlockRetryWait() {
		return config.getInt("deadlockRetryWait");			
	}
	public void setDeadlockRetryWait(Integer deadlockRetryWait) {
		if(deadlockRetryWait!=null)
			config.putInt("deadlockRetryWait", deadlockRetryWait.intValue());		
	}
	public CompositeMap getConfig(){
		return this.config;
	}
}
