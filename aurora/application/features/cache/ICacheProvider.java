package aurora.application.features.cache;

import java.util.Date;

import uncertain.cache.ICacheManager;


public interface ICacheProvider extends ICacheManager {
	
	public enum VALUE_TYPE{value,valueSet,record,recordSet};

	public String getKey();

	public String getType();

	public String getValue();
	
	public void reload();
	
	public String getReloadTopic();
	
	public String getReloadMessage();
	
	public Date getLastReloadDate();
	
/*	public void writeLock();

	public void writeUnLock();

	public void readLock();

	public void readUnLock();*/



}
