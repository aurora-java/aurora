package aurora.application.features.cache;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import uncertain.composite.CompositeMap;

public class CacheProviderRegistry {

	public static final String KEY_CACHE_NAME = "cacheName"; 
	public static final String KEY_CACHE_DESC = "cacheDesc";
	public static final String KEY_RELOAD_TYPE = "reloadType";
	public static final String KEY_RELOAD_INTERVAL = "reloadInterval";
	public static final String KEY_RELOAD_TOPIC = "reloadTopic";
	public static final String KEY_RELOAD_MSG = "reloadMessage";
	public static final String KEY_LAST_RELOAD_DATE = "lastReloadDate";
	public static final String KEY_PAST_TIME = "pastTime";
	
	public static Map<String,ICacheProvider> providerMap = new HashMap<String,ICacheProvider>();
	
	public static CompositeMap getAllProvider(){
		SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		CompositeMap result = new CompositeMap("result");
		Set<Entry<String,ICacheProvider>> entrySet = providerMap.entrySet();
		for(Entry<String,ICacheProvider> entry:entrySet){
			ICacheProvider provider = entry.getValue();
			if(provider != null ){
				CompositeMap record = new CompositeMap("record");
				record.put(KEY_CACHE_NAME, entry.getKey());
				record.put(KEY_CACHE_DESC, provider.getCacheDesc());
				record.put(KEY_RELOAD_TOPIC, provider.getReloadTopic());
				record.put(KEY_RELOAD_MSG, provider.getReloadMessage());
				Date date = provider.getLastReloadDate();
				if(date != null){
					String dateStr = dateFormat.format(date);
					record.put(KEY_LAST_RELOAD_DATE, dateStr);
				   long pastTime=((new Date()).getTime()-date.getTime())/1000;//转换成秒
			       record.put(KEY_PAST_TIME, pastTime);
				}else{
					record.put(KEY_LAST_RELOAD_DATE, "");
					record.put(KEY_PAST_TIME, "");
				}
				if(provider instanceof PeriodModeCacheProvider){
					record.put(KEY_RELOAD_TYPE, "periodMode");
					record.put(KEY_RELOAD_INTERVAL, ((PeriodModeCacheProvider)provider).getRefreshInterval());
				}else{
					record.put(KEY_RELOAD_TYPE, "");
					record.put(KEY_RELOAD_INTERVAL, "");
				}
				result.addChild(record);
			}
		}
		return result;
		
	}
	public static void put(String cacheName,ICacheProvider provider){
		providerMap.put(cacheName, provider);
	}
	public static ICacheProvider getProvider(String cacheName){
		return providerMap.get(cacheName);
	}
	public static boolean reloadCache(String cacheName) throws Exception{
		ICacheProvider provider = getProvider(cacheName);
		if(provider == null)
			throw new IllegalArgumentException("cache:"+cacheName+" may not registry its CacheProvider!");
		provider.reload();
		return true;
	}
	public static void remove(String cacheName){
		providerMap.remove(cacheName);
	}
}
