package aurora.plugin.c3p0;

import java.util.HashMap;

public class PoolConfig {
	//如果为true在取得连接的同时将校验连接的有效性
	boolean mTestConnectionOnCheckin=true;
	//获得连接的超时时间,如果超过这个时间,会抛出异常，单位毫秒
	int mCheckoutTimeout=100;
	//每隔多少时间检查一下pool中的空闲连接，单位秒
	int mIdleConnectionTestPeriod=120;
	//定义所有连接测试都执行的测试语句
	String mPreferredTestQuery="select 1 from dual";
	HashMap config;
	
	public PoolConfig(){
		config=new HashMap();
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
	
	public String getPreferredTestQuery(){
		return (String)config.get("preferredTestQuery");
	}
	
	public void setPreferredTestQuery(String value){
		if(value!=null)
			config.put("preferredTestQuery", value);
		else 
			config.put("preferredTestQuery", mPreferredTestQuery);
		
	}

	public HashMap getConfig(){
		return this.config;
	}
}
