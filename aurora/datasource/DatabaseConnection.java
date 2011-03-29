package aurora.datasource;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IConfigurable;

public class DatabaseConnection implements IConfigurable {
	String name;
	String driverClass;
	String url;
	String userName;
	String password;	
	boolean pool=true;	
	CompositeMap config;
	public DatabaseConnection(){
		
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
	
	public void beginConfigure(CompositeMap config) {		
		this.config=config;
	}

	public void endConfigure() {	
		
	}	
}
