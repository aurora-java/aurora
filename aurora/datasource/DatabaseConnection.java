package aurora.datasource;

import java.io.ByteArrayInputStream;
import java.util.Enumeration;
import java.util.Properties;

import uncertain.composite.CompositeMap;

public class DatabaseConnection {
	String name;
	String driverClass;
	String url;
	String userName;
	String password;
	String initSql;
	boolean pool = true;
	CompositeMap config = null;

	public String getInitSql() {
		return initSql;
	}

	public void setInitSql(String initSql) {
		this.initSql = initSql;
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

	public CompositeMap getConfig() {
		return config;
	}

	public void setConfig(CompositeMap config) {
		this.config = config;
	}

	public void addProperties(CompositeMap config) throws Exception {
		String key;
		String text = config.getText();
		Properties properties = new Properties();

		ByteArrayInputStream stream = new ByteArrayInputStream(
				text.getBytes("UTF-8"));
		properties.load(stream);

		Enumeration enumn = properties.propertyNames();
		if (enumn != null) {
			this.config = new CompositeMap();
			while (enumn.hasMoreElements()) {
				key = (String) enumn.nextElement();
				this.config.put(key, properties.getProperty(key).trim());
			}
		}
	}
}
