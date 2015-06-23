/*
 * Created on 2012-12-27 16:29:03
 * $Id$
 */
package aurora.bm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import uncertain.cache.CacheBuiltinExceptionFactory;
import uncertain.cache.ICache;
import uncertain.cache.INamedCacheFactory;
import uncertain.composite.CompositeMap;
import uncertain.core.IGlobalInstance;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.AbstractLocatableObject;

public class CacheBasedBmAccessCheckerFactory extends AbstractLocatableObject
		implements IBusinessModelAccessCheckerFactory, IGlobalInstance {

	INamedCacheFactory mCacheFactory;
	IModelFactory mModelFactory;
	ICache mBmDataCache;

	String mCacheName;
	String menuCachePath;
	String menuIdKey;

	public CacheBasedBmAccessCheckerFactory(IModelFactory mf,
			INamedCacheFactory fact) {
		this.mCacheFactory = fact;
		this.mModelFactory = mf;
	}

	public void onInitialize() {
		if (menuCachePath == null)
			throw BuiltinExceptionFactory.createAttributeMissing(this,
					"menuCachePath");
		if (menuIdKey == null)
			throw BuiltinExceptionFactory.createAttributeMissing(this,
					"menuIdKey");
		if (mCacheName == null)
			throw BuiltinExceptionFactory.createAttributeMissing(this,
					"cacheName");
		mBmDataCache = mCacheFactory.getNamedCache(mCacheName);
		if (mBmDataCache == null)
			throw CacheBuiltinExceptionFactory.createNamedCacheNotFound(this,
					mCacheName);

	}

	public IBusinessModelAccessChecker getChecker(String model_name,
			CompositeMap session_context) throws Exception {
		BusinessModel bm = mModelFactory.getModelForRead(model_name);
		assert bm != null;
		BusinessModel bm_for_check = bm.getModelForAccessCheck();
		if (!bm_for_check.getNeedAccessControl())
			return DefaultAccessChecker.ALWAYS_ALLOW;
		CompositeMap userFunctionMap = (CompositeMap) session_context
				.getObject(menuCachePath);
		ArrayList<Object> funtionIdList = new ArrayList<Object>();
		if (userFunctionMap != null) {
			@SuppressWarnings("unchecked")
			List<CompositeMap> l = userFunctionMap.getChildsNotNull();
			for (CompositeMap m : l) {
				funtionIdList.add(m.get(menuIdKey));
			}
		}
		return new BmAccessChecker(model_name, funtionIdList, mBmDataCache);
	}

	public String getCacheName() {
		return mCacheName;
	}

	public void setCacheName(String cacheName) {
		this.mCacheName = cacheName;
	}

	public String getMenuCachePath() {
		return menuCachePath;
	}

	public void setMenuCachePath(String menuCachePath) {
		this.menuCachePath = menuCachePath;
	}

	public String getMenuIdKey() {
		return menuIdKey;
	}

	public void setMenuIdKey(String menuIdKey) {
		this.menuIdKey = menuIdKey;
	}

	static class BmAccessChecker implements IBusinessModelAccessChecker {

		private ArrayList<Object> funtionIdList;
		private ICache cache;
		private String bm_name;
		private HashMap<String, Boolean> hashMap = new HashMap<String, Boolean>();

		public BmAccessChecker(String bm_name, ArrayList<Object> funtionIdList,
				ICache cache) {
			this.bm_name = bm_name;
			this.funtionIdList = funtionIdList;
			this.cache = cache;
		}

		@Override
		public boolean canPerformOperation(String operation) {
			Boolean can = hashMap.get(operation);
			if (can == null) {
				can = false;
				for (Object id : funtionIdList) {
					String key = id.toString() + "." + bm_name;
					CompositeMap m = (CompositeMap) cache.getValue(key);
					if (m != null && "Y".equals(m.getString(operation))) {
						can = true;
						break;
					}
				}
				hashMap.put(operation, can);
			}
			return can;
		}
	}

}
