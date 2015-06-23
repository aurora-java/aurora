package aurora.plugin.xapool;

import uncertain.composite.CompositeMap;

public class PoolConfig {
	//如果为true在取得连接的同时将校验连接的有效性
	boolean mTestConnectionOnCheckin=true;
	//获得连接的超时时间,如果超过这个时间,会抛出异常，单位毫秒
	int mCheckoutTimeout=100;
	//每隔多少时间检查一下pool中的空闲连接，单位秒
	int mIdleConnectionTestPeriod=120;
	//定义所有连接测试都执行的测试语句
	String mPreferredTestQuery="select 1 from dual";
	CompositeMap config;
	
	public PoolConfig(){
		config=new CompositeMap();
	}
	
	public void setIdleConnectionTestPeriod(Integer value){
		if(value!=null){
			config.put("idleConnectionTestPeriod", value);
		}else{
			config.put("idleConnectionTestPeriod", new Integer(mIdleConnectionTestPeriod));
		}
	}
	
	public Integer getIdleConnectionTestPeriod(){
		return (Integer)config.get("idleConnectionTestPeriod");
	}
	
	public void setCheckoutTimeout(Integer value){
		if(value!=null){
			config.put("checkoutTimeout", value);
		}else{
			config.put("checkoutTimeout", new Integer(mCheckoutTimeout));
		}
	}
	
	public Integer getCheckoutTimeout(){
		return (Integer)config.get("checkoutTimeout");
	}
	
	public void setTestConnectionOnCheckin(Boolean value){
		if(value!=null){
			config.put("testConnectionOnCheckin", value);
		}else{
			config.put("testConnectionOnCheckin", new Boolean(mTestConnectionOnCheckin));
		}
	}
	
	public Boolean getTestConnectionOnCheckin(){
		return (Boolean)config.get("testConnectionOnCheckin");
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
		return (Integer)config.get("maxSize");		
	}
	public void setMaxSize(Integer maxSize) {
		if(maxSize!=null)
			config.put("maxSize", maxSize);			
	}
	public Integer getMinSize() {
		return (Integer)config.get("minSize");			
	}
	public void setMinSize(Integer minSize) {
		if(minSize!=null)
			config.put("minSize", minSize);			
	}	
	
	public String getPreferredTestQuery(){
		return (String)config.get("preferredTestQuery");
	}
	
	public void setPreferredTestQuery(String value){
		if(value!=null)
			config.put("preferredTestQuery", value);
		else 
			config.put("preferredTestQuery", mPreferredTestQuery);
		
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
	public CompositeMap getConfig(){
		return this.config;
	}
}
