package aurora.application.features.cache;

import java.util.Date;

import uncertain.cache.ICache;


public interface ICacheProvider {
	
	public enum VALUE_TYPE{value,valueSet,record,recordSet};
	
	public void onInitialize() throws Exception;

	public String getKey();

	public void reload() throws Exception;
	
	public String getReloadTopic();
	
	public String getReloadMessage();
	
	public Date getLastReloadDate();
	
	public void writeLock();

	public void writeUnLock();

	public void readLock();

	public void readUnLock();

	public String getType();

	public String getValue();

	public ICache getCache();
	
	public String getCacheName();
	
	public String getCacheDesc();

}
