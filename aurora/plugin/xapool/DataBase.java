package aurora.plugin.xapool;
public class DataBase {
	String name;
	String driverClass;
	String url;
	String userName;
	String password;	
	boolean pool=true;
	Long expirationTime;
	Long sleepTime;
	Integer maxConn;
	Integer minConn;
	Integer deadlockMaxWait;
	Integer deadlockRetryWait;
	public DataBase(){
		
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDriverClass() {
		return driverClass;
	}
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean getPool() {
		return pool;
	}
	public void setPool(boolean pool) {
		this.pool = pool;
	}
	public Long getExpirationTime() {
		return expirationTime;
	}
	public void setExpirationTime(Long expirationTime) {
		this.expirationTime = expirationTime;
	}
	public Long getSleepTime() {
		return sleepTime;
	}
	public void setSleepTime(Long sleepTime) {
		this.sleepTime = sleepTime;
	}
	public Integer getMaxConn() {
		return maxConn;
	}
	public void setMaxConn(Integer maxConn) {
		this.maxConn = maxConn;
	}
	public Integer getMinConn() {
		return minConn;
	}
	public void setMinConn(Integer minConn) {
		this.minConn = minConn;
	}
	public Integer getDeadlockMaxWait() {
		return deadlockMaxWait;
	}
	public void setDeadlockMaxWait(Integer deadlockMaxWait) {
		this.deadlockMaxWait = deadlockMaxWait;
	}
	public Integer getDeadlockRetryWait() {
		return deadlockRetryWait;
	}
	public void setDeadlockRetryWait(Integer deadlockRetryWait) {
		this.deadlockRetryWait = deadlockRetryWait;
	}	
}
