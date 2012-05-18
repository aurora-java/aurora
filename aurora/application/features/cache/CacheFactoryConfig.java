/*
 * Created on 2011-5-5 ����10:30:42
 * $Id$
 */
package aurora.application.features.cache;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import uncertain.cache.CacheMapping;
import uncertain.cache.CacheWrapper;
import uncertain.cache.ICache;
import uncertain.cache.ICacheFactory;
import uncertain.cache.ICacheReader;
import uncertain.cache.ICacheWriter;
import uncertain.cache.INamedCacheFactory;
import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.core.ConfigurationError;
import uncertain.core.IGlobalInstance;
import uncertain.core.ILifeCycle;
import uncertain.core.UncertainEngine;
import uncertain.event.IContextListener;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;
import uncertain.util.FilePatternFilter;
import uncertain.util.FileUtil;

public class CacheFactoryConfig implements INamedCacheFactory, ILifeCycle {

	public static ICache getNamedCache(IObjectRegistry reg, String name) {
		INamedCacheFactory fact = (INamedCacheFactory) reg.getInstanceOfType(INamedCacheFactory.class);
		if (fact != null) {
			if (!fact.isCacheEnabled(name))
				return null;
			else
				return fact.getNamedCache(name);
		} else
			return null;
	}

	private static final CacheWrapper NOT_ENABLED_CACHE = new CacheWrapper();

	String mName;
	String cacheConfig = "cacheConfig";

	INamedCacheFactory mDefaultCacheFactory;
	String mDefaultCacheFactoryName;
	INamedCacheFactory[] mNamedCacheFactoryArray;
	CacheMapping[] mCacheMappingArray;

	// factory name -> factory instance
	Map mCacheFactoryMap = new HashMap();
	// cache name -> factory instance
	Map mPredefinedCacheMap = new HashMap();

	IObjectRegistry mRegistry;
	ILogger mLogger;
	UncertainEngine mEngine;
	List<ILifeCycle> mLoadedLifeCycleList = new LinkedList<ILifeCycle>();
	Set<ICacheProvider> cacheProviderSet = new HashSet<ICacheProvider>();

	public CacheFactoryConfig() {

	}

	public CacheFactoryConfig(IObjectRegistry reg) {
		this.mRegistry = reg;
		reg.registerInstance(INamedCacheFactory.class, this);
		reg.registerInstance(ICacheFactory.class, this);
	}

	public String getDefaultCacheFactory() {
		return mDefaultCacheFactoryName;
	}

	public void setDefaultCacheFactory(String mDefaultCacheFactory) {
		this.mDefaultCacheFactoryName = mDefaultCacheFactory;
	}

	public ICacheReader getCacheReader() {
		return mDefaultCacheFactory == null ? null : mDefaultCacheFactory.getCacheReader();
	}

	public ICacheWriter getCacheWriter() {
		return mDefaultCacheFactory == null ? null : mDefaultCacheFactory.getCacheWriter();
	}

	public ICache getCache() {
		return mDefaultCacheFactory == null ? null : mDefaultCacheFactory.getCache();
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public ICache getNamedCache(String name) {
		Object o = mPredefinedCacheMap.get(name);
		if (o == null)
			return mDefaultCacheFactory == null ? null : mDefaultCacheFactory.getNamedCache(name);
		else {
			if (NOT_ENABLED_CACHE.equals(o))
				return null;
			else
				return ((INamedCacheFactory) o).getNamedCache(name);
		}
	}

	/*
	 * public void setNamedCache(String name, ICache cache) {
	 * if(mDefaultCacheFactory!=null) mDefaultCacheFactory.setNamedCache(name,
	 * cache); }
	 */
	public void addCacheFactories(INamedCacheFactory[] factories) {
		mNamedCacheFactoryArray = factories;
	}

	public void addCacheMappings(CacheMapping[] mappings) {
		mCacheMappingArray = mappings;
	}

	public boolean startup() {
		mLogger = LoggingContext.getLogger(this.getClass().getCanonicalName(), mRegistry);
		for (int i = 0; i < mNamedCacheFactoryArray.length; i++)
			mCacheFactoryMap.put(mNamedCacheFactoryArray[i].getName(), mNamedCacheFactoryArray[i]);
		for (int i = 0; i < mCacheMappingArray.length; i++) {
			CacheMapping cm = mCacheMappingArray[i];
			INamedCacheFactory fact = (INamedCacheFactory) mCacheFactoryMap.get(cm.getCacheFactory());
			if (fact == null)
				throw new ConfigurationError("Can't find cache factory named " + cm.getCacheFactory());
			if (!cm.getEnabled()) {
				mPredefinedCacheMap.put(cm.getName(), NOT_ENABLED_CACHE);
			} else {
				mPredefinedCacheMap.put(cm.getName(), fact);
			}
		}
		if (mDefaultCacheFactoryName != null) {
			mDefaultCacheFactory = (INamedCacheFactory) mCacheFactoryMap.get(mDefaultCacheFactoryName);
			if (mDefaultCacheFactory == null)
				throw new ConfigurationError("Can't find cache factory named " + mDefaultCacheFactoryName);
		}
		if (cacheConfig != null) {
			if (mRegistry == null)
				throw new IllegalStateException("Field IObjectRegistry can't be null");
			mEngine = (UncertainEngine) mRegistry.getInstanceOfType(UncertainEngine.class);
			if (mEngine == null)
				throw BuiltinExceptionFactory.createInstanceNotFoundException(null, UncertainEngine.class);
			File cacheConfigDir = new File(mEngine.getConfigDirectory(), cacheConfig);
			if (cacheConfigDir.exists()) {
				scanConfigFiles(cacheConfigDir, UncertainEngine.DEFAULT_CONFIG_FILE_PATTERN);
//				String resourcePath = null;
//				try {
//					resourcePath = cacheConfigDir.getCanonicalPath();
//				} catch (IOException e) {
//					throw new RuntimeException(e);
//				}
//				if (resourcePath != null)
//					throw BuiltinExceptionFactory.createResourceLoadException(null, resourcePath, null);
			}
			
		}
		return true;
	}

	public boolean isCacheEnabled(String name) {
		Object o = mPredefinedCacheMap.get(name);
		if (o == null)
			return mDefaultCacheFactory == null ? false : mDefaultCacheFactory.isCacheEnabled(name);
		else {
			if (NOT_ENABLED_CACHE.equals(o))
				return false;
			else
				return true;
		}
	}

	public void shutdown() {
		if (mNamedCacheFactoryArray == null)
			return;
		for (int i = 0; i < mNamedCacheFactoryArray.length; i++) {
			Object o = mNamedCacheFactoryArray[i];
			if (o instanceof ILifeCycle) {
				ILifeCycle s = (ILifeCycle) o;
				s.shutdown();
			}
		}
		if (mLoadedLifeCycleList != null)
			for (ILifeCycle l : mLoadedLifeCycleList) {
				try {
					l.shutdown();
				} catch (Throwable thr) {
					mLogger.log(Level.WARNING, "Error when shuting down instance " + l, thr);
				}
			}

	}

	/*
	 * public void onShutdown(){ shutdown(); }
	 */
	public void setNamedCache(String name, ICache cache) {
		Object o = mPredefinedCacheMap.get(name);
		if (o == null) {
			if (mDefaultCacheFactory != null)
				mDefaultCacheFactory.setNamedCache(name, cache);
		} else {
			if (NOT_ENABLED_CACHE.equals(o))
				return;
			else
				((INamedCacheFactory) o).setNamedCache(name, cache);
		}
	}

	public String getCacheConfig() {
		return cacheConfig;
	}

	public void setCacheConfig(String cacheConfig) {
		this.cacheConfig = cacheConfig;
	}

	private void scanConfigFiles(File dir, String file_pattern) {
		CompositeLoader compositeLoader = CompositeLoader.createInstanceForOCM();
		FilePatternFilter filter = new FilePatternFilter(file_pattern);
		File cfg_files[] = dir.listFiles(filter);
		List file_list = FileUtil.getSortedList(cfg_files);
		Logger console = Logger.getLogger(this.getClass().getCanonicalName());
		if (cfg_files.length > 0) {
			ListIterator fit = file_list.listIterator(cfg_files.length);
			while (fit.hasPrevious()) {
				File file = (File) fit.previous();
				String file_path = file.getAbsolutePath();
				mLogger.log("Loading configuration file " + file_path);
				try {
					CompositeMap config_map = compositeLoader.loadByFullFilePath(file_path);
					Object inst = mEngine.getOcManager().createObject(config_map);
					if (inst == null) {
						console.warning("Can't load initialize config file " + file_path);
						continue;
					}
					if (!(inst instanceof ICacheProvider)) {
						console.warning("config file " + file_path + " is not an ICacheProvider object.");
						continue;
					}
					if (inst instanceof IGlobalInstance)
						mEngine.getObjectRegistry().registerInstance(inst);
					if (inst instanceof IContextListener)
						mEngine.addContextListener((IContextListener) inst);
					if (inst instanceof ILifeCycle) {
						ILifeCycle c = (ILifeCycle) inst;
						if (!mLoadedLifeCycleList.contains(c))
							if (c.startup()) {
								mLoadedLifeCycleList.add(c);
							}
					}
					cacheProviderSet.add((ICacheProvider) inst);
				} catch (Throwable thr) {
					console.warning("Can't load initialize config file " + file_path);
					mEngine.logException("Error when loading configuration file " + file_path, thr);
				}
			}
		}
	}

	public void onInitialize() throws Exception {
		for (ICacheProvider cacheApp : cacheProviderSet) {
			cacheApp.onInitialize();
		}
	}
}
